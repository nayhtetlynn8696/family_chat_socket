package com.linn.it.solution.family_chat_socket.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.linn.it.solution.family_chat_socket.network.RestClient
import com.linn.it.solution.family_chat_socket.network.response.FileUploadResponse
import com.linn.it.solution.family_chat_socket.view.FileUploadView
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FileUploadViewModel(application: Application): AndroidViewModel(application) {
    var fileUploadView:FileUploadView?=null

    fun setViewFileUpload(fileUploadView: FileUploadView){
        this.fileUploadView=fileUploadView
    }

    fun uploadFile(file: String){
        fileUploadView!!.showProgress()
        RestClient.getApiService().uploadFile(file)
            .enqueue(object : Callback<FileUploadResponse>{
                override fun onResponse(
                    call: Call<FileUploadResponse>,
                    response: Response<FileUploadResponse>
                ) {
                    fileUploadView!!.dismiss()
                    if (response.isSuccessful){
                        val mResponse=response.body()!!
                        if (mResponse.status==1){
                            fileUploadView!!.successFileUpload(mResponse)
                        }else{
                            fileUploadView!!.showError(mResponse.message!!,0)
                        }
                    }else{
                        fileUploadView!!.showError("Uploading Error",0)
                    }
                }

                override fun onFailure(call: Call<FileUploadResponse>, t: Throwable) {
                    fileUploadView!!.dismiss()
                    fileUploadView!!.showNetworkFailed()
                }

            })
    }
}