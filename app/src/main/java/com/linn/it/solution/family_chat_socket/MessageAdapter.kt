package com.linn.it.solution.family_chat_socket

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.linn.it.solution.family_chat_socket.databinding.MyMessageItemBinding
import com.linn.it.solution.family_chat_socket.databinding.OtherMessageItemBinding

class MessageAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var mMessages = ArrayList<MessageVO>()

    class MessageViewHolder : RecyclerView.ViewHolder {
        var myMessageBinding: MyMessageItemBinding? = null
        var otherMessageBinding: OtherMessageItemBinding? = null

        constructor(binding: MyMessageItemBinding) : super(binding.root) {
            myMessageBinding = binding
        }

        constructor(binding: OtherMessageItemBinding) : super(binding.root) {
            otherMessageBinding = binding
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == Common.RIGHT) {
            val binding =
                MyMessageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            MessageViewHolder(binding)
        } else {
            val binding =
                OtherMessageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            MessageViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as MessageViewHolder

        val message = mMessages[position]

        if (holder.itemViewType == Common.RIGHT) {
            viewHolder.myMessageBinding!!.tvMessage.maxWidth=Common.width-100
            viewHolder.myMessageBinding!!.tvMessage.text = message.messageContent
            viewHolder.myMessageBinding!!.tvTime.text = message.msgTime

            if (message.photo!=null || message.image!=null){
                viewHolder.myMessageBinding!!.ivMessage.visibility=View.VISIBLE
                viewHolder.myMessageBinding!!.tvMessage.visibility=View.GONE
                viewHolder.myMessageBinding!!.ivMessage.maxWidth=Common.width-200

                if (message.photo!=null) {
                    Glide.with(viewHolder.itemView).load(message.photo)
                        .into(viewHolder.myMessageBinding!!.ivMessage)
                }else{
                    viewHolder.myMessageBinding!!.ivMessage.setImageBitmap(message.image)
                }
            }else{
                viewHolder.myMessageBinding!!.ivMessage.visibility=View.GONE
                viewHolder.myMessageBinding!!.tvMessage.visibility=View.VISIBLE
            }
        } else {
            viewHolder.otherMessageBinding!!.tvMessage.maxWidth=Common.width-100
            viewHolder.otherMessageBinding!!.tvMessage.text = message.messageContent
            viewHolder.otherMessageBinding!!.tvTime.text = message.msgTime

            if (message.photo!=null || message.image!=null){
                viewHolder.otherMessageBinding!!.ivMessage.visibility=View.VISIBLE
                viewHolder.otherMessageBinding!!.tvMessage.visibility=View.GONE
                viewHolder.otherMessageBinding!!.ivMessage.maxWidth=Common.width-200

                if (message.photo!=null) {
                    Glide.with(viewHolder.itemView).load(message.photo)
                        .into(viewHolder.otherMessageBinding!!.ivMessage)
                }else{
                    viewHolder.otherMessageBinding!!.ivMessage.setImageBitmap(message.image)
                }
            }else{
                viewHolder.otherMessageBinding!!.ivMessage.visibility=View.GONE
                viewHolder.otherMessageBinding!!.tvMessage.visibility=View.VISIBLE
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (mMessages[position].userName == Common.SENDER) {
            Common.RIGHT
        } else {
            Common.LEFT
        }

    }

    override fun getItemCount(): Int {
        return mMessages.size
    }

    fun setData(messages: ArrayList<MessageVO>) {
        this.mMessages = messages
        notifyDataSetChanged()
    }

//    private fun convertBitmap(base64String: String): Bitmap {
//        val baseArr=base64String.split(",")
//        val decodedString: ByteArray = if (baseArr.size==1){
//            Base64.decode(base64String, Base64.DEFAULT)
//        }else{
//            Base64.decode(base64String.split(",")[1], Base64.DEFAULT)
//        }
//        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
//    }
}