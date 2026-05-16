package com.example.cskh.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSError
import platform.Foundation.NSString
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.stringByAppendingPathComponent
import platform.PhotosUI.PHPickerConfiguration
import platform.PhotosUI.PHPickerResult
import platform.PhotosUI.PHPickerViewController
import platform.PhotosUI.PHPickerViewControllerDelegateProtocol
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.UIKit.UIViewController
import platform.darwin.NSObject
import platform.posix.fclose
import platform.posix.fopen
import platform.posix.fwrite
import platform.posix.memcpy
import kotlin.random.Random

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun rememberImagePicker(
    onResult: (List<PickedImage>) -> Unit,
    onError: (String) -> Unit,
): ImagePicker {
    return remember {
        object : ImagePicker {
            private var retainedDelegate: NSObject? = null

            override fun pickImages(max: Int) {
                try {
                    val vc = resolveTopVC()
                    if (vc == null) {
                        onError("Không tìm được view controller")
                        return
                    }
                    val config = PHPickerConfiguration()
                    config.selectionLimit = max.toLong()
                    val picker = PHPickerViewController(configuration = config)
                    val delegate = PickerDelegate(onResult, onError)
                    retainedDelegate = delegate
                    picker.delegate = delegate
                    vc.presentViewController(picker, animated = true, completion = null)
                } catch (t: Throwable) {
                    onError(t.message ?: "Không mở được thư viện ảnh")
                }
            }

            override fun takePhoto() {
                try {
                    val vc = resolveTopVC()
                    if (vc == null) {
                        onError("Không tìm được view controller")
                        return
                    }
                    if (!UIImagePickerController.isSourceTypeAvailable(
                        UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
                    )) {
                        onError("Thiết bị không hỗ trợ camera")
                        return
                    }
                    val picker = UIImagePickerController()
                    picker.sourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
                    picker.allowsEditing = false
                    val delegate = CameraDelegate(onResult, onError)
                    retainedDelegate = delegate
                    picker.delegate = delegate
                    vc.presentViewController(picker, animated = true, completion = null)
                } catch (t: Throwable) {
                    onError(t.message ?: "Không mở được camera")
                }
            }
        }
    }
}

private class PickerDelegate(
    private val onResult: (List<PickedImage>) -> Unit,
    private val onError: (String) -> Unit,
) : NSObject(), PHPickerViewControllerDelegateProtocol {

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
            provider.loadDataRepresentationForTypeIdentifier("public.image") { nsData: NSData?, err: NSError? ->
                try {
                    if (nsData != null) {
                        val fileUrl = saveDataToTemp(nsData)
                        out += PickedImage(
                            uri = fileUrl.absoluteString ?: fileUrl.path.orEmpty(),
                            name = fileUrl.lastPathComponent ?: "image",
                        )
                    }
                } catch (t: Throwable) {
                    onError(t.message ?: "Không chọn được ảnh")
                } finally {
                    pending -= 1
                    if (pending == 0 && out.isNotEmpty()) {
                        onResult(out)
                    }
                }
            }
        }
    }
}

private class CameraDelegate(
    private val onResult: (List<PickedImage>) -> Unit,
    private val onError: (String) -> Unit,
) : NSObject(), UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol {

    override fun imagePickerController(
        picker: UIImagePickerController,
        didFinishPickingMediaWithInfo: Map<Any?, *>,
    ) {
        picker.dismissViewControllerAnimated(true, completion = null)
        val image = didFinishPickingMediaWithInfo["UIImagePickerControllerOriginalImage"]
            as? platform.UIKit.UIImage
        if (image != null) {
            val data = platform.UIKit.UIImageJPEGRepresentation(image, 0.8)
            if (data != null) {
                val fileUrl = saveDataToTemp(data)
                onResult(listOf(
                    PickedImage(
                        uri = fileUrl.absoluteString ?: fileUrl.path.orEmpty(),
                        name = fileUrl.lastPathComponent ?: "photo.jpg",
                    )
                ))
            } else {
                onError("Không xử lý được ảnh")
            }
        } else {
            onError("Không chụp được ảnh")
        }
    }

    override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
        picker.dismissViewControllerAnimated(true, completion = null)
    }
}

private fun resolveTopVC(): UIViewController? {
    val root = PickerPresenter.rootViewController ?: return null
    return traverseForTopVC(root)
}

private fun traverseForTopVC(vc: UIViewController): UIViewController {
    val presented = vc.presentedViewController as? UIViewController
    if (presented != null) return traverseForTopVC(presented)
    return vc
}

@OptIn(ExperimentalForeignApi::class)
private fun saveDataToTemp(nsData: NSData): NSURL {
    val len = nsData.length.toInt()
    val bytes = ByteArray(len)
    bytes.usePinned { pinned ->
        memcpy(pinned.addressOf(0), nsData.bytes, nsData.length)
    }
    val dir = NSTemporaryDirectory()
    val name = "photo_${Random.nextInt(100000)}.jpg"
    val path = (dir as NSString).stringByAppendingPathComponent(name)
    val f = fopen(path, "wb") ?: error("Không mở được file ảnh")
    try {
        bytes.usePinned { pinned ->
            fwrite(pinned.addressOf(0), 1u, len.toULong(), f)
        }
    } finally {
        fclose(f)
    }
    return NSURL.fileURLWithPath(path)
}
