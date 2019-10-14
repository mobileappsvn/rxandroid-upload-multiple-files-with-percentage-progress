package com.robert.progress.kotlin

/**
 * Stream read and write progress
 */
interface ProgressCallback {

    /**
     * The progress has changed. If numBytes, totalBytes, and percent are both -1, the total size is not available.
     *
     * @param numBytes Read/write size
     * @param totalBytes Total size
     * @param percent Percentage
     */
    fun onProgressChanged(numBytes: Long, totalBytes: Long, percent: Float)
}