package com.robert.progress.sample.kotlin

import okhttp3.RequestBody
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface UploadApiRepository {

    @Multipart
    @POST("UploadMultiplePartFile/")
    fun uploadMultipleFiles(
            @Field("username") username: String,
            @Field("password") password: String,
            @Field("email") email: String,
            @PartMap files: LinkedHashMap<String, @JvmSuppressWildcards RequestBody>
    ): Observable<UploadMultipleFilesResponse>

    @POST("UploadMultiplePartFile/")
    fun uploadMultipleFiles(@Body body: RequestBody): Observable<UploadMultipleFilesResponse>

    @Multipart
    @POST("UploadMultiplePartFile/")
    fun uploadMultipleFiles(@PartMap body: LinkedHashMap<String, @JvmSuppressWildcards RequestBody>): Observable<UploadMultipleFilesResponse>

}