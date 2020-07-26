package com.example.demoapp2.pages

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.demoapp2.R
import com.example.demoapp2.data.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_list_page.view.*


class ListPage : Fragment() {


    private lateinit var auth: FirebaseAuth
    private var database: FirebaseDatabase? = null
    private var referencedatabase: DatabaseReference? = null
    private var fragmentlayout: View? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        referencedatabase = FirebaseDatabase.getInstance().getReference()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // ссылка на макет фрагмента
        fragmentlayout =  inflater.inflate(R.layout.fragment_list_page, container, false)


        //получаем авторизованного или нет юзера
        val currentUser: FirebaseUser? = auth.currentUser


        fragmentlayout!!.btn_save_bd.setOnClickListener {
            if (currentUser != null) {
                saveBD(fragmentlayout!!.ed_save_bd.text.toString(), currentUser.uid)
            }
        }

        return  fragmentlayout

    }

    private fun saveBD(post: String, uid: String){

        if (TextUtils.isEmpty(post)) {
            fragmentlayout!!.ed_save_bd.error = "Обязательное"
            return
        }

        Toast.makeText(
            getActivity()?.getBaseContext(), "Отправка...", Toast.LENGTH_SHORT).show()

        referencedatabase!!.child("users").child(uid).addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    writeNewPost(uid, post)

                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })


    }




    private fun writeNewPost(userId: String, body: String) {

        val key = referencedatabase!!.child(userId).push().key
        if (key == null) {
            Log.w(TAG, "Не найдена коллекция $userId ")
            return
        }

        //database!!.child("Messages").child(userId).setValue(body)
        val post = Post(userId, body)
        val postValues = post.toMap()

        val childUpdates = hashMapOf<String, Any>(
            "/Messages/$key" to postValues,
            "/user-Messages/$userId/$key" to postValues
        )

        referencedatabase!!.updateChildren(childUpdates)


    }


    companion object {
        private  val TAG = "DemoApp"
    }
}