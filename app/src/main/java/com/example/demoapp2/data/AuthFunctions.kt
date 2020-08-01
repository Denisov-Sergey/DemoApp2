package com.example.demoapp2.data

import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

class AuthFunctions {

    fun updateUI(user: FirebaseUser){

        Log.d(TAG, "$user")

    }

    companion object{
        val TAG = "DemoApp"

        //val updateUI(user: FirebaseUser) = AuthFunctions.updateUI(user)
    }
}