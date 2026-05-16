import SwiftUI
import ComposeApp
import UserNotifications

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}

class AppDelegate: NSObject, UIApplicationDelegate, UNUserNotificationCenterDelegate {
    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {
        UNUserNotificationCenter.current().delegate = self

        // If launched from a notification tap
        if let notification = launchOptions?[.remoteNotification] as? [String: Any] {
            processNotificationData(notification)
        }

        requestNotificationAuthorization()
        return true
    }

    func application(
        _ application: UIApplication,
        didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data
    ) {
        // Token available for FCM/APNs backend registration if needed
    }

    func application(
        _ application: UIApplication,
        didFailToRegisterForRemoteNotificationsWithError error: Error
    ) {
        print("Failed to register for remote notifications: \(error)")
    }

    // Foreground: show banner
    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        willPresent notification: UNNotification
    ) async -> UNNotificationPresentationOptions {
        return [.banner, .sound, .badge]
    }

    // Notification tap
    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        didReceive response: UNNotificationResponse
    ) async {
        let userInfo = response.notification.request.content.userInfo
        processNotificationData(userInfo)
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
