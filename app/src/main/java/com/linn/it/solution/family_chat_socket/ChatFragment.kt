package com.linn.it.solution.family_chat_socket

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.linn.it.solution.family_chat_socket.databinding.FragmentChatBinding
import io.socket.client.IO
import io.socket.client.Socket
import java.util.*

class ChatFragment : Fragment() {
    private var _binding: FragmentChatBinding? = null
    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!

    private lateinit var mAdapter: MessageAdapter

    val chatId = "7EHGpmXKvStAjak-YDPLTS2r8fhEdf8"
    private var senderId = 1
    private var receiverId = 2

    private val gson: Gson = Gson()
    private val mMessages = ArrayList<MessageVO>()

    private lateinit var mSocket: Socket
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding=FragmentChatBinding.inflate(inflater, container, false)
        val view = binding.root


        binding.rvMessage.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        mAdapter = MessageAdapter()
        binding.rvMessage.adapter = mAdapter

        //Let's connect to our Chat room! :D
        try {
            mSocket =
                IO.socket("http://192.168.0.195:3000")  //http://192.168.144.94:3000 //http://192.168.0.195:3000

            mSocket.on(Socket.EVENT_CONNECT) {
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Connected", Toast.LENGTH_LONG).show()
                }
            }
            mSocket.connect()
        } catch (ex: java.lang.Exception) {
            Toast.makeText(requireContext(), ex.message, Toast.LENGTH_LONG).show()
        }


//        val data =
//            MessageVO(ChatActivity.userName, ChatActivity.userName, senderId, receiverId, "", chatId, getCurrentTime())
//        val jsonData = gson.toJson(data) // Gson changes data object to Json type.
//        mSocket.emit("subscribe", jsonData)
//
//        mSocket.on("newUserToChatRoom") { arr ->
////            val name = arr[0].toString() //This pass the userName!
//            val userData = gson.fromJson(arr[0].toString(), MessageVO::class.java)
////            val name = userData.userName
//            senderId = userData.senderId
//
//            receiverId = if (senderId == 1) {
//                2
//            } else {
//                1
//            }
//
////            val newUser = MessageVO(name, "Join Quick Answer",senderId,receiverId,"",chatId)
//            addItemToRecyclerView(userData)
//            Log.d("LogData", "on New User triggered.")
//        }
//
//        mSocket.on("updateChat") { arr ->
//            val chat: MessageVO = gson.fromJson(arr[0].toString(), MessageVO::class.java)
//            addItemToRecyclerView(chat)
//        }
//
//        mSocket.on("userLeftChatRoom") { arr ->
////            val leftUserName = arr[0].toString()
//            val leftChat: MessageVO = gson.fromJson(arr[0].toString(), MessageVO::class.java)
////            val chat = MessageVO(leftUserName, "Left Quick Answer",senderId,receiverId,"",chatId)
//            addItemToRecyclerView(leftChat)
//        }
//
//        mSocket.on("typing") { arr ->
//            requireActivity().runOnUiThread {
//                val typingData = gson.fromJson(arr[0].toString(), MessageVO::class.java)
//                if (typingData.userName != ChatActivity.userName) {
//                    binding.tvTyping.visibility = View.VISIBLE
//                    binding.tvTyping.text = typingData.messageContent
//                }
//            }
//        }
//
//        mSocket.on("stopTyping") { arr ->
//            requireActivity().runOnUiThread {
//                binding.tvTyping.visibility = View.GONE
//            }
//        }
//
//        binding.btnSend.setOnClickListener {
//            if (binding.edtMessage.text.isNullOrEmpty()) {
//                ImagePicker.with(this)
//                    .createIntent { intent ->
//                        startForProfileImageResult.launch(intent)
//                    }
//            } else {
//                sendMessage(binding.edtMessage.text.toString(),"")
//            }
//        }
//
//        binding.edtMessage.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//
//            }
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                if (p0 == null) {
//                    return
//                }
//                if (p0.isNotEmpty()) {
//                    sendTyping()
//
//                    binding.btnSend.setImageResource(R.drawable.ic_send)
//                } else {
//                    binding.btnSend.setImageResource(R.drawable.ic_image)
//                }
//            }
//
//            override fun afterTextChanged(p0: Editable?) {
//                callStopTyping()
//            }
//
//        })

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    companion object {

    }

//    private fun stopTyping() {
//        mSocket.emit("stopTyping", chatId)
//    }
//
//    private fun sendTyping() {
//        val sendData = MessageVO(
//            ChatActivity.userName,
//            "${ChatActivity.userName} is typing",
//            senderId,
//            receiverId,
//            "",
//            chatId,
//            getCurrentTime()
//        )
//        val jsonData = gson.toJson(sendData)
//
//        mSocket.emit("typing", jsonData)
//    }
//
//    private fun sendMessage(content: String, image: String) {
//        stopTyping()
//
//        val time = getCurrentTime()
//        val sendData = MessageVO(ChatActivity.userName, content, senderId, receiverId, image, chatId, time)
//        val jsonData = gson.toJson(sendData)
//        mSocket.emit("newMessage", jsonData)
//
//        val message = MessageVO(ChatActivity.userName, content, senderId, receiverId, image, chatId, time)
//        addItemToRecyclerView(message)
//    }
//
//    private fun addItemToRecyclerView(message: MessageVO) {
//
//        //Since this function is inside of the listener,
//        //You need to do it on UIThread!
//        requireActivity().runOnUiThread {
//            mMessages.add(message)
//            mAdapter.notifyItemInserted(mMessages.size)
//            binding.edtMessage.setText("")
//            binding.rvMessage.scrollToPosition(mMessages.size - 1) //move focus on last message
//        }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        val data = MessageVO(
//            ChatActivity.userName,
//            "Left Quick Answer",
//            senderId,
//            receiverId,
//            "",
//            chatId,
//            getCurrentTime()
//        )
//        val jsonData = gson.toJson(data)
//
//        //Before disconnecting, send "unsubscribe" event to server so that
//        //server can send "userLeftChatRoom" event to other users in chatroom
//        mSocket.emit("unsubscribe", jsonData)
//        mSocket.disconnect()
//    }
//
//    private fun callStopTyping() {
//        object : CountDownTimer(5000, 1000) {
//            @SuppressLint("SetTextI18n")
//            override fun onTick(millisUntilFinished: Long) {
//
//            }
//
//            override fun onFinish() {
//                stopTyping()
//            }
//        }.start()
//    }
//
//    private fun getCurrentTime(): String {
//        val currentTimeFormat = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
//        return currentTimeFormat.format(Date())
//    }
//
//    private fun encodeImage(bm: Bitmap): String {
//        val baos = ByteArrayOutputStream()
//        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
//        val b = baos.toByteArray()
//        return Base64.encodeToString(b, Base64.DEFAULT)
//    }
//
//
//    private val startForProfileImageResult =
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
//            val resultCode = result.resultCode
//            val data = result.data
//
//            when (resultCode) {
//                Activity.RESULT_OK -> {
//                    //Image Uri will not be null for RESULT_OK
//                    val fileUri = data?.data!!
//
////                    mProfileUri = fileUri
////                    imgProfile.setImageURI(fileUri)
//
//                    val imageStream = requireActivity().contentResolver.openInputStream(fileUri)
//                    val bitmap = BitmapFactory.decodeStream(imageStream)
//
//                    Log.d("LogData:Two", encodeImage(bitmap))
//
//                    sendMessage("",encodeImage(bitmap))
//
//                }
//                ImagePicker.RESULT_ERROR -> {
//                    Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
//                }
//                else -> {
//                    Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
}