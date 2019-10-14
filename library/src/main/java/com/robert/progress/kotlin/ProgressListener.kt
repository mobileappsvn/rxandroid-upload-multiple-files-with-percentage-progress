package com.robert.progress.kotlin

/**
 * Progress callback
 */
abstract class ProgressListener : ProgressCallback {
    private var started: Boolean = false
    private var lastRefreshTime = 0L
    private var lastBytesWritten = 0L
    private var minTime = 100   //Minimum callback time of 100ms, avoid frequent callbacks

    /**
     * The progress has changed. If numBytes, totalBytes, and percent are both -1, the total size is not available.
     *
     * @param numBytes   Read/write size
     * @param totalBytes Total size
     * @param percent    Percentage
     */
    override fun onProgressChanged(numBytes: Long, totalBytes: Long, percent: Float) {
        if (!started) {
            onProgressStart(totalBytes)
            started = true
        }
        if (numBytes == -1L && totalBytes == -1L && percent == -1f) {
            onProgressChanged(-1, -1, -1f, -1f)
            return
        }
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastRefreshTime >= minTime || numBytes == totalBytes || percent >= 1f) {
            var intervalTime = currentTime - lastRefreshTime
            if (intervalTime == 0L) {
                intervalTime += 1
            }
            val updateBytes = numBytes - lastBytesWritten
            val networkSpeed = updateBytes / intervalTime
            onProgressChanged(numBytes, totalBytes, percent, networkSpeed.toFloat())
            lastRefreshTime = System.currentTimeMillis()
            lastBytesWritten = numBytes
        }
        if (numBytes == totalBytes || percent >= 1f) {
            onProgressFinish()
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
    abstract fun onProgressChanged(numBytes: Long, totalBytes: Long, percent: Float, speed: Float)

    /**
     * Start of progress
     *
     * @param totalBytes Total size
     */
    open fun onProgressStart(totalBytes: Long) {

    }

    /**
     * End of progress
     */
    open fun onProgressFinish() {

    }
}
