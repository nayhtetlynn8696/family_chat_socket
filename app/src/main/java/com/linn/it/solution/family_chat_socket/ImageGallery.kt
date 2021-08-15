package com.linn.it.solution.family_chat_socket

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore

object ImageGallery {
    fun listOfImages(context: Context):ArrayList<String>{
        val imageList=ArrayList<String>()
        var cursor:Cursor?=null

        val projection= arrayOf(MediaStore.MediaColumns.DATA,
        MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

        val orderBy=MediaStore.Images.Media.DATE_TAKEN

        val uri:Uri=MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        cursor=context.contentResolver.query(uri,projection,null,null, "$orderBy DESC")

        if (cursor!=null) {
            val columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
            while (cursor.moveToNext()) {
                val absoluteImagePath=cursor.getString(columnIndexData)
                imageList.add(absoluteImagePath)
            }
        }

        return imageList
    }
}