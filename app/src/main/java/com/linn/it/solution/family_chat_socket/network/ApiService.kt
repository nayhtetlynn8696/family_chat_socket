package com.linn.it.solution.family_chat_socket.network

import com.linn.it.solution.family_chat_socket.network.EndPoint.Companion.UPLOAD_IMAGE
import com.linn.it.solution.family_chat_socket.network.response.FileUploadResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST(UPLOAD_IMAGE)
    fun uploadFile(@Part("img") image:String): Call<FileUploadResponse>
}