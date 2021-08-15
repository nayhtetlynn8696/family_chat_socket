package com.linn.it.solution.family_chat_socket.network.response

import com.google.gson.annotations.SerializedName

class FileUploadResponse {
    @SerializedName("status")
    var status:Int=0
    @SerializedName("message")
    var message:String?=null
    @SerializedName("img_url")
    var imgUrl:String?=null
}