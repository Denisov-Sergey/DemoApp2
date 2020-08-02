package com.example.demoapp2.gallery_module

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.demoapp2.MainActivity
import com.example.demoapp2.R
import kotlinx.android.synthetic.main.activity_detail_image.*

class detailImage : AppCompatActivity() {

    companion object{
        public val IINTENT_BYTEARRAY = "BYTEARRAY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_image)



    }

    override fun onStart() {
        super.onStart()

        val byteArray  = intent.getByteArrayExtra(IINTENT_BYTEARRAY)
        val bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)
        iv_detail_img.setImageBitmap(bmp)

        btn_back_to_gallery.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("ToGallery", 1)
            startActivity(intent)
        }
    }
}