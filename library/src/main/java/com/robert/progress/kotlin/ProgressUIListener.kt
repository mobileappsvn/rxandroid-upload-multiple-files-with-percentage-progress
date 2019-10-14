package com.robert.progress.kotlin

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message

/**
 * Stream read and write progress ui callback
 */
abstract class ProgressUIListener : ProgressListener() {
    private var mHandler: Handler? = null

    private fun ensureHandler() {
        if (mHandler != null) {
            return
        }
        synchronized(ProgressUIListener::class.java) {
            if (mHandler == null) {
                mHandler = object : Handler(Looper.getMainLooper()) {
                    override fun handleMessage(msg: Message?) {
                        if (msg == null) {
                            return
                        }
                        when (msg.what) {
                            WHAT_START -> {
                                val startData = msg.data ?: return
                                onUIProgressStart(startData.getLong(TOTAL_BYTES))
                            }
                            WHAT_UPDATE -> {
                                val updateData = msg.data ?: return
                                val numBytes = updateData.getLong(CURRENT_BYTES)
                                val totalBytes = updateData.getLong(TOTAL_BYTES)
                                val percent = updateData.getFloat(PERCENT)
                                val speed = updateData.getFloat(SPEED)
                                onUIProgressChanged(numBytes, totalBytes, percent, speed)
                            }
                            WHAT_FINISH -> onUIProgressFinish()
                            else -> {
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * The progress has changed. If numBytes, totalBytes, percent, speed are both -1, the total size is not available.
     *
     * @param numBytes   Read/write size
     * @param totalBytes Total size
     * @param percent    Percentage
     * @param speed      Speed ​​bytes/ms
     */
    override fun onProgressChanged(numBytes: Long, totalBytes: Long, percent: Float, speed: Float) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            onUIProgressChanged(numBytes, totalBytes, percent, speed)
            return
        }
        ensureHandler()
        val message = mHandler!!.obtainMessage()
        message.what = WHAT_UPDATE
        val data = Bundle()
        data.putLong(CURRENT_BYTES, numBytes)
        data.putLong(TOTAL_BYTES, totalBytes)
        data.putFloat(PERCENT, percent)
        data.putFloat(SPEED, speed)
        message.data = data
        mHandler!!.sendMessage(message)
    }


    /**
     * Start of progress
     *
     * @param totalBytes Total size
     */
    override fun onProgressStart(totalBytes: Long) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            onUIProgressStart(totalBytes)
            return
        }
        ensureHandler()
        val message = mHandler!!.obtainMessage()
        message.what = WHAT_START
        val data = Bundle()
        data.putLong(TOTAL_BYTES, totalBytes)
        message.data = data
        mHandler!!.sendMessage(message)
    }

    /**
     * End of progress
     */
    override fun onProgressFinish() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            onUIProgressFinish()
            return
        }
        ensureHandler()
        val message = mHandler!!.obtainMessage()
        message.what = WHAT_FINISH
        mHandler!!.sendMessage(message)
    }

    /**
     * The progress has changed. If numBytes, totalBytes, percent, speed are both -1, the total size is not available.
     *
     * @param numBytes   Read/write size
     * @param totalBytes Total size
     * @param percent    Percentage
     * @param speed      Speed ​​bytes/ms
     */
    abstract fun onUIProgressChanged(numBytes: Long, totalBytes: Long, percent: Float, speed: Float)


    /**
     * Start of progress
     *
     * @param totalBytes Total size
     */
    open fun onUIProgressStart(totalBytes: Long) {

    }

    /**
     * End of progress
     */
    open fun onUIProgressFinish() {

    }

    companion object {

        private const val WHAT_START = 0x01
        private const val WHAT_UPDATE = 0x02
        private const val WHAT_FINISH = 0x03
        private const val CURRENT_BYTES = "numBytes"
        private const val TOTAL_BYTES = "totalBytes"
        private const val PERCENT = "percent"
        private const val SPEED = "speed"
    }
}
