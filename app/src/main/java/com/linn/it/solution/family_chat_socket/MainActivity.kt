package com.linn.it.solution.family_chat_socket

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.linn.it.solution.family_chat_socket.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        val root: View =binding.root
        setContentView(root)

        binding.btnStart.setOnClickListener {
            if (validate()){
                val name=binding.edtName.text.toString().trim()
                val room=binding.edtAddress.text.toString().trim()

                val bundle=Bundle()
                bundle.putString(Common.NAME,name)
                bundle.putString(Common.ADDRESS,room)
                startActivity(ChatActivity.newIntent(this).putExtra(Common.USER,bundle))
            }
        }
    }

    private fun validate():Boolean{
        var status=true

        if (binding.edtName.text.isNullOrEmpty()){
            status=false
            Toast.makeText(this,getString(R.string.enter_username_or_phone), Toast.LENGTH_LONG).show()
        }

        if (binding.edtAddress.text.isNullOrEmpty()){
            status=false
            Toast.makeText(this,getString(R.string.enter_address), Toast.LENGTH_LONG).show()
        }

        return status
    }
}