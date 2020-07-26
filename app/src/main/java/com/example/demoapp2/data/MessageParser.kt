package com.example.demoapp2.data

class MessageParser( uid: String,  message: String) {

    private var uid: String? = uid
    private var message: String? = message


    fun getUid(): String? {
        return uid
    }
    fun getMessage(): String? {
        return message
    }

}