package com.example.demoapp2.pages

import android.app.AlertDialog
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
    private var keylist: MutableList<String>? = null
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

            //кнопки
            fragmentlayout!!.btn_save_bd.setOnClickListener {
                saveBD(fragmentlayout!!.ed_save_bd.text.toString(), currentUser.uid)
            }

            //load db data
            messagelist = mutableListOf()   //тип дпнныъ массив MutableList типа Strinf
            keylist = mutableListOf()

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
                        //DataSnapshot { key = -MDdXMWhqkdzB2dPMJ-p, value = {uid=SMuHVPgGD2RckMtuYP7ahVJzp6b2, message=test32} }
                        val message : String? = i.child("message").getValue(String::class.java)
                        val key : String? = i.key
                        messagelist!!.add(message.toString())
                        keylist!!.add(key.toString())
                        //Log.d(TAG, "$i")
                        //Log.d(TAG, "$key")
                        //Log.d(TAG, "$keylist")
                        //Log.d(TAG, "$dataSnapshot")


                        //Toast.makeText(getActivity()?.getBaseContext(), "$i ", Toast.LENGTH_LONG).show()
                        //Toast.makeText(getActivity()?.getBaseContext(), "$message ", Toast.LENGTH_LONG).show()
                    }
                    //ссобщить нашему адаптеру что он изменился
                    arrayAdapter!!.notifyDataSetChanged();
                }
            }
            ref.addListenerForSingleValueEvent(messageListener)

            //клики по списку
            /*listView.setOnItemLongClickListener {
                adapterView, view, i, l ->

            }*/



            listView.setOnItemClickListener { parent, view, position, id  ->
                val msg = messagelist!![position]
                val key : String = keylist!![position]
                //println(keylist)
                //println("$parent, $view, $position, $id")

                showUpdateDialog(key, msg, currentUser.uid, position)

            }

        }

        return  fragmentlayout

    }

    private fun showUpdateDialog(
        messageId: String,
        messageText: String,
        uid: String,
        position: Int
    ){
        val  dialogBuilder = AlertDialog.Builder(getActivity())
        val layoutInflater: LayoutInflater = layoutInflater
        val dialogView: View = layoutInflater.inflate(R.layout.update_dialog, null)
        //кнопки вслывашки
        val et_ud = dialogView.findViewById<EditText>(R.id.et_ud_message)
        val btn_upd = dialogView.findViewById<Button>(R.id.btn_ut_update)
        val btn_delete = dialogView.findViewById<Button>(R.id.btn_ut_delete)
        //

        dialogBuilder.setView(dialogView)
        dialogBuilder.setTitle("Обновить $messageText")

        //
        val alertdialog: AlertDialog = dialogBuilder.create()

        //наполнение елементами
        et_ud.setText(messageText)
        btn_delete.setOnClickListener {
            deleteMessage(messageId, messageText, uid)
            //обновить массив
            messagelist!!.removeAt(position)
            keylist!!.removeAt(position)
            arrayAdapter!!.notifyDataSetChanged()

            alertdialog.dismiss()
        }

        btn_upd.setOnClickListener {
            updateMessage(messageId, et_ud.text.toString(), uid)
            //обновить массив адаптера
            messagelist!![position] = et_ud.text.toString()
            arrayAdapter!!.notifyDataSetChanged();

            alertdialog.dismiss()
        }

        alertdialog.show()
    }

    private fun updateMessage(messageId: String, messageText: String, uid: String) {

        database!!.getReference("Messages").child(messageId).child("message").setValue(messageText)
        database!!.getReference("user-Messages").child(uid).child(messageId).child("message").setValue(messageText)

        Toast.makeText(activity?.baseContext, "Обновлено  $messageText", Toast.LENGTH_SHORT).show()
    }

    private fun deleteMessage(messageId: String, messageText: String, uid: String) {

        database!!.getReference("Messages").child(messageId).removeValue()
        database!!.getReference("user-Messages").child(uid).child(messageId).removeValue()

        Toast.makeText(activity?.baseContext, "Удалено $messageText", Toast.LENGTH_SHORT).show()
    }

    private fun deleteBD(uid: String) {
        TODO("Not yet implemented")
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

                    //Toast.makeText(getActivity()?.getBaseContext(), "$dataSnapshot ", Toast.LENGTH_LONG).show()
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

        keylist!!.add(key)
        referencedatabase!!.updateChildren(childUpdates)


    }


    companion object {
        private  val TAG = "DemoApp"
    }
}