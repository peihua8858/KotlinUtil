@file:JvmName("FileUtil")
@file:JvmMultifileClass

package com.fz.common.file

import com.fz.common.array.isNonEmpty
import com.fz.common.text.isNonEmpty
import com.fz.common.utils.*
import java.io.*
import java.nio.charset.Charset
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

val UTF8: Charset = Charset.forName("UTF-8")

/**
 * 判断指定文件是否存在
 *
 * @param file 文件对象
 * @return 存在返回true，否则返回false
 */
fun File?.exists(): Boolean {
    return this != null && exists()
}

fun File?.mkdirs(): Boolean {
    return this != null && mkdirs()
}

/**
 * 判断指定文件是否存在
 *
 * @param fileName 文件对象
 * @return 存在返回true，否则返回false
 */
fun String?.exists(): Boolean {
    return !this.isNullOrEmpty() && File(this).exists()
}

fun File?.isFile(): Boolean {
    return this != null && isFile
}

fun String?.isFile(): Boolean {
    return !isNullOrEmpty() && File(this).isFile
}

fun File?.isDirectory(): Boolean {
    return this != null && this.isDirectory
}

fun File?.isDirectoryEmpty(): Boolean {
    return listFiles() == null
}

fun File?.listFiles(): Array<File>? {
    if (isDirectory()) {
        val files = this?.listFiles()
        return if (files != null && files.isNotEmpty()) files else null
    }
    return null
}

/**
 * 删除指定的文件
 *
 * @param file 文件对象
 * @return
 */
fun File?.deleteFile(): Boolean {
    return exists() && (this?.delete() ?: false)
}

/**
 * 删除文件或文件夹
 * @author dingpeihua
 * @date 2020/11/25 20:17
 * @version 1.0
 */
fun File?.deleteFileOrDir(): Boolean {
    return this?.let {
        if (!exists()) {
            return true
        }
        if (isFile) {
            return delete()
        }
        val files = listFiles()
        if (files != null) {
            for (file in files) {
                file.deleteFileOrDir()
            }
        }
        return delete()
    } ?: false
}

/**
 * 删除指定目录下所有的文件
 *
 * @param file 目录文件对象
 * @param isDeleteCurDir 是否删除当前目录
 * @return
 */
@JvmOverloads
fun File?.deleteDirectory(isDeleteCurDir: Boolean = true): Boolean {
    return this?.let {
        if (exists()) {
            if (isDirectory) {
                val files = listFiles()
                if (files.isNonEmpty()) {
                    // 先删除该目录下所有的文件
                    for (f: File? in files) {
                        f.deleteDirectory(true)
                    }
                    return true
                }
            } else {
                // 最后删除该目录
                if (isDeleteCurDir) {
                    return delete()
                }
            }
        }
        return false
    } ?: true
}

/**
 * 删除指定目录下所有的文件
 *
 * @param directoryName 目录文件对象
 * @return
 */
fun CharSequence?.deleteDirectory(): Boolean {
    return this.isNonEmpty() && File(this.toString()).deleteDirectory()
}

/**
 * 删除指定的文件或目录
 *
 * @param file 文件或目录对象
 */
fun File?.delete(): Boolean {
    if (!isNull()) {
        if (isFile) {
            return deleteFile()
        } else if (isDirectory) {
            return deleteDirectory()
        }
    }
    return false
}

/**
 * 删除指定的文件或目录
 *
 * @param fileOrDirName 文件名（全路径）或目录名
 */
fun CharSequence?.delete(): Boolean {
    return this.isNonEmpty() && File(this.toString()).delete()
}


@JvmOverloads
fun File?.write(content: String?, append: Boolean = false): Boolean {
    if (this == null) {
        return false
    }
    // OutputStreamWriter osw = null;
    if (content.isNullOrEmpty()) {
        return false
    }
    if (exists()) {
        delete()
    }
    if (parentFile?.exists() == false) {
        this.parentFile?.mkdirs()
    }
    try {
        FileOutputStream(this, append).use { fos ->
            fos.write(content.toByteArray(charset("utf-8")), 0, content.length)
            fos.flush()
            return true
        }
    } catch (e1: FileNotFoundException) {
        e1.printStackTrace()
    } catch (e1: UnsupportedEncodingException) {
        e1.printStackTrace()
    } catch (e1: IOException) {
        e1.printStackTrace()
    }
    return false
}

fun CharSequence?.write(fileName: String?): Boolean {
    if (fileName == null || this == null) {
        return false
    }
    return File(fileName).write(this.toString(), false)
}

fun CharSequence?.write(fileName: String?, append: Boolean): Boolean {
    if (fileName == null || this == null) {
        return false
    }
    return File(fileName).write(this.toString(), append)
}

fun File?.read(): String {
    if (this == null) {
        return ""
    }
    if (!exists()) {
        return ""
    }
    try {
        FileInputStream(this).use { fis ->
            val b = ByteArray(1024)
            val sb = StringBuilder()
            while (fis.read(b, 0, b.size) != -1) {
                sb.append(String(b, 0, b.size, UTF8))
            }
            return sb.toString()
        }
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return ""
}

fun String?.read(): String? {
    if (this == null) {
        return null
    }
    return File(this).read()
}

fun File?.createFile(): Boolean {
    try {
        if (isNotNull()) {
            return this.createNewFile()
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return false
}

fun String?.createFile(): Boolean {
    if (this == null) {
        return false
    }
    return !isNullOrEmpty() && File(this).createFile()
}

/**
 * 文件拷贝操作
 *
 * @param source 源文件
 * @param dest   目标文件
 * @return boolean true拷贝成功，反之失败
 * @author dingpeihua
 * @date 2018/5/12 20:28
 * @version 1.0
 */
fun File?.copyToFile(dest: File?): Boolean {
    try {
        this?.let { input ->
            FileInputStream(input).use { fis ->
                dest?.let { out ->
                    FileOutputStream(out).use { fos ->
                        val buffer = ByteArray(1024)
                        var length: Int
                        while (fis.read(buffer).also { length = it } > 0) {
                            fos.write(buffer, 0, length)
                        }
                        return true
                    }
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return false
}

fun File.writeImageToFile(data: ByteArray?): File? {
    return if (this.isFile) {
        writeToFile(data)
    } else writeImageToFile(data, this, false)
}

fun writeImageToFile(data: ByteArray?, outFile: File, deleteFile: Boolean): File? {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val rand = Random()
    val randomNum = rand.nextInt(1000 + 1)
    val fileName = "IMG_$timeStamp$randomNum.jpg"
    return outFile.writeToFile(data, fileName, deleteFile)
}

fun File?.writeToFile(
    data: ByteArray?,
    fileName: String?,
    deleteParentAllFile: Boolean,
): File? {
    if (fileName == null) {
        return null
    }
    if (deleteParentAllFile) {
        val isDelete = delete()
        println("LockWriteFile>>>isDelete:$isDelete")
    }
    if (!exists()) {
        mkdirs()
    }
    val image = File(this, fileName)
    return image.writeToFile(data)
}

fun File?.writeToFile(data: ByteArray?): File? {
    if (this == null) {
        return null
    }
    try {
        FileOutputStream(this).use { outStream ->
            outStream.write(data)
            outStream.flush()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return this
}

fun read(inputStream: InputStream?): String? {
    if (inputStream == null) {
        return null
    }
    try {
        val b = ByteArray(1024)
        val sb = StringBuilder()
        while (inputStream.read(b, 0, b.size) != -1) {
            sb.append(String(b, 0, b.size, UTF8))
        }
        inputStream.close()
        return sb.toString()
    } catch (e1: FileNotFoundException) {
        e1.printStackTrace()
    } catch (e1: UnsupportedEncodingException) {
        e1.printStackTrace()
    } catch (e1: IOException) {
        e1.printStackTrace()
    } finally {
        try {
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    return null
}

const val UNIT_KB: Long = 1024
const val UNIT_MB = 1024 * 1024.toLong()
const val UNIT_GB = 1024 * 1024 * 1024.toLong()

/**
 * 计算文件大小
 *
 * @param file
 * @author dingpeihua
 * @date 2020/9/10 1:17
 * @version 1.0
 */
fun File?.calculate(): String {
    return calculate(getFolderSize())
}

/**
 * 获取文件夹大小
 *
 * @param parentFile File实例
 * @return long
 */
private fun File?.getFolderSize(): Long {
    if (this == null) {
        return 0
    }
    var size: Long = 0
    try {
        val files = this.listFiles()
        if (files == null || files.isEmpty()) {
            return 0
        }
        for (file in files) {
            size = if (file.isDirectory) {
                size + file.getFolderSize()
            } else {
                size + file.length()
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return size
}

/**
 * 计算文件大小
 *
 * @param length
 * @author dingpeihua
 * @date 2020/9/10 1:17
 * @version 1.0
 */
fun calculate(length: Long): String {
    return when {
        length >= UNIT_GB -> {
            String.format(Locale.US, "%.2f GB", length / 1024f / 1024f / 1024f)
        }
        length >= UNIT_MB -> {
            String.format(Locale.US, "%.2f MB", length / 1024f / 1024f)
        }
        length > UNIT_KB -> {
            String.format(Locale.US, "%.2f KB", length / 1024f)
        }
        else -> {
            String.format(Locale.US, "%.2f B", length.toFloat())
        }
    }
}

fun String?.splitFileName(): Array<String>? {
    if (this == null) {
        return null
    }
    var name = this
    var extension: String? = null
    val i = this.lastIndexOf(".")
    if (i != -1) {
        name = this.substring(0, i)
        extension = this.substring(i)
    }
    if (!name.isNullOrEmpty() && !extension.isNullOrEmpty()) {
        return arrayOf(name, extension)
    }
    return null
}

/**
 * 建立一个MIME类型与文件后缀名的匹配表
 */
val MIME_MAP_TABLE = arrayOf(
    arrayOf(".3gp", "video/3gpp"),
    arrayOf(".apk", "application/vnd.android.package-archive"),
    arrayOf(".asf", "video/x-ms-asf"),
    arrayOf(".avi", "video/x-msvideo"),
    arrayOf(".bin", "application/octet-stream"),
    arrayOf(".bmp", "image/bmp"),
    arrayOf(".c", "text/plain"),
    arrayOf(".class", "application/octet-stream"),
    arrayOf(".conf", "text/plain"),
    arrayOf(".cpp", "text/plain"),
    arrayOf(".doc", "application/msword"),
    arrayOf(".docx", "application/msword"),
    arrayOf(".exe", "application/octet-stream"),
    arrayOf(".gif", "image/gif"),
    arrayOf(".gtar", "application/x-gtar"),
    arrayOf(".gz", "application/x-gzip"),
    arrayOf(".h", "text/plain"),
    arrayOf(".htm", "text/html"),
    arrayOf(".html", "text/html"),
    arrayOf(".jar", "application/java-archive"),
    arrayOf(".java", "text/plain"),
    arrayOf(".jpeg", "image/jpeg"),
    arrayOf(".jpg", "image/jpeg"),
    arrayOf(".js", "application/x-javascript"),
    arrayOf(".log", "text/plain"),
    arrayOf(".m3u", "audio/x-mpegurl"),
    arrayOf(".m4a", "audio/mp4a-latm"),
    arrayOf(".m4b", "audio/mp4a-latm"),
    arrayOf(".m4p", "audio/mp4a-latm"),
    arrayOf(".m4u", "video/vnd.mpegurl"),
    arrayOf(".m4v", "video/x-m4v"),
    arrayOf(".mov", "video/quicktime"),
    arrayOf(".mp2", "audio/x-mpeg"),
    arrayOf(".mp3", "audio/x-mpeg"),
    arrayOf(".mp4", "video/mp4"),
    arrayOf(".mpc", "application/vnd.mpohun.certificate"),
    arrayOf(".mpe", "video/mpeg"),
    arrayOf(".mpeg", "video/mpeg"),
    arrayOf(".mpg", "video/mpeg"),
    arrayOf(".mpg4", "video/mp4"),
    arrayOf(".mpga", "audio/mpeg"),
    arrayOf(".msg", "application/vnd.ms-outlook"),
    arrayOf(".ogg", "audio/ogg"),
    arrayOf(".pdf", "application/pdf"),
    arrayOf(".png", "image/png"),
    arrayOf(".pps", "application/vnd.ms-powerpoint"),
    arrayOf(".ppt", "application/vnd.ms-powerpoint"),
    arrayOf(".prop", "text/plain"),
    arrayOf(".rar", "application/x-rar-compressed"),
    arrayOf(".rc", "text/plain"),
    arrayOf(".rmvb", "audio/x-pn-realaudio"),
    arrayOf(".rtf", "application/rtf"),
    arrayOf(".sh", "text/plain"),
    arrayOf(".tar", "application/x-tar"),
    arrayOf(".tgz", "application/x-compressed"),
    arrayOf(".txt", "text/plain"),
    arrayOf(".wav", "audio/x-wav"),
    arrayOf(".wma", "audio/x-ms-wma"),
    arrayOf(".wmv", "audio/x-ms-wmv"),
    arrayOf(".wps", "application/vnd.ms-works"),
    arrayOf(".xml", "text/plain"),
    arrayOf(".z", "application/x-compress"),
    arrayOf(".zip", "application/zip"),
    arrayOf(".xlsx", "application/vnd.ms-excel"),
    arrayOf(".xls", "application/vnd.ms-excel"),
    arrayOf("", "*/*")
)

/**
 * 根据文件后缀名获得对应的MIME类型。
 *
 * @param file
 */
fun File?.getMIMEType(): String {
    return this?.let {
        var type = "*/*"
        val fName = name
        //获取后缀名前的分隔符"."在fName中的位置。
        val dotIndex = fName.lastIndexOf(".")
        if (dotIndex < 0) {
            return type
        }
        /* 获取文件的后缀名 */
        val end = fName.substring(dotIndex).lowercase(Locale.getDefault())
        if (end === "") return type
        //在MIME和文件类型的匹配表中找到对应的MIME类型。
        for (i in MIME_MAP_TABLE.indices) {
            if (end == MIME_MAP_TABLE[i][0]) {
                type = MIME_MAP_TABLE[i][1]
                break
            }
        }
        return type
    } ?: ""
}

fun Any?.writeLog(logFolder: File, message: String) {
    val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val time: String = dateFormat.format(Date())
    val fileName = "zf-crash-$time.log"
    val logFile = File(logFolder, fileName)
    logFile.write(message)
}

fun Any?.writeLog(logFolder: CharSequence, message: String) {
    writeLog(File(logFolder.toString()), message)
}

fun Any?.writeLog(message: String, logFolderName: CharSequence?) {
    if (logFolderName.isNullOrEmpty()) {
        return
    }
    writeLog(logFolderName, message)
}

fun File.ensureDir(): Boolean {
    try {
        isDirectory.no {
            isFile.yes {
                delete()
            }
            return mkdirs()
        }
    } catch (e: Exception) {
    }
    return false
}

/**
 * 获取指定文件（包含文件夹及其子文件）大小
 *
 * @param f
 * @return
 * @throws Exception
 */
fun String?.getFileSize(): Long {
    if (this.isNullOrEmpty()) {
        return 0
    }
    return File(this).getFileSize()
}

fun String?.formatSize(): String {
    if (this.isNullOrEmpty()) {
        return "0B"
    }
    return File(this).formatSize()
}

/**
 * 获取指定文件（包含文件夹及其子文件）大小
 *
 * @param f
 * @return
 * @throws Exception
 */
fun File?.getFileSize(): Long {
    if (this == null || !this.exists()) {
        return 0
    }
    if (isFile) {
        return length()
    }
    var size: Long = 0
    val files = listFiles()
    if (files != null) {
        for (file in files) {
            size += file.getFileSize()
        }
    }
    return size
}

fun File?.formatSize(): String {
    return getFileSize().formatFileSize()
}

/**
 * 转换文件大小
 *
 * @return
 */
fun Long.formatFileSize(): String {
    if (this == 0L) {
        return "0B"
    }
    return when {
        this < 1024 -> {
            String.format(Locale.US, "%.2fB", this.toFloat())
        }
        this < 1048576 -> {
            String.format(Locale.US, "%.2fKB", this / 1024f)
        }
        this < 1073741824 -> {
            String.format(Locale.US, "%.2fMB", this / 1048576f)
        }
        else -> {
            String.format(Locale.US, "%.2fGB", this / 1073741824f)
        }
    }
}