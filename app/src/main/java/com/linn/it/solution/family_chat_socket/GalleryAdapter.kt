package com.linn.it.solution.family_chat_socket

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.linn.it.solution.family_chat_socket.databinding.ImageItemBinding

class GalleryAdapter(delegate: ImageDelegate):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var mImageList=ArrayList<String>()
    val mDelegate=delegate

    class GalleryViewHolder(var binding:ImageItemBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding=ImageItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return GalleryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder=holder as GalleryViewHolder

        val layoutParams =
            ViewGroup.MarginLayoutParams(Common.width/3-13, Common.width/3-13)
        layoutParams.setMargins(4, 0, 4, 0)
        viewHolder.binding.cvImage.layoutParams=layoutParams

        val item=mImageList[position]

        Glide.with(viewHolder.itemView).load(item).into(viewHolder.binding.ivImage)

        viewHolder.binding.cvImage.setOnClickListener {
            mDelegate.onClick(item)
        }
    }

    override fun getItemCount(): Int {
        return mImageList.size
    }

    fun setData(images:ArrayList<String>){
        this.mImageList=images
        notifyDataSetChanged()
    }
}