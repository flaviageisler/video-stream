package br.com.videoStream.video

import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

class VideoStreamService {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    fun prepareContent(fileName: String, range: String?): ResponseEntity<ByteArray> {
        var rangeStart: Long = 0
        var rangeEnd: Long
        val data: ByteArray
        val fileSize: Long
        val fileType = getMimeType(fileName)
        try {
            fileSize = getFileSize(fileName)
            if (range == null) {
                return ResponseEntity.status(HttpStatus.OK)
                    .header(CONTENT_TYPE, fileType)
                    .header(CONTENT_LENGTH, fileSize.toString())
                    .body(readByteRange(fileName, rangeStart, fileSize - 1)) // Read the object and convert it as bytes
            }
            val ranges = range.split("-").toTypedArray()
            rangeStart = ranges[0].substring(6).toLong()
            rangeEnd = if (ranges.size > 1) {
                if (ranges[1].isNotEmpty()) ranges[1].toLong() else fileSize - 1
            } else {
                fileSize - 1
            }
            if (fileSize < rangeEnd) {
                rangeEnd = fileSize - 1
            }
            data = readByteRange(fileName, rangeStart, rangeEnd)
        } catch (e: IOException) {
            logger.error("Exception while reading the file {}", e.message)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
        val contentLength = (rangeEnd - rangeStart + 1).toString()
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
            .header(CONTENT_TYPE, fileType)
            .header(HttpHeaders.ACCEPT_RANGES, BYTES)
            .header(CONTENT_LENGTH, contentLength)
            .header(HttpHeaders.CONTENT_RANGE, "$BYTES $rangeStart-$rangeEnd/$fileSize")
            .body(data)
    }

    fun readByteRange(filename: String, start: Long, end: Long): ByteArray {
        val path = Paths.get(filename)
        Files.newInputStream(path).use { inputStream ->
            ByteArrayOutputStream().use { bufferedOutputStream ->
                val data = ByteArray(BYTE_RANGE)
                var nRead: Int
                while (inputStream.read(data, 0, data.size).also { nRead = it } != -1) {
                    bufferedOutputStream.write(data, 0, nRead)
                }
                bufferedOutputStream.flush()
                val result = ByteArray((end - start).toInt() + 1)
                System.arraycopy(bufferedOutputStream.toByteArray(), start.toInt(), result, 0, result.size)
                return result
            }
        }
    }

    private fun getFileSize(fileName: String): Long = Files.size(Paths.get(fileName))

    private fun getMimeType(fileName: String): String = Files.probeContentType(Paths.get(fileName))

}