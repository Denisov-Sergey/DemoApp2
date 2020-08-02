package com.example.demoapp2.gallery_module

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.example.demoapp2.MainActivity
import com.example.demoapp2.R
import com.example.demoapp2.data.MessageAdapter
import com.example.demoapp2.data.Post
import com.example.demoapp2.pages.ListPage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_detail_image.*

class detailImage : AppCompatActivity() {

    companion object{
        val IINTENT_BYTEARRAY = "BYTEARRAY"
        val IINTENT_IMAGEPATH = "IMAGEPATH"
    }


    private lateinit var auth: FirebaseAuth
    private var database: FirebaseDatabase? = null
    private var referencedatabase: DatabaseReference? = null
    private var imgpath: String? = null

    private var allPosts: MutableList<String>? = null
    var arrayAdapter: PostAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_image)

        imgpath = intent.getStringExtra(IINTENT_IMAGEPATH)
//        URIUtil.EncodeString(imgpath)


        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        referencedatabase = FirebaseDatabase.getInstance().getReference()

        //loadData
        allPosts = mutableListOf()
        val ref = FirebaseDatabase.getInstance().getReference("Posts")
        val messageListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (i in dataSnapshot.children){
                    val img = i.child("image").getValue(String::class.java)
                    Log.d(MainActivity.TAG, "Posts $i")
                    Log.d(MainActivity.TAG, "imgpath  ${imgpath.toString()}")
                    if (img == imgpath.toString()){
                        val message : String? = i.child("message").getValue(String::class.java)
                        allPosts!!.add(message.toString())
                        Log.d(MainActivity.TAG, "Комменты  $allPosts")
                    }
                    arrayAdapter!!.notifyDataSetChanged()
                }

            }
        }
        ref.addListenerForSingleValueEvent(messageListener)
    }

    override fun onStart() {
        super.onStart()

        val byteArray  = intent.getByteArrayExtra(IINTENT_BYTEARRAY)
        val bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)
        iv_detail_img.setImageBitmap(bmp)

        //

        btn_back_to_gallery.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("ToGallery", 1)
            startActivity(intent)
        }


        ///
        btn_postcomment.setOnClickListener{
            postComment(ed_post_to_image.text.toString())
        }


        arrayAdapter = PostAdapter(this, R.layout.poststoimages, allPosts!!)
        //привязка адаптера
        list_posts.adapter = arrayAdapter

    }

    private fun postComment(comment: String) {
        if (TextUtils.isEmpty(comment)) {
            ed_post_to_image.error = "Обязательное"
            return
        }


        Toast.makeText(this, "Отправка...", Toast.LENGTH_SHORT).show()

        referencedatabase!!.child("post").child(imgpath.toString()).addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    writeNewPost(imgpath.toString(), comment)

                    //ed_post_to_image.text.clear()
                    allPosts!!.add(comment)
                    arrayAdapter!!.notifyDataSetChanged()

                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })
    }

    fun writeNewPost(imgpath: String, comment: String){
        val key = referencedatabase!!.child(imgpath).push().key
        if (key == null) {
            Log.w(MainActivity.TAG, "Не найдена коллекция $imgpath ")
            return
        }
        val userId: String = auth.currentUser!!.uid
        //database!!.child("Messages").child(userId).setValue(body)
        val post = Comment(userId, comment, imgpath)
        val postValues = post.toMap()

        val childUpdates = hashMapOf<String, Any>(
            "/Posts/$key" to postValues
        )

        referencedatabase!!.updateChildren(childUpdates)
    }

}