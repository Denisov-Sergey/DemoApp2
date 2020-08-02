package com.example.demoapp2.gallery_module

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.demoapp2.R

class PostAdapter(val mCtx: Context, val layoutResId: Int, val messageList: MutableList<String>?) :
    ArrayAdapter<String>(mCtx, layoutResId, messageList as MutableList<String>) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
        val view: View = layoutInflater.inflate(layoutResId, null)

        val message = messageList?.get(position)

        val tv_post = view.findViewById<TextView>(R.id.tv_post)
        tv_post.text = message

        return view
        //return super.getView(position, convertView, parent)
    }

}