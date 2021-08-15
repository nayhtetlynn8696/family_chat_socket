package com.linn.it.solution.family_chat_socket

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.Gson
import com.linn.it.solution.family_chat_socket.databinding.ActivityChatBinding
import com.linn.it.solution.family_chat_socket.network.response.FileUploadResponse
import com.linn.it.solution.family_chat_socket.view.FileUploadView
import com.linn.it.solution.family_chat_socket.view_model.FileUploadViewModel
import io.socket.client.IO
import io.socket.client.Socket
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ChatActivity : AppCompatActivity(), ImageDelegate, FileUploadView {
    private lateinit var binding: ActivityChatBinding

    private lateinit var mFileUploadViewModel: FileUploadViewModel

    //    private var imageCapture:ImageCapture?=null
    private lateinit var outputDirectory: File

    private lateinit var mAdapter: MessageAdapter
    private lateinit var mGalleryAdapter: GalleryAdapter

    private var userName = ""
    val chatId = "7EHGpmXKvStAjak-YDPLTS2r8fhEdf8"

    private var time=""
    private var senderId = 1
    private var receiverId = 2

    private val gson: Gson = Gson()
    private val mMessages = ArrayList<MessageVO>()

    private lateinit var mSocket: Socket
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        val root: View = binding.root
        setContentView(root)

        mFileUploadViewModel = ViewModelProvider(this).get(FileUploadViewModel::class.java)
        mFileUploadViewModel.setViewFileUpload(this)

        outputDirectory = getOutputDirectory()

        val user = intent.getBundleExtra(Common.USER) ?: return
        userName = user.getString(Common.NAME)!!
        val description = user.getString(Common.ADDRESS)!!
        title = "Quick Answer"
        Common.SENDER = userName

        val resources = this.resources // get resources from context
        Common.width = resources.displayMetrics.widthPixels

        binding.chatLayout.rvMessage.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mAdapter = MessageAdapter()
        binding.chatLayout.rvMessage.adapter = mAdapter

        binding.rvGallery.setHasFixedSize(true)
        binding.rvGallery.layoutManager = GridLayoutManager(this, 3)
//            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mGalleryAdapter = GalleryAdapter(this)
        binding.rvGallery.adapter = mGalleryAdapter

        //Let's connect to our Chat room! :D
        try {
            mSocket =
                IO.socket("http://192.168.144.45:3000")  //employee=http://192.168.144.94:3000  mpt=http://192.168.0.195:3000

            mSocket.on(Socket.EVENT_CONNECT) {
                runOnUiThread {
                    Toast.makeText(this@ChatActivity, "Connected", Toast.LENGTH_LONG).show()
                }
            }
            mSocket.connect()
        } catch (ex: Exception) {
            Toast.makeText(this@ChatActivity, ex.message, Toast.LENGTH_LONG).show()
        }

        mAdapter.setData(mMessages)

//        if (allPermissionGranted()) {
//            startCamera()
//        } else {
//            ActivityCompat.requestPermissions(
//                this,
//                Common.REQUIRED_PERMISSIONS,
//                Common.REQUEST_CODE_PERMISSIONS
//            )
//        }

        val data =
            MessageVO(userName, description, senderId, receiverId, null, chatId, getCurrentTime())
        val jsonData = gson.toJson(data) // Gson changes data object to Json type.
        mSocket.emit("subscribe", jsonData)

        mSocket.on("newUserToChatRoom") { arr ->
//            val name = arr[0].toString() //This pass the userName!
            val userData = gson.fromJson(arr[0].toString(), MessageVO::class.java)

//            val newUser = MessageVO(name, "Join Quick Answer",senderId,receiverId,"",chatId)
            addItemToRecyclerView(userData)
            Log.d("LogData", "on New User triggered.")
        }

        mSocket.on("updateChat") { arr ->
            val chat: MessageVO = gson.fromJson(arr[0].toString(), MessageVO::class.java)
            addItemToRecyclerView(chat)
        }

        mSocket.on("userLeftChatRoom") { arr ->
//            val leftUserName = arr[0].toString()
            val leftChat: MessageVO = gson.fromJson(arr[0].toString(), MessageVO::class.java)
//            val chat = MessageVO(leftUserName, "Left Quick Answer",senderId,receiverId,"",chatId)
            addItemToRecyclerView(leftChat)
        }

        mSocket.on("typing") { arr ->
            runOnUiThread {
                val typingData = gson.fromJson(arr[0].toString(), MessageVO::class.java)
                if (typingData.userName != userName) {
                    binding.chatLayout.tvTyping.visibility = View.VISIBLE
                    binding.chatLayout.tvTyping.text = typingData.messageContent
                }
            }
        }

        mSocket.on("stopTyping") { arr ->
            runOnUiThread {
                binding.chatLayout.tvTyping.visibility = View.GONE
            }
        }

        binding.chatLayout.btnSend.setOnClickListener {
            if (binding.chatLayout.edtMessage.text.isNullOrEmpty()) {
                ImagePicker.with(this)
                    .createIntent { intent ->
                        startForProfileImageResult.launch(intent)
                    }


//                if (galleryPermissionGranted()) {
//                    loadImage()
//                } else {
//                    ActivityCompat.requestPermissions(
//                        this,
//                        Common.REQUIRED_GALLERY_PERMISSIONS,
//                        Common.READ_PERMISSIONS_CODE
//                    )
//                }
            } else {
                sendMessage(binding.chatLayout.edtMessage.text.toString(), null)
            }
        }

        binding.chatLayout.edtMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0 == null) {
                    return
                }
                if (p0.isNotEmpty()) {
                    sendTyping()

                    binding.chatLayout.btnSend.setImageResource(R.drawable.ic_send)
                } else {
                    binding.chatLayout.btnSend.setImageResource(R.drawable.ic_image)
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                callStopTyping()
            }

        })

        binding.cameraLayout.btnCapture.setOnClickListener {
//            takePhoto()
        }
    }

    private fun loadImage() {
        binding.rvGallery.visibility = View.VISIBLE
        val imageList = ImageGallery.listOfImages(this)
        mGalleryAdapter.setData(imageList)
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull().let { mFile ->
            File(mFile, resources.getString(R.string.app_name)).apply {
                mkdir()
            }
        }
        return if (mediaDir.exists()) {
            mediaDir
        } else {
            filesDir
        }
    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            when (resultCode) {
                Activity.RESULT_OK -> {
                    //Image Uri will not be null for RESULT_OK
                    val fileUri = data?.data!!

                    val imageStream = contentResolver.openInputStream(fileUri)
                    val bitmap = BitmapFactory.decodeStream(imageStream)

                    Log.d("LogData", encodeImage(bitmap))

                    mFileUploadViewModel.uploadFile(encodeImage(bitmap))
                    time = getCurrentTime()
                    val message = MessageVO(userName, "", senderId, receiverId, null, chatId, time,bitmap)
                    addItemToRecyclerView(message)

                }
                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
                }
            }
        }

//    private fun takePhoto() {
//        val imageCapture=imageCapture ?: return
//        val photoFile=File(
//            outputDirectory,
//            SimpleDateFormat(Common.FILE_NAME_FORMAT,Locale.getDefault()).format(System.currentTimeMillis())+".jpg"
//        )
//
//        val outputOption=ImageCapture
//            .OutputFileOptions
//            .Builder(photoFile)
//            .build()
//
//        imageCapture.takePicture(
//            outputOption,ContextCompat.getMainExecutor(this),
//            object : ImageCapture.OnImageSavedCallback{
//                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
//                    val saveUri=Uri.fromFile(photoFile)
//                    binding.chatLayout.chatView.visibility=View.VISIBLE
//                    binding.cameraLayout.cameraView.visibility=View.GONE
//
//                    val imageStream = contentResolver.openInputStream(saveUri)
//                    val bitmap = BitmapFactory.decodeStream(imageStream)
//
//                    Log.d("LogData:Two", encodeImage(bitmap))
//
//                    sendMessage("", "http://192.168.144.45/img/aggrement.png")
//
//                    Toast.makeText(this@ChatActivity,"Save = $saveUri",Toast.LENGTH_LONG).show()
//                }
//
//                override fun onError(exception: ImageCaptureException) {
//                    Log.e(Common.TAG,"onError:${exception.message}",exception)
//                }
//
//            }
//        )
//    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == Common.REQUEST_CODE_PERMISSIONS) {
//            if (allPermissionGranted()) {
//                startCamera()
//            } else {
//                Toast.makeText(this, "Permissions not granted by the user", Toast.LENGTH_LONG)
//                    .show()
//            }
//        }else if (requestCode == Common.READ_PERMISSIONS_CODE){
//            if (galleryPermissionGranted()){
//                loadImage()
//            }else{
//                Toast.makeText(this, "Permissions not granted by the user", Toast.LENGTH_LONG)
//                    .show()
//            }
//        }
//    }

//    private fun startCamera() {
//        binding.chatLayout.chatView.visibility=View.GONE
//        binding.cameraLayout.cameraView.visibility=View.VISIBLE
//        val cameraProviderFeature = ProcessCameraProvider.getInstance(this)
//
//        cameraProviderFeature.addListener({
//            val cameraProvider:ProcessCameraProvider=cameraProviderFeature.get()
//            val preview = Preview.Builder()
//                .build()
//                .also { mPreview->
//                    mPreview.setSurfaceProvider(
//                        binding.cameraLayout.cameraPreview.surfaceProvider
//                    )
//
//                }
//
//            imageCapture=ImageCapture.Builder().build()
//
//            val cameraSelector=CameraSelector.DEFAULT_BACK_CAMERA
//
//            try {
//                cameraProvider.unbindAll()
//                cameraProvider.bindToLifecycle(this,cameraSelector,
//                    preview,imageCapture)
//            }catch (e:Exception){
//                Log.d(Common.TAG,e.message.toString())
//            }
//        }, ContextCompat.getMainExecutor(this))
//    }

    private fun allPermissionGranted() =
        Common.REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(baseContext, it) ==
                    PackageManager.PERMISSION_GRANTED
        }

    private fun galleryPermissionGranted() =
        Common.REQUIRED_GALLERY_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(baseContext, it) ==
                    PackageManager.PERMISSION_GRANTED
        }

    private fun stopTyping() {
        mSocket.emit("stopTyping", chatId)
    }

    private fun sendTyping() {
        val sendData = MessageVO(
            userName,
            "$userName is typing",
            senderId,
            receiverId,
            null,
            chatId,
            getCurrentTime()
        )
        val jsonData = gson.toJson(sendData)

        mSocket.emit("typing", jsonData)
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, ChatActivity::class.java)
        }
    }

    private fun sendMessage(content: String, image: String?) {
        stopTyping()

        val time = getCurrentTime()
        val sendData = MessageVO(userName, content, senderId, receiverId, image, chatId, time)
        val jsonData = gson.toJson(sendData)
        mSocket.emit("newMessage", jsonData)

        if (image == null) {
            val message = MessageVO(userName, content, senderId, receiverId, image, chatId, time)
            addItemToRecyclerView(message)
        }
    }

    private fun addItemToRecyclerView(message: MessageVO) {

        //Since this function is inside of the listener,
        //You need to do it on UIThread!
        runOnUiThread {
            mMessages.add(message)
            mAdapter.notifyItemInserted(mMessages.size)
            binding.chatLayout.edtMessage.setText("")
            binding.chatLayout.rvMessage.scrollToPosition(mMessages.size - 1) //move focus on last message
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val data = MessageVO(
            userName,
            "Left Quick Answer",
            senderId,
            receiverId,
            null,
            chatId,
            getCurrentTime()
        )
        val jsonData = gson.toJson(data)

        //Before disconnecting, send "unsubscribe" event to server so that
        //server can send "userLeftChatRoom" event to other users in chatroom
        mSocket.emit("unsubscribe", jsonData)
        mSocket.disconnect()
    }

    private fun callStopTyping() {
        object : CountDownTimer(5000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {
                stopTyping()
            }
        }.start()
    }

    private fun getCurrentTime(): String {
        val currentTimeFormat = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
        return currentTimeFormat.format(Date())
    }

    private fun encodeImage(bm: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    private fun createImageFile(file: File): MultipartBody.Part {
        return MultipartBody.Part.createFormData(
            "img",
            file.name,
            file.asRequestBody("image/*".toMediaType())
        )
    }

    override fun onClick(path: String) {
        runOnUiThread {
            binding.rvGallery.visibility = View.GONE

            val bitmap = BitmapFactory.decodeFile(path)

            val file = File(path)
//            Log.d("LogData:Two", encodeImage(bitmap))

//            mFileUploadViewModel.uploadFile(createImageFile(file))
//            sendMessage("", "http://192.168.144.45/img/aggrement.png")
        }
    }

    override fun successFileUpload(response: FileUploadResponse) {
        val message = MessageVO(userName, "", senderId, receiverId, response.imgUrl, chatId, time)
        val jsonData = gson.toJson(message)
        mSocket.emit("newMessage", jsonData)
    }

    override fun showError(message: String, code: Int) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun showNetworkFailed() {
        Toast.makeText(this, "Network Error", Toast.LENGTH_LONG).show()
    }

    override fun dismiss() {

    }

    override fun showProgress() {

    }
}