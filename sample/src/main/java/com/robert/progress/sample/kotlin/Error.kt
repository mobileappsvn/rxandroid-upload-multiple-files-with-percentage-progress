package com.robert.progress.sample.kotlin

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Error {

    @SerializedName("code")
    @Expose
    var code: Int? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    fun withCode(code: Int?): Error {
        this.code = code
        return this
    }

    fun withMessage(message: String?): Error {
        this.message = message
        return this
    }

}
