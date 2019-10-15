package com.robert.progress.kotlin

import java.io.IOException

import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink
import okio.Okio

class ProgressMultipartRequestBody(private val mRequestBody: LinkedHashMap<String, RequestBody>, private val progressListener: ProgressCallback?) : RequestBody() {

    override fun contentType(): MediaType? {
        mRequestBody.forEach { (_, requestBody)  ->
            return requestBody.contentType()
        }
        return null
    }

    @Throws(IOException::class)
    override fun contentLength(): Long {
        var contentLength: Long = 0L
        mRequestBody.forEach { (_, requestBody)  ->
            contentLength += requestBody.contentLength()
        }
        return contentLength
    }

    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        if (progressListener == null) {
            mRequestBody.forEach { (_, requestBody)  ->
                requestBody.writeTo(sink)
            }
            return
        }
        val progressOutputStream = ProgressOutputStream(sink.outputStream(), progressListener, contentLength())
        val progressSink = Okio.buffer(Okio.sink(progressOutputStream))

        mRequestBody.forEach { (_, requestBody)  ->
            requestBody.writeTo(progressSink)
        }
        progressSink.flush()
    }

}