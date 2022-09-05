package com.devyk.aveditor.utils

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import com.devyk.aveditor.utils.LogHelper.TAG
import java.io.File
import java.io.IOException
import java.net.URISyntaxException


/**
 * <pre>
 *     author  : devyk on 2020-08-09 13:53
 *     blog    : https://juejin.im/user/578259398ac2470061f3a3fb/posts
 *     github  : https://github.com/yangkun19921001
 *     mailbox : yang1001yk@gmail.com
 *     desc    : This is FileUtils
 * </pre>
 */
object FileUtils {


    fun createFileByDeleteOldFile(filePath: String): Boolean {
        return createFileByDeleteOldFile(
            getFileByPath(
                filePath
            )
        )
    }

    private fun getFileByPath(filePath: String): File {
        return File(filePath)
    }

    private fun createFileByDeleteOldFile(file: File?): Boolean {
        if (file == null) return false
        // file exists and unsuccessfully delete then return false
        if (file.exists() && !file.delete()) return false
        if (!createOrExistsDir(file.parentFile)) return false
        return try {
            file.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }

    }

    private fun createOrExistsDir(file: File?): Boolean {
        return file != null && if (file.exists()) file.isDirectory else file.mkdirs()
    }

    private fun deleteFile(filePath: String): Boolean =
        deleteFile(getFileByPath(filePath))


    private fun deleteFile(file: File?): Boolean {
        if (file == null) return false
        if (file.exists() && file.delete())
            return true
        return false
    }

    /**
     * content： 转 String URL File Path
     * @see https://stackoverflow.com/questions/3401579/get-filename-and-path-from-uri-from-mediastore
     */
    fun getFilePathByUri(context: Context, contentUri: Uri): String? {
        try {
            return getFilePath(context, contentUri)
        } catch (error: Exception) {
            LogHelper.e(TAG, error.message)
        }
        return null
    }


    @Throws(URISyntaxException::class)
    fun getFilePath(context: Context, uri: Uri): String? {
        var tempUri = uri
        var selection: String? = null
        var selectionArgs: Array<String>? = null
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        if (DocumentsContract.isDocumentUri(context.applicationContext, tempUri)) {
            if (isExternalStorageDocument(tempUri)) {
                val docId = DocumentsContract.getDocumentId(tempUri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
            } else if (isDownloadsDocument(tempUri)) {
                val id = DocumentsContract.getDocumentId(tempUri)
                tempUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
                )
            } else if (isMediaDocument(tempUri)) {
                val docId = DocumentsContract.getDocumentId(tempUri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                when (split[0]) {
                    "image" -> {
                        tempUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    }
                    "video" -> {
                        tempUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    }
                    "audio" -> {
                        tempUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                }
                selection = "_id=?"
                selectionArgs = arrayOf(split[1])
            }
        }
        if ("content".equals(tempUri.scheme!!, ignoreCase = true)) {


            if (isGooglePhotosUri(tempUri)) {
                return tempUri.lastPathSegment
            }

            val projection = arrayOf(MediaStore.Images.Media.DATA)
            var cursor: Cursor? = null
            try {
                cursor = context.contentResolver
                    .query(tempUri, projection, selection, selectionArgs, null)
                val columnIndex = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                if (cursor.moveToFirst()) {
                    return cursor.getString(columnIndex)
                }
                cursor.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        } else if ("file".equals(tempUri.scheme!!, ignoreCase = true)) {
            return tempUri.path
        }
        return null
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }


    //删除文件夹
    private fun deleteDirectory(folder: File) {
        if (folder.exists()) {
            val files = folder.listFiles() ?: return
            for (i in files.indices) {
                if (files[i].isDirectory) {
                    deleteDirectory(files[i])
                } else {
                    files[i].delete()
                }
            }
        }
        folder.delete()
    }

    /**
     * 判断文件是否存在
     */
    fun isExists(path: String): Boolean {
        return File(path).exists()
    }

}