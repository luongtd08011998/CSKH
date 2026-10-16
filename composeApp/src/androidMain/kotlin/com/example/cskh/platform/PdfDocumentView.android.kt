package com.example.cskh.platform

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import java.io.File

@Composable
actual fun PdfDocumentView(
    filePath: String,
    modifier: Modifier,
) {
    AndroidView(
        factory = { context ->
            val scrollView = ScrollView(context).apply {
                setBackgroundColor(Color.parseColor("#E2E8F0"))
                isFillViewport = true
            }

            val container = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER_HORIZONTAL
                setPadding(0, (12 * context.resources.displayMetrics.density).toInt(), 0, (24 * context.resources.displayMetrics.density).toInt())
            }

            scrollView.addView(
                container,
                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                )
            )

            val loadingBar = ProgressBar(context).apply {
                isIndeterminate = true
            }
            container.addView(loadingBar)

            renderPdfIntoContainer(context, filePath, container, loadingBar)

            scrollView
        },
        update = { scrollView ->
            // View retains state
        },
        modifier = modifier,
    )
}

private fun renderPdfIntoContainer(
    context: Context,
    filePath: String,
    container: LinearLayout,
    loadingBar: ProgressBar,
) {
    val file = File(filePath)
    if (!file.exists()) {
        container.removeView(loadingBar)
        return
    }

    Thread {
        try {
            val fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            val pdfRenderer = PdfRenderer(fileDescriptor)
            val pageCount = pdfRenderer.pageCount

            val displayMetrics = context.resources.displayMetrics
            val screenWidth = displayMetrics.widthPixels
            val scale = 2.0f
            val bitmaps = mutableListOf<Bitmap>()

            for (i in 0 until pageCount) {
                val page = pdfRenderer.openPage(i)
                val targetWidth = (screenWidth * scale).toInt()
                val targetHeight = ((screenWidth.toFloat() / page.width) * page.height * scale).toInt()

                val bitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
                bitmap.eraseColor(Color.WHITE)
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                bitmaps.add(bitmap)
                page.close()
            }
            pdfRenderer.close()
            fileDescriptor.close()

            val activity = context as? Activity
            val postAction = Runnable {
                container.removeView(loadingBar)
                val dp = displayMetrics.density
                for (bitmap in bitmaps) {
                    val imageView = ImageView(context).apply {
                        setImageBitmap(bitmap)
                        adjustViewBounds = true
                        elevation = 4 * dp
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            setMargins(
                                (8 * dp).toInt(),
                                (6 * dp).toInt(),
                                (8 * dp).toInt(),
                                (6 * dp).toInt()
                            )
                        }
                    }
                    container.addView(imageView)
                }
            }

            if (activity != null) {
                activity.runOnUiThread(postAction)
            } else {
                container.post(postAction)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            container.post {
                container.removeView(loadingBar)
            }
        }
    }.start()
}
