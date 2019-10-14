package com.robert.progress.kotlin

import okhttp3.RequestBody
import okhttp3.ResponseBody

/**
 * Progress callback request body/response body wrapper class
 */
class ProgressHelper {
    companion object {

        /**
         * The package request body is a request body with progress
         *
         * @param requestBody      Request body to be packaged
         * @param progressListener Progress callback
         * @return Request body with progress, requesting with this request body
         */
        fun withProgress(requestBody: RequestBody?, progressListener: ProgressListener?): RequestBody {
            requireNotNull(requestBody) { "requestBody == null" }
            requireNotNull(progressListener) { "progressListener == null" }
            return ProgressRequestBody(requestBody, progressListener)
        }

        /**
         *
         * The package request body is a response body with progress
         *
         * @param responseBody     Responsive body to be packaged
         * @param progressListener Progress callback
         * @return Response body with progress, using this response body to read response data
         */
        fun withProgress(responseBody: ResponseBody?, progressListener: ProgressListener?): ResponseBody {
            requireNotNull(responseBody) { "responseBody == null" }
            requireNotNull(progressListener) { "progressListener == null" }
            return ProgressResponseBody(responseBody, progressListener)
        }
    }
}
