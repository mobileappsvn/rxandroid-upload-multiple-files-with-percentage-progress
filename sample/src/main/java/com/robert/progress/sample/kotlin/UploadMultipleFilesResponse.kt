package com.robert.progress.sample.kotlin

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UploadMultipleFilesResponse {

    @SerializedName("error")
    @Expose
    var error: Error? = null

    @SerializedName("result")
    @Expose
    var result: MutableList<String>? = null

    @SerializedName("status")
    @Expose
    var status: Boolean? = null

    fun withResult(result: MutableList<String>): UploadMultipleFilesResponse {
        this.result = result
        return this
    }

    fun withStatus(status: Boolean?): UploadMultipleFilesResponse {
        this.status = status
        return this
    }

    fun withError(error: Error?): UploadMultipleFilesResponse {
        this.error = error
        return this
    }

}
