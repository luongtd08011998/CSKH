package com.example.cskh.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalUIViewController
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.NSString
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.stringByAppendingPathComponent
import platform.PhotosUI.PHPickerConfiguration
import platform.PhotosUI.PHPickerResult
import platform.PhotosUI.PHPickerViewController
import platform.PhotosUI.PHPickerViewControllerDelegateProtocol
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerCameraDeviceRear
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerOriginalImage
import platform.UIKit.UIImagePickerControllerSourceTypeCamera
import platform.UIKit.UIImagePickerControllerSourceTypePhotoLibrary
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.darwin.NSObject
import kotlin.random.Random

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun rememberImagePicker(
    onResult: (List<PickedImage>) -> Unit,
    onError: (String) -> Unit,
): ImagePicker {
    val vc = LocalUIViewController.current

    return remember {
        object : ImagePicker {
            // Keep strong refs for ObjC delegates (avoid being GC'd early).
            private var currentDelegate: NSObject? = null

            override fun pickImages(max: Int) {
                try {
                    val config = PHPickerConfiguration().apply {
                        selectionLimit = max
                        filter = platform.PhotosUI.PHPickerFilter.imagesFilter
                    }
                    val picker = PHPickerViewController(configuration = config)
                    val delegate = object : NSObject(), PHPickerViewControllerDelegateProtocol {
                        override fun picker(
                            picker: PHPickerViewController,
                            didFinishPicking: List<*>,
                        ) {
                            picker.dismissViewControllerAnimated(true, completion = null)
                            val results = didFinishPicking.filterIsInstance<PHPickerResult>()
                            if (results.isEmpty()) return

                            val out = mutableListOf<PickedImage>()
                            var pending = results.size

                            results.forEach { r ->
                                val provider = r.itemProvider
                                provider.loadObjectOfClass(UIImage.`class`()!!) { obj, err ->
                                    try {
                                        val img = obj as? UIImage
                                        if (img != null) {
                                            val fileUrl = saveUiImageToTemp(img)
                                            out += PickedImage(
                                                uri = fileUrl.absoluteString ?: fileUrl.path.orEmpty(),
                                                name = fileUrl.lastPathComponent ?: "image",
                                            )
                                        }
                                    } catch (t: Throwable) {
                                        onError(t.message ?: "Không chọn được ảnh")
                                    } finally {
                                        pending -= 1
                                        if (pending == 0 && out.isNotEmpty()) onResult(out)
                                    }
                                }
                            }
                        }
                    }
                    currentDelegate = delegate
                    picker.delegate = delegate
                    vc.presentViewController(picker, animated = true, completion = null)
                } catch (t: Throwable) {
                    onError(t.message ?: "Không mở được thư viện ảnh")
                }
            }

            override fun takePhoto() {
                try {
                    val picker = UIImagePickerController().apply {
                        sourceType = UIImagePickerControllerSourceTypeCamera
                        cameraDevice = UIImagePickerControllerCameraDeviceRear
                    }
                    val delegate = object : NSObject(),
                        UIImagePickerControllerDelegateProtocol,
                        UINavigationControllerDelegateProtocol {
                        override fun imagePickerController(
                            picker: UIImagePickerController,
                            didFinishPickingMediaWithInfo: Map<Any?, *>,
                        ) {
                            picker.dismissViewControllerAnimated(true, completion = null)
                            val img = didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage
                            if (img == null) return
                            try {
                                val fileUrl = saveUiImageToTemp(img)
                                onResult(
                                    listOf(
                                        PickedImage(
                                            uri = fileUrl.absoluteString ?: fileUrl.path.orEmpty(),
                                            name = fileUrl.lastPathComponent ?: "photo",
                                        ),
                                    ),
                                )
                            } catch (t: Throwable) {
                                onError(t.message ?: "Không lưu được ảnh")
                            }
                        }

                        override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
                            picker.dismissViewControllerAnimated(true, completion = null)
                        }
                    }
                    currentDelegate = delegate
                    picker.delegate = delegate
                    vc.presentViewController(picker, animated = true, completion = null)
                } catch (t: Throwable) {
                    onError(t.message ?: "Không mở được camera")
                }
            }
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun saveUiImageToTemp(image: UIImage): NSURL {
    val data: NSData = UIImageJPEGRepresentation(image, 0.92) ?: error("Không tạo được dữ liệu ảnh")
    val dir = NSTemporaryDirectory()
    val name = "photo_${Random.nextInt(100000)}.jpg"
    val path = (dir as NSString).stringByAppendingPathComponent(name)
    val url = NSURL.fileURLWithPath(path)
    val ok = data.writeToURL(url, atomically = true)
    if (!ok) error("Không ghi được file ảnh")
    return url
}

