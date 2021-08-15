package com.linn.it.solution.family_chat_socket.view

interface BaseView {
    fun showError(message:String,code:Int)
    fun showNetworkFailed()
    fun dismiss()
    fun showProgress()
}