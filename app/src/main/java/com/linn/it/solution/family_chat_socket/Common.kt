package com.linn.it.solution.family_chat_socket

import android.Manifest

object Common {
    const val NAME="name"
    const val ADDRESS="address"
    const val CHAT_ID="chat_id"
    const val USER="user"

    var SENDER=""
    var width=300

    const val LEFT = 1
    const val RIGHT = 2

    const val TAG="cameraX"
    const val FILE_NAME_FORMAT="yy-MM-dd-HH-ss-SSS"
    const val REQUEST_CODE_PERMISSIONS=123
    val REQUIRED_PERMISSIONS= arrayOf(Manifest.permission.CAMERA)

    val REQUIRED_GALLERY_PERMISSIONS= arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    const val READ_PERMISSIONS_CODE=101
}