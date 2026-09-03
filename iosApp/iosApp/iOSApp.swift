import SwiftUI
import ComposeApp
import UserNotifications
import FirebaseCore
import FirebaseMessaging

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}

class AppDelegate: NSObject, UIApplicationDelegate, UNUserNotificationCenterDelegate, MessagingDelegate {
    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {
        // Khởi tạo Firebase
        FirebaseApp.configure()

        // Đặt delegate cho Messaging
        Messaging.messaging().delegate = self

        UNUserNotificationCenter.current().delegate = self

        // If launched from a notification tap
        if let notification = launchOptions?[.remoteNotification] as? [String: Any] {
            processNotificationData(notification)
        }

        requestNotificationAuthorization()

        // Lắng nghe notification từ Kotlin để present UIActivityViewController
        // (Kotlin/Native không thể set popoverPresentationController.sourceView trực tiếp)
        NotificationCenter.default.addObserver(
            forName: NSNotification.Name("CskhPresentShareSheet"),
            object: nil,
            queue: .main
        ) { [weak self] notification in
            guard let filePath = notification.userInfo?["filePath"] as? String else { return }
            self?.presentShareSheet(filePath: filePath)
        }

        // Lắng nghe notification từ Kotlin để present WKWebView xem hóa đơn PDF
        NotificationCenter.default.addObserver(
            forName: NSNotification.Name("CskhPresentDocumentViewer"),
            object: nil,
            queue: .main
        ) { [weak self] notification in
            guard let filePath = notification.userInfo?["filePath"] as? String else { return }
            self?.presentHtmlViewer(filePath: filePath)
        }

        return true
    }

    private func presentShareSheet(filePath: String) {
        let fileUrl = URL(fileURLWithPath: filePath)
        let activityVC = UIActivityViewController(activityItems: [fileUrl], applicationActivities: nil)

        guard let rootVC = UIApplication.shared.connectedScenes
            .compactMap({ $0 as? UIWindowScene })
            .flatMap({ $0.windows })
            .first(where: { $0.isKeyWindow })?
            .rootViewController else { return }
        var topVC = rootVC
        while let presented = topVC.presentedViewController {
            topVC = presented
        }

        if UIDevice.current.userInterfaceIdiom == .pad {
            activityVC.popoverPresentationController?.sourceView = topVC.view
            activityVC.popoverPresentationController?.sourceRect = CGRect(
                x: topVC.view.bounds.midX,
                y: topVC.view.bounds.midY,
                width: 0,
                height: 0
            )
            activityVC.popoverPresentationController?.permittedArrowDirections = []
        }

        topVC.present(activityVC, animated: true)
    }

    private func presentHtmlViewer(filePath: String) {
        guard let rootVC = UIApplication.shared.connectedScenes
            .compactMap({ $0 as? UIWindowScene })
            .flatMap({ $0.windows })
            .first(where: { $0.isKeyWindow })?
            .rootViewController else { return }
        var topVC = rootVC
        while let presented = topVC.presentedViewController {
            topVC = presented
        }
        let viewerVC = HtmlViewerViewController(filePath: filePath)
        let nav = UINavigationController(rootViewController: viewerVC)
        nav.modalPresentationStyle = .fullScreen
        topVC.present(nav, animated: true)
    }

    func application(
        _ application: UIApplication,
        didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data
    ) {
        // Chuyển APNs token cho Firebase → Firebase sẽ tạo FCM token
        Messaging.messaging().apnsToken = deviceToken
        print("APNs token received, forwarded to Firebase")
    }

    func application(
        _ application: UIApplication,
        didFailToRegisterForRemoteNotificationsWithError error: Error
    ) {
        print("Failed to register for remote notifications: \(error)")
    }

    func application(
        _ application: UIApplication,
        didReceiveRemoteNotification userInfo: [AnyHashable: Any],
        fetchCompletionHandler completionHandler: @escaping (UIBackgroundFetchResult) -> Void
    ) {
        Messaging.messaging().appDidReceiveMessage(userInfo)
        processNotificationData(userInfo)
        completionHandler(.newData)
    }

    // Firebase Messaging delegate — nhận FCM token
    func messaging(
        _ messaging: Messaging,
        didReceiveRegistrationToken fcmToken: String?
    ) {
        guard let token = fcmToken, !token.isEmpty else { return }
        print("FCM token received: \(token.prefix(20))...")
        // Lưu token vào Kotlin bridge
        IosFcmTokenBridgeKt.saveFcmToken(token: token)

        // Đăng ký topic để nhận thông báo tin nổi bật (giống Android)
        Messaging.messaging().subscribe(toTopic: "general_news") { error in
            if let error = error {
                print("FCM subscribe general_news error: \(error)")
            } else {
                print("FCM subscribed to general_news OK")
            }
        }
    }

    // Foreground: show banner
    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        willPresent notification: UNNotification,
        withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void
    ) {
        completionHandler([.banner, .sound, .badge])
    }

    // Notification tap
    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        didReceive response: UNNotificationResponse,
        withCompletionHandler completionHandler: @escaping () -> Void
    ) {
        let userInfo = response.notification.request.content.userInfo
        processNotificationData(userInfo)
        completionHandler()
    }

    private func requestNotificationAuthorization() {
        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .badge, .sound]) { granted, error in
            if granted {
                DispatchQueue.main.async {
                    UIApplication.shared.registerForRemoteNotifications()
                }
            }
            if let error = error {
                print("Notification authorization error: \(error)")
            }
        }
    }

    private func processNotificationData(_ userInfo: [AnyHashable: Any]) {
        let type = (userInfo["type"] as? String ?? "").trimmingCharacters(in: .whitespacesAndNewlines)
        let referenceId = (userInfo["referenceId"] as? String ?? "").trimmingCharacters(in: .whitespacesAndNewlines)

        var articleTitle: String? = nil
        var articleContent: String? = nil
        var feedbackId: Int64? = nil
        var invoiceId: Int64? = nil
        var navigateTo: String? = nil

        let upperType = type.uppercased()

        // ARTICLE
        if upperType == "ARTICLE" && !referenceId.isEmpty {
            articleTitle = userInfo["articleTitle"] as? String
                ?? userInfo["article_title"] as? String
                ?? userInfo["title"] as? String
            articleContent = userInfo["articleContent"] as? String
                ?? userInfo["article_content"] as? String
                ?? userInfo["body"] as? String
        }

        // FEEDBACK
        if upperType == "FEEDBACK" {
            if !referenceId.isEmpty, let id = Int64(referenceId), id > 0 {
                feedbackId = id
            }
            if feedbackId == nil {
                if let idStr = userInfo["feedback_id"] as? String, let id = Int64(idStr), id > 0 {
                    feedbackId = id
                } else if let idNum = userInfo["feedback_id"] as? NSNumber {
                    let id = idNum.int64Value
                    if id > 0 { feedbackId = id }
                }
            }
        }

        // INVOICE / PAYMENT / DEBT_REMINDER / OVERDUE / WATER_CUTOFF
        if upperType == "INVOICE" || upperType == "PAYMENT" || upperType == "DEBT_REMINDER" || upperType == "OVERDUE" || upperType == "WATER_CUTOFF" {
            if !referenceId.isEmpty, let id = Int64(referenceId), id > 0 {
                invoiceId = id
            }
            if invoiceId == nil {
                if let idStr = userInfo["invoice_id"] as? String, let id = Int64(idStr), id > 0 {
                    invoiceId = id
                } else if let idNum = userInfo["invoice_id"] as? NSNumber {
                    let id = idNum.int64Value
                    if id > 0 { invoiceId = id }
                }
            }
        }

        // navigate_to
        navigateTo = userInfo["navigate_to"] as? String
        if navigateTo == nil || navigateTo?.isEmpty == true {
            switch upperType {
            case "PAYMENT", "INVOICE", "DEBT_REMINDER", "OVERDUE", "WATER_CUTOFF":
                navigateTo = "notifications_billing"
            case "MAINTENANCE", "WATER_CUT":
                navigateTo = "notifications_maintenance"
            case "NOTIFICATION", "FEATURED":
                navigateTo = "notifications_featured"
            default:
                break
            }
        }

        IosNotificationBridgeKt.setIosNotificationData(
            articleTitle: articleTitle,
            articleContent: articleContent,
            feedbackId: feedbackId.map { KotlinLong(value: $0) },
            invoiceId: invoiceId.map { KotlinLong(value: $0) },
            navigateTo: navigateTo
        )
    }
}
