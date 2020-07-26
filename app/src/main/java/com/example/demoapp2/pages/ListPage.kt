package com.example.demoapp2.pages

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.demoapp2.R
import com.example.demoapp2.data.MessageAdapter
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

    lateinit var listView: ListView
    private var messagelist: MutableList<String>? = null
    var arrayAdapter: MessageAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        referencedatabase = FirebaseDatabase.getInstance().getReference()



        //loadData
        /*messagelist = mutableListOf()
        val ref = FirebaseDatabase.getInstance().getReference("user-Messages").child(FirebaseAuth.getInstance().currentUser!!.uid)
        val messageListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (i in dataSnapshot.children){
                    val message : String? = i.child("message").getValue(String::class.java)
                    messagelist!!.add(message.toString())
                    //Toast.makeText(getActivity()?.getBaseContext(), "$message ", Toast.LENGTH_LONG).show()
                }

                //Toast.makeText(getActivity()?.getBaseContext(), "$messagelist ", Toast.LENGTH_LONG).show()
            }
        }
        ref.addListenerForSingleValueEvent(messageListener)*/

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // ссылка на макет фрагмента
        fragmentlayout =  inflater.inflate(R.layout.fragment_list_page, container, false)


        //получаем авторизованного или нет юзера
        val currentUser: FirebaseUser? = auth.currentUser


        if (currentUser != null) {
        fragmentlayout!!.btn_save_bd.setOnClickListener {
                saveBD(fragmentlayout!!.ed_save_bd.text.toString(), currentUser.uid)

        }

            //load db data
            messagelist = mutableListOf()   //тип дпнныъ массив MutableList типа Strinf

            //найти лист вью и создать для него адаптер
            listView = fragmentlayout!!.findViewById(R.id.bd_list_view)
            //в скобках Context , а не this как в java , потом темплейт вьюхи и что туда засовываем
            arrayAdapter = MessageAdapter(getActivity()!!.applicationContext, R.layout.messages, messagelist!!)
            //привязка адаптера
            listView.adapter = arrayAdapter


            val ref = FirebaseDatabase.getInstance().getReference("user-Messages").child(currentUser.uid)
            val messageListener = object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {

                        //данные хранятся в подветках - childreb
                    for (i in dataSnapshot.children){
                        //там есть uid  и message взять значение как строку
                        val message : String? = i.child("message").getValue(String::class.java)
                        messagelist!!.add(message.toString())
                        //Toast.makeText(getActivity()?.getBaseContext(), "$message ", Toast.LENGTH_LONG).show()
                    }
                    //ссобщить нашему адаптеру что он изменился
                    arrayAdapter!!.notifyDataSetChanged();
                }
            }
            ref.addListenerForSingleValueEvent(messageListener)


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

                    messagelist!!.add(post)
                    //Toast.makeText(getActivity()?.getBaseContext(), "$message ", Toast.LENGTH_LONG).show()
                    arrayAdapter?.notifyDataSetChanged();
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