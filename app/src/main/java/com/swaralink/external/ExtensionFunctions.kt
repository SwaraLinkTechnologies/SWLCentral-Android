package com.swaralink.external

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import org.apache.commons.io.IOUtil
import java.io.*
import java.lang.Exception



fun String.removeSpaces() = replace(" ", "") //added by divya


fun getRealPathFromUri(context: Context, contentUri: Uri?): String? {

    var file: File? = null
    try {
        val parcelFileDescriptor = context.contentResolver.openFileDescriptor(
            contentUri!!, "r", null
        )
        val inputStream: InputStream = FileInputStream(parcelFileDescriptor!!.fileDescriptor)
        file = File(context.cacheDir, getFileName(context, contentUri))
        val fileOutputStream: OutputStream = FileOutputStream(file)
        IOUtil.copy(inputStream, fileOutputStream)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return file!!.path
}

fun getFileName(context: Context, fileUri: Uri?): String? {
    var name = ""
    val cursor = context.contentResolver.query(fileUri!!, null, null, null, null)
    if (cursor != null) {
        val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor.moveToFirst()
        name = cursor.getString(index)
        cursor.close()
    }
    return name
}
fun getMimeType(url: String): String? {
    var type: String? = null
    val extension =
        url.substring(url.lastIndexOf(".") + 1) /*MimeTypeMap.getFileExtensionFromUrl(BASE_URL);*/
    if (extension != null) {
        type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    }
    return type
}
