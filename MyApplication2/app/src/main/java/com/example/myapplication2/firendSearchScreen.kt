package com.example.myapplication2

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun FriendSearchScreen(dbAddFollowData: dbAddFollowData,navController: NavController) {

    val db = FirebaseFirestore.getInstance()

    var searchText by remember { mutableStateOf("") }
    var searchResult by remember { mutableStateOf<Pair<String, String>?>(null) }
    var searching by remember { mutableStateOf(false) } // 検索中かどうかのフラグ

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 検索用のテキストボックス
        TextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text("友達を検索する") }
        )

        // 検索結果表示
        if (searching) {
            Text(text = "検索中...")
        } else if (searchResult != null) {
            val (foundName, foundEmail) = searchResult!!
            Text(text = "検索結果")
            Text(text = "名前: $foundName")
            Text(text = "メールアドレス: $foundEmail")
        } else {
            Text(text = "検索結果")
            Text(text = "名前: 該当なし")
            Text(text = "メールアドレス: 該当なし")
        }


        // 検索ボタン
        Button(
            onClick = {
                // 検索中フラグを立てる
                searching = true
                // 検索結果をリセット
                searchResult = null
                // Firestoreで検索を行う
                db.collection("User")
                    .whereEqualTo("email", searchText)
                    .get()
                    .addOnSuccessListener { documents ->
                        Log.d("TAG","document:$documents")
                        for (document in documents) {
                            Log.d("document id", "documentid:$document.id")
                            val foundName = document.getString("userName") ?: "名前なし"
                            val foundEmail = document.getString("email") ?: "メールアドレスなし"
                            searchResult = Pair(foundName, foundEmail)
                            // followingに新しく登録
                            dbAddFollowData.addFollowingData(document.id)
                            dbAddFollowData.addFollowersData(document.id)
                            Log.d("FriendSearchScreen", "User name for email $foundName: $foundEmail")
                        }
                        // 検索が完了したので検索中フラグを下ろす
                        searching = false
                    }
                    .addOnFailureListener { exception ->
                        // エラー処理
                        searchResult = null // 検索結果をnullに設定
                        searching = false // 検索が完了したので検索中フラグを下ろす
                        Log.e("FriendSearchScreen", "Error searching for user", exception)
                    }
            }
        ) {
            Text(text = "検索")
        }
    }
}