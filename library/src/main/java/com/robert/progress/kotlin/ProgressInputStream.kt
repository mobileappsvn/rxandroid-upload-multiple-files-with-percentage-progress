package com.robert.progress.kotlin

import java.io.IOException
import java.io.InputStream

/**
 * Progressive input stream
 */
class ProgressInputStream(private val stream: InputStream?, private val listener: ProgressCallback, private val total: Long) : InputStream() {
    private var totalRead: Long = 0


    @Throws(IOException::class)
    override fun read(): Int {
        val read = this.stream!!.read()
        if (this.total < 0) {
            this.listener.onProgressChanged(-1, -1, -1f)
            return read
        }
        if (read >= 0) {
            this.totalRead++
            this.listener.onProgressChanged(this.totalRead, this.total, this.totalRead * 1.0f / this.total)
        }
        return read
    }

    @Throws(IOException::class)
    override fun read(b: ByteArray, off: Int, len: Int): Int {
        val read = this.stream!!.read(b, off, len)
        if (this.total < 0) {
            this.listener.onProgressChanged(-1, -1, -1f)
            return read
        }
        if (read >= 0) {
            this.totalRead += read.toLong()
            this.listener.onProgressChanged(this.totalRead, this.total, this.totalRead * 1.0f / this.total)
        }
        return read
    }

    @Throws(IOException::class)
    override fun close() {
        if (this.stream != null) {
            this.stream.close()
        }
    }

}