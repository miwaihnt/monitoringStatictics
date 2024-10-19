package com.example.myapplication2.sampledata

import android.util.Log
import com.example.myapplication2.Data.AllUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


class UserInfoRepository @Inject constructor(
    private val db: FirebaseFirestore,
) {

    suspend fun checkUserExsits(uid:String):Boolean {
        Log.d("UserInfoRepository","uid:$uid")

        return suspendCoroutine {cont ->
            db.collection("User")
                .document(uid)
                .get()
                .addOnCompleteListener {result->
                    Log.d("checkUserExsits","result:${result.result}")
                    if (result.isSuccessful) {
                        //コルーチンの再会
                        cont.resume(result.result.exists())
                    } else {
                        cont.resumeWithException(result.exception?:Exception("Unknown error"))
                    }
                }.addOnFailureListener {exception->
                    Log.e("checkUserExsits","excepiton:${exception.message}")

                }
        }

    }

    suspend fun collectEmailPassword(UID:String):Pair<String,String>?{

        val TAG = "collectEmailPassword"

        return suspendCoroutine { cont ->
            //firestoreからEmailとpasswordを取得
            db.collection("User")
                .document(UID)
                .get()
                .addOnCompleteListener { Task->
                    if (Task.isSuccessful) {
                        val document = Task.result
                        val email = document.getString("email")?:""
                        val password = document.getString("password")?:""
                        cont.resume(Pair(email, password))
                    } else {
                        cont.resumeWithException(Task.exception?:Exception("Unknown error"))
                    }
                }.addOnFailureListener {exception ->
                    Log.e("checkUserExsits","excepiton:${exception.message}")
                }



        }



    }

}