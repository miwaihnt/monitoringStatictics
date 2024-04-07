package com.example.myapplication2

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class UserRegistration(val db : FirebaseFirestore){

    val TAG = "dbUserRegist"

    fun registration(email:String,password:String){

        try {
            val user = hashMapOf(
                "email" to email,
                "password" to password
            )
            db.collection("User")
                .add(user)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG,"DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error adding document", e)
                }


        } catch (e:Exception){
            Log.e(TAG,"Failure Registration")
        }

    }

}