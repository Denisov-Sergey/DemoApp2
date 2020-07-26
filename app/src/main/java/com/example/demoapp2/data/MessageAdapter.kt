package com.example.demoapp2.data

import android.annotation.SuppressLint
import android.content.Context
import android.database.DataSetObserver
import android.os.Message
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListAdapter
import android.widget.TextView
import com.example.demoapp2.R

class MessageAdapter(val mCtx: Context, val layoutResId: Int, val messageList: MutableList<String>?) :
    ArrayAdapter<String>(mCtx, layoutResId, messageList as MutableList<String>) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        val view: View = layoutInflater.inflate(layoutResId, null)

        val message = messageList?.get(position)

        val tv_message = view.findViewById<TextView>(R.id.tv_message)
        tv_message.text = message

        return view
        //return super.getView(position, convertView, parent)
    }

}