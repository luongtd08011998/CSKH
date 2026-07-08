import UIKit
import WebKit

/// Màn hình xem bản thể hiện hóa đơn điện tử (.html) ngay trong app.
/// Thiết kế: Toolbar xanh gradient phía trên + WKWebView + Bottom bar nút "Lưu về máy"
class HtmlViewerViewController: UIViewController {

    private let filePath: String
    private var webView: WKWebView!

    init(filePath: String) {
        self.filePath = filePath
        super.init(nibName: nil, bundle: nil)
    }

    required init?(coder: NSCoder) { fatalError() }

    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .white
        navigationController?.setNavigationBarHidden(true, animated: false)
        setupWebView()
        setupBottomBar()
        loadHtml()
    }

    // ── WKWebView ────────────────────────────────────────────────────────────
    private func setupWebView() {
        let config = WKWebViewConfiguration()
        config.preferences.javaScriptEnabled = true
        let prefs = WKWebpagePreferences()
        prefs.allowsContentJavaScript = true
        config.defaultWebpagePreferences = prefs
        config.preferences.setValue(true, forKey: "allowFileAccessFromFileURLs")
        
        webView = WKWebView(frame: .zero, configuration: config)
        webView.navigationDelegate = self
        webView.translatesAutoresizingMaskIntoConstraints = false
        webView.scrollView.showsHorizontalScrollIndicator = true
        view.addSubview(webView)
        // Constraints sẽ được cập nhật sau khi bottom bar được tạo
    }

    // ── Bottom bar: nút Đóng và nút Lưu ──────────────────────────────────────
    private func setupBottomBar() {
        let bottomBar = UIView()
        bottomBar.backgroundColor = .white
        bottomBar.translatesAutoresizingMaskIntoConstraints = false

        // Gạch phân cách mỏng phía trên bottom bar
        let separator = UIView()
        separator.backgroundColor = UIColor(white: 0.88, alpha: 1)
        separator.translatesAutoresizingMaskIntoConstraints = false
        bottomBar.addSubview(separator)

        let backBtn = UIButton(type: .system)
        backBtn.setTitle("Đóng", for: .normal)
        backBtn.setTitleColor(.darkGray, for: .normal)
        backBtn.titleLabel?.font = UIFont.boldSystemFont(ofSize: 15)
        backBtn.backgroundColor = UIColor(white: 0.93, alpha: 1)
        backBtn.layer.cornerRadius = 12
        backBtn.clipsToBounds = true
        backBtn.translatesAutoresizingMaskIntoConstraints = false
        backBtn.addTarget(self, action: #selector(closeTapped), for: .touchUpInside)

        let saveBtn = UIButton(type: .system)
        saveBtn.setTitle("⬇   Lưu hóa đơn về máy", for: .normal)
        saveBtn.setTitleColor(.white, for: .normal)
        saveBtn.titleLabel?.font = UIFont.boldSystemFont(ofSize: 15)
        saveBtn.layer.cornerRadius = 12
        saveBtn.clipsToBounds = true
        saveBtn.translatesAutoresizingMaskIntoConstraints = false
        saveBtn.addTarget(self, action: #selector(saveTapped), for: .touchUpInside)

        // Gradient xanh cho nút lưu
        let btnGradient = CAGradientLayer()
        btnGradient.colors = [
            UIColor(red: 0.05, green: 0.28, blue: 0.63, alpha: 1).cgColor,
            UIColor(red: 0.10, green: 0.46, blue: 0.82, alpha: 1).cgColor,
        ]
        btnGradient.startPoint = CGPoint(x: 0, y: 0.5)
        btnGradient.endPoint   = CGPoint(x: 1, y: 0.5)
        let screenWidth = UIScreen.main.bounds.width
        btnGradient.frame = CGRect(x: 0, y: 0, width: screenWidth - 16 - 90 - 12 - 16, height: 50)
        saveBtn.layer.insertSublayer(btnGradient, at: 0)

        bottomBar.addSubview(backBtn)
        bottomBar.addSubview(saveBtn)
        view.addSubview(bottomBar)

        NSLayoutConstraint.activate([
            // Separator
            separator.topAnchor.constraint(equalTo: bottomBar.topAnchor),
            separator.leadingAnchor.constraint(equalTo: bottomBar.leadingAnchor),
            separator.trailingAnchor.constraint(equalTo: bottomBar.trailingAnchor),
            separator.heightAnchor.constraint(equalToConstant: 1),

            // Nút Đóng
            backBtn.topAnchor.constraint(equalTo: separator.bottomAnchor, constant: 12),
            backBtn.leadingAnchor.constraint(equalTo: bottomBar.leadingAnchor, constant: 16),
            backBtn.heightAnchor.constraint(equalToConstant: 50),
            backBtn.bottomAnchor.constraint(equalTo: bottomBar.safeAreaLayoutGuide.bottomAnchor, constant: -12),
            backBtn.widthAnchor.constraint(equalToConstant: 90),

            // Nút Lưu
            saveBtn.topAnchor.constraint(equalTo: separator.bottomAnchor, constant: 12),
            saveBtn.leadingAnchor.constraint(equalTo: backBtn.trailingAnchor, constant: 12),
            saveBtn.trailingAnchor.constraint(equalTo: bottomBar.trailingAnchor, constant: -16),
            saveBtn.heightAnchor.constraint(equalToConstant: 50),
            saveBtn.bottomAnchor.constraint(equalTo: bottomBar.safeAreaLayoutGuide.bottomAnchor, constant: -12),

            // Bottom bar
            bottomBar.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            bottomBar.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            bottomBar.bottomAnchor.constraint(equalTo: view.bottomAnchor), // Kéo tới tận cùng dưới

            // WebView
            webView.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor),
            webView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            webView.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            webView.bottomAnchor.constraint(equalTo: bottomBar.topAnchor),
        ])
    }

    // ── Load HTML ────────────────────────────────────────────────────────────
    private func loadHtml() {
        print("INVOICE_DEBUG: [Swift] loadHtml called")
        print("INVOICE_DEBUG: [Swift] filePath = \(filePath)")
        
        let file = URL(fileURLWithPath: filePath)
        print("INVOICE_DEBUG: [Swift] URL = \(file)")
        
        guard let data = try? Data(contentsOf: file) else {
            print("INVOICE_DEBUG: [Swift] ERROR - Could not read data from URL!")
            return
        }
        print("INVOICE_DEBUG: [Swift] Read \(data.count) bytes from file")
        
        let directory = file.deletingLastPathComponent()
        print("INVOICE_DEBUG: [Swift] Base directory = \(directory)")
        
        print("INVOICE_DEBUG: [Swift] Loading as regular HTML via loadFileURL")
        webView.loadFileURL(file, allowingReadAccessTo: directory)
    }

    // ── Actions ──────────────────────────────────────────────────────────────
    @objc private func closeTapped() {
        dismiss(animated: true)
    }

    @objc private func saveTapped() {
        let src = URL(fileURLWithPath: filePath)
        guard FileManager.default.fileExists(atPath: filePath) else {
            showAlert("Không tìm thấy file để lưu.")
            return
        }

        // Dùng Share Sheet để khách hàng chọn: lưu vào Files, AirDrop, Zalo,...
        let activityVC = UIActivityViewController(activityItems: [src], applicationActivities: nil)

        if UIDevice.current.userInterfaceIdiom == .pad {
            activityVC.popoverPresentationController?.sourceView = view
            activityVC.popoverPresentationController?.sourceRect = CGRect(
                x: view.bounds.midX, y: view.bounds.maxY - 80,
                width: 0, height: 0
            )
            activityVC.popoverPresentationController?.permittedArrowDirections = [.down]
        }

        present(activityVC, animated: true)
    }

    private func showAlert(_ msg: String) {
        let alert = UIAlertController(title: nil, message: msg, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "OK", style: .default))
        present(alert, animated: true)
    }
}

extension HtmlViewerViewController: WKNavigationDelegate {
    func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
        print("INVOICE_DEBUG: [Swift] WKWebView didFinish navigation")
    }
    
    func webView(_ webView: WKWebView, didFail navigation: WKNavigation!, withError error: Error) {
        print("INVOICE_DEBUG: [Swift] WKWebView didFail navigation: \(error)")
    }
    
    func webView(_ webView: WKWebView, didFailProvisionalNavigation navigation: WKNavigation!, withError error: Error) {
        print("INVOICE_DEBUG: [Swift] WKWebView didFailProvisionalNavigation: \(error)")
    }
}
