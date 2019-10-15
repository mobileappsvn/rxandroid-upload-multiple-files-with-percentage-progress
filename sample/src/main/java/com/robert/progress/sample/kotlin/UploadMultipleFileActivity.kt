package com.robert.progress.sample.kotlin

import android.Manifest
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.robert.progress.kotlin.ProgressHelper
import com.robert.progress.kotlin.ProgressUIListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import pub.devrel.easypermissions.EasyPermissions
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.util.*

class UploadMultipleFileActivity: AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    private var uri: Uri? = null
    private var data: Intent? = null
    lateinit var uploadService: UploadApiRepository
    private var disposables: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()

        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BASIC
        val okHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()

        uploadService = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(UploadApiRepository::class.java)
    }

    private fun initView() {

        selectImageUpload.setOnClickListener {
            uploadProgress!!.progress = 0

            val openGalleryIntent = Intent(Intent.ACTION_PICK)
            openGalleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            openGalleryIntent.type = "image/*"
            startActivityForResult(openGalleryIntent, REQUEST_GALLERY_CODE)
        }
    }


    private fun upload(filePaths: ArrayList<String>) {

        //TODO======================= Preparing data =================================
        val bodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)

        for (i in filePaths.indices) {
            val file = File(filePaths[i])
            bodyBuilder.addFormDataPart("file[]", file.name, RequestBody.create(MediaType.parse("multipart/form-data"), file))
        }

        bodyBuilder.addFormDataPart("username", "Robert")
        bodyBuilder.addFormDataPart("password", "123456!")
        bodyBuilder.addFormDataPart("email", "mobile.apps.pro.vn@gmail.com")

        val build = bodyBuilder.build()
        val requestBody = ProgressHelper.withProgress(build, object : ProgressUIListener() {

            override fun onUIProgressStart(totalBytes: Long) {
                super.onUIProgressStart(totalBytes)
                Log.e(TAG,  "=============Starting upload===============")
                Toast.makeText(applicationContext, "Start upload with Total Bytes=$totalBytes", Toast.LENGTH_SHORT).show()
            }

            override fun onUIProgressChanged(numBytes: Long, totalBytes: Long, percent: Float, speed: Float) {

                Log.e(TAG,  "--->NumBytes/totalBytes:$numBytes/$totalBytes")
                Log.e(TAG,  "--->Percent:$percent")
                Log.e(TAG,  "--->Speed:$speed")

                uploadProgress!!.progress = (100 * percent).toInt()
                uploadInfo!!.text = " NumBytes/TotalBytes:$numBytes/$totalBytes Bytes\n Percent:${(100 * percent).toInt()}%\n Speed: ${speed * 1000 / 1024f / 1024f} MB/Second"
            }

            override fun onUIProgressFinish() {
                super.onUIProgressFinish()
                Log.e(TAG,  "============= End Upload ===============")
                Toast.makeText(applicationContext, "Upload completed!", Toast.LENGTH_SHORT).show()
            }
        })

        disposables.add(uploadService.uploadMultipleFiles(requestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<UploadMultipleFilesResponse>() {

                    override fun onStart() {
                        super.onStart()
                        //Show progress bar
                    }

                    override fun onNext(response: UploadMultipleFilesResponse) {
                        //Parsing error if any from respond for notification to user
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        //Dismiss progress bar
                    }

                    override fun onComplete() {
                        //Dismiss progress bar
                    }
                }))

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_GALLERY_CODE && resultCode == RESULT_OK) {

            this.data = data
            this.uri = data!!.data

            if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                val filePaths = parsingFilePath(data)
                if (filePaths.isNotEmpty()) {
                    upload(filePaths)
                }
            } else {
                EasyPermissions.requestPermissions(this, getString(R.string.read_file), READ_REQUEST_CODE, Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this@UploadMultipleFileActivity)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        if (data != null) {
            val filePaths = parsingFilePath(data)
            if (filePaths.isNotEmpty()) {
                upload(filePaths)
            }
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        Log.d(TAG, "Permission has been denied")
    }

    private fun parsingFilePath(data: Intent?): ArrayList<String> {
        val filePaths = ArrayList<String>()
        //ArrayList<Uri> uris = new ArrayList<>();

        if (data != null && data.clipData != null) {
            val clipData = data.clipData

            for (i in 0 until clipData!!.itemCount) {
                val item = clipData.getItemAt(i)
                val uri = item.uri
                val filePath = getRealPathFromURIPath(uri)
                filePath?.let { filePaths.add(it) }
                //uris.add(uri);

                Log.e(TAG,  "filePath[$i]=$filePath")
            }
        }

        return filePaths
    }

    private fun getRealPathFromURIPath(contentURI: Uri): String? {
        var cursor: Cursor? = null
        try {
            cursor = contentResolver.query(contentURI, null, null, null, null)
            if (cursor == null) {
                return contentURI.path
            }
            cursor.moveToFirst()
            return cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA))
        } finally {
            cursor?.close()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val REQUEST_GALLERY_CODE = 200
        private const val READ_REQUEST_CODE = 300
        private const val BASE_URL = "http://192.168.10.10/api/"
        private const val url = "http://192.168.10.10/api/UploadMultiplePartFile/"
    }
}
