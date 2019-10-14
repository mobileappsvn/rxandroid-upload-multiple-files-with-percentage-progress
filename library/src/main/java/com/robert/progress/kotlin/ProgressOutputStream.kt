package com.robert.progress.kotlin

import java.io.IOException
import java.io.OutputStream

/**
 * Output stream with progress
 */
class ProgressOutputStream(private val stream: OutputStream?, private val listener: ProgressCallback, private val total: Long) : OutputStream() {
    private var totalWritten: Long = 0

    @Throws(IOException::class)
    override fun write(b: ByteArray, off: Int, len: Int) {
        this.stream!!.write(b, off, len)
        if (this.total < 0) {
            this.listener.onProgressChanged(-1, -1, -1f)
            return
        }
        if (len < b.size) {
            this.totalWritten += len.toLong()
        } else {
            this.totalWritten += b.size.toLong()
        }
        this.listener.onProgressChanged(this.totalWritten, this.total, this.totalWritten * 1.0f / this.total)
    }

    @Throws(IOException::class)
    override fun write(b: Int) {
        this.stream!!.write(b)
        if (this.total < 0) {
            this.listener.onProgressChanged(-1, -1, -1f)
            return
        }
        this.totalWritten++
        this.listener.onProgressChanged(this.totalWritten, this.total, this.totalWritten * 1.0f / this.total)
    }

    @Throws(IOException::class)
    override fun close() {
        if (this.stream != null) {
            this.stream.close()
        }
    }

    @Throws(IOException::class)
    override fun flush() {
        if (this.stream != null) {
            this.stream.flush()
        }
    }
}
