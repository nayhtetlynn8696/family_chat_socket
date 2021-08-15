package com.linn.it.solution.family_chat_socket.view

import com.linn.it.solution.family_chat_socket.network.response.FileUploadResponse

interface FileUploadView:BaseView {
    fun successFileUpload(response: FileUploadResponse)
}