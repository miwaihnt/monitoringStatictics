package com.example.myapplication2

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.tasks.await
import androidx.compose.foundation.lazy.items
import com.example.myapplication2.Data.AllUser
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text


@Composable
fun ListFriends(navController: NavController,dbInfoGet: dbInfoGet,auth: FirebaseAuth) {

    val db = dbInfoGet.db
    val userList = remember { mutableStateListOf<AllUser>() }
    val authUser = auth.currentUser
    if (authUser !== null) {
        Log.d("ListFriends","authUser:${authUser.uid}")
    }

    //フォローしているユーザのドキュメントIDを取得
    LaunchedEffect(Unit) {
        try {
            if (authUser !== null) {
                userList.clear()
                val selectedQuery = db.collection("User").document(authUser.uid).collection("FollowData").get().await()
                    if (!selectedQuery.isEmpty) {
                        for (document in selectedQuery) {
                            val Following = document.get("following") as? List<String> ?: emptyList()
                            val followers = document.get("followers") as? List<String> ?: emptyList()
                            Log.d("ListFriends","Following:$Following")
                            Log.d("ListFriends","followers:$followers")

                            //フォローしているユーザの名前を抽出
                            for (userData in Following) {
                                val userDoc = db.collection("User").document(userData).get().await()
                                Log.d("ListFriends","userDoc:${userDoc.data}")
                                if (userDoc !== null) {
                                    val userName = AllUser(
                                        password = userDoc["password"] as? String ?: "",
                                        userName = userDoc["userName"] as? String ?: "",
                                        email = userDoc["userName"] as? String ?: "",
                                        profileImage = userDoc["profileImage"] as? String?:"",
                                        docId = ""
                                    )
                                    userList.add(userName)
                                }
                            }
                        }
                    } else {
                        Log.d("ListFriends", "No data found in FollowData")
                    }
            }
        } catch (e:Exception) {
            Log.e("ListError","Firestre not getting :${e.message}",e)
        }
    }

    LazyColumn(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(userList) {user ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable { },

                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = MaterialTheme.shapes.medium // カードの形状を設定

            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .clickable { ontapped(navController) }
                        
                ) {
                    Text(
                        text = user.userName,
                        modifier = Modifier
                            .padding(16.dp)
                            .clickable { ontapped(navController) }
                    )
                }
            }
        }
    }
}


private  fun ontapped(navController: NavController) {
    navController.navigate("StatisticsInfo")

}


//    全量のユーザデータを取得するコード
//    LaunchedEffect(Unit) {
//
//        try {
//            val querySnapshot = db.collection("User").get().await()
//            userList.clear()
//            for (document in querySnapshot) {
//                val user = AllUser(
//                    password = document.getString("password") ?: "",
//                    userName = document.getString("userName") ?: "",
//                    email = document.getString("email") ?: ""
//                )
//                userList.add(user)
//            }
//        } catch (e:Exception) {
//            Log.e("ListError","Firestre not getting :${e.message}",e)
//        }
//    }
//





