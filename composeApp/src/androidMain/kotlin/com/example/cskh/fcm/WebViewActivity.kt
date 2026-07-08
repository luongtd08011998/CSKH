package com.example.cskh.fcm

import android.annotation.SuppressLint
import android.content.ContentValues
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Gravity
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import java.io.File
import java.io.FileOutputStream

class WebViewActivity : ComponentActivity() {

    private lateinit var webView: WebView
    private var htmlFilePath: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val url = intent?.getStringExtra(EXTRA_URL).orEmpty()
        htmlFilePath = intent?.getStringExtra(EXTRA_HTML_FILE_PATH).orEmpty()
        val title = intent?.getStringExtra(EXTRA_TITLE) ?: "Hóa đơn điện tử"

        val contentView: View = when {
            htmlFilePath.isNotBlank() -> {
                val file = File(htmlFilePath)
                if (!file.exists()) { finish(); return }
                if (file.extension.equals("pdf", ignoreCase = true)) {
                    createPdfView(file)
                } else {
                    createWebViewForFile(file)
                }
            }
            url.isNotBlank() -> createWebView(url)
            else -> { finish(); return }
        }

        val dp = resources.displayMetrics.density

        // ── Root ────────────────────────────────────────────────────────────────
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#F5F7FA"))
        }



        // ── WebView chiếm phần còn lại ───────────────────────────────────────
        root.addView(
            contentView,
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f),
        )

        // ── Bottom bar: nút Lưu về máy ──────────────────────────────────────
        if (htmlFilePath.isNotBlank()) {
            // Đường kẻ trên bottom bar
            val bottomDivider = View(this).apply {
                setBackgroundColor(Color.parseColor("#E0E0E0"))
            }
            root.addView(
                bottomDivider,
                LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (1 * dp).toInt()),
            )

            val bottomBar = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setBackgroundColor(Color.WHITE)
                setPadding((16 * dp).toInt(), (12 * dp).toInt(), (16 * dp).toInt(), (16 * dp).toInt())
            }

            val backBtnBottom = TextView(this).apply {
                text = "Đóng"
                textSize = 15f
                setTextColor(Color.parseColor("#424242"))
                typeface = Typeface.DEFAULT_BOLD
                gravity = Gravity.CENTER
                setPadding((20 * dp).toInt(), (14 * dp).toInt(), (20 * dp).toInt(), (14 * dp).toInt())
                background = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadius = 12 * dp
                    setColor(Color.parseColor("#EEEEEE"))
                }
                isClickable = true
                isFocusable = true
                setOnClickListener { finish() }
            }

            val saveBtn = TextView(this).apply {
                text = "⬇   Lưu hóa đơn về máy"
                textSize = 15f
                setTextColor(Color.WHITE)
                typeface = Typeface.DEFAULT_BOLD
                gravity = Gravity.CENTER
                setPadding((0).toInt(), (14 * dp).toInt(), (0).toInt(), (14 * dp).toInt())
                background = GradientDrawable(
                    GradientDrawable.Orientation.LEFT_RIGHT,
                    intArrayOf(Color.parseColor("#0D47A1"), Color.parseColor("#1976D2")),
                ).apply {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadius = 12 * dp
                }
                isClickable = true
                isFocusable = true
                elevation = 4 * dp
                setOnClickListener { saveToDownloads() }
            }
            
            bottomBar.addView(
                backBtnBottom,
                LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                    rightMargin = (12 * dp).toInt()
                }
            )
            
            bottomBar.addView(
                saveBtn,
                LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f),
            )

            root.addView(
                bottomBar,
                LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT),
            )
        }

        setContentView(root)
    }

    // ── Lưu file sang Downloads ─────────────────────────────────────────
    private fun saveToDownloads() {
        val src = File(htmlFilePath)
        if (!src.exists()) { toast("Không tìm thấy file để lưu."); return }
        val isPdf = src.extension.equals("pdf", ignoreCase = true)
        val mime = if (isPdf) "application/pdf" else "text/html"

        try {
            val fileName = src.name
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = contentResolver
                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, mime)
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                    ?: run { toast("Không thể tạo file."); return }
                resolver.openOutputStream(uri)?.use { out -> src.inputStream().use { it.copyTo(out) } }
                    ?: run { toast("Không ghi được file."); return }
            } else {
                @Suppress("DEPRECATION")
                val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                dir.mkdirs()
                FileOutputStream(File(dir, fileName)).use { out -> src.inputStream().use { it.copyTo(out) } }
            }
            toast("✅ Đã lưu \"$fileName\" vào Downloads.")
        } catch (e: Exception) {
            toast("Lỗi: ${e.message}")
        }
    }

    private fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_LONG).show()

    override fun onBackPressed() {
        if (::webView.isInitialized && webView.canGoBack()) webView.goBack()
        else super.onBackPressed()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun createWebView(url: String): WebView = WebView(this).also { webView = it }.apply {
        webViewClient = WebViewClient()
        settings.javaScriptEnabled = true
        loadUrl(url)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun createWebViewForFile(file: File): WebView = WebView(this).also { webView = it }.apply {
        webViewClient = WebViewClient()
        settings.javaScriptEnabled = true
        settings.allowFileAccess = true
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true
        settings.builtInZoomControls = true
        settings.displayZoomControls = false
        val content = file.readText(Charsets.UTF_8)
        val mimeType = if (file.name.endsWith(".xml", ignoreCase = true) || content.trimStart().startsWith("<?xml")) "text/xml" else "text/html"
        loadDataWithBaseURL("file://${file.parent}/", content, mimeType, "UTF-8", null)
    }

    private fun createPdfView(file: File): View {
        val scrollView = android.widget.ScrollView(this).apply {
            setBackgroundColor(Color.LTGRAY)
        }
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        scrollView.addView(container, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT))

        Thread {
            try {
                val fileDescriptor = android.os.ParcelFileDescriptor.open(file, android.os.ParcelFileDescriptor.MODE_READ_ONLY)
                val pdfRenderer = android.graphics.pdf.PdfRenderer(fileDescriptor)
                val pageCount = pdfRenderer.pageCount

                val bitmaps = mutableListOf<android.graphics.Bitmap>()
                val displayMetrics = resources.displayMetrics
                val screenWidth = displayMetrics.widthPixels

                for (i in 0 until pageCount) {
                    val page = pdfRenderer.openPage(i)
                    // Scale cho ảnh PDF nét nhất có thể bằng độ phân giải màn hình x2
                    val scale = 2f
                    val width = (screenWidth * scale).toInt()
                    val height = (screenWidth.toFloat() / page.width * page.height * scale).toInt()
                    
                    val bitmap = android.graphics.Bitmap.createBitmap(width, height, android.graphics.Bitmap.Config.ARGB_8888)
                    bitmap.eraseColor(Color.WHITE)
                    page.render(bitmap, null, null, android.graphics.pdf.PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    bitmaps.add(bitmap)
                    page.close()
                }
                pdfRenderer.close()
                fileDescriptor.close()

                runOnUiThread {
                    for (bitmap in bitmaps) {
                        val imageView = android.widget.ImageView(this@WebViewActivity).apply {
                            setImageBitmap(bitmap)
                            adjustViewBounds = true
                            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                                setMargins(0, 0, 0, (8 * displayMetrics.density).toInt())
                            }
                        }
                        container.addView(imageView)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread { toast("Không thể hiển thị PDF: ${e.message}") }
            }
        }.start()

        return scrollView
    }

    companion object {
        const val EXTRA_URL = "URL"
        const val EXTRA_HTML_FILE_PATH = "HTML_FILE_PATH"
        const val EXTRA_TITLE = "TITLE"
    }
}
