package com.linn.it.solution.family_chat_socket

import android.graphics.Bitmap

data class MessageVO(
    var userName:String,
    var messageContent:String,
    var senderId:Int,
    var receiverId:Int,
    var photo:String?=null,
    var chatId:String,
    var msgTime:String,
    var image:Bitmap?=null
)