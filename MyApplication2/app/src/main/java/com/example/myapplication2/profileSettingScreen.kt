package com.example.myapplication2

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch


@Composable
fun ParentComponent(navController: NavController) {
    val userNameState = remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    val db = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser

    // Firestoreからユーザー名を取得
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            val userEmail = user.email

            db.collection("User")
                .whereEqualTo("email", userEmail)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.isEmpty) {
                        Log.d("ParentComponent", "User with email $userEmail not found")
                    } else {
                        for (document in querySnapshot) {
                            val fetchedUserName = document.getString("userName") ?: "名前なし"
                            userNameState.value = fetchedUserName
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("ParentComponent", "Error getting documents: ", exception)
                }
        }
    }

    val coroutineScope = rememberCoroutineScope()

    ProfileSettingsScreen(
        userName = userNameState.value,
        onUserNameChange = { newName ->
            userNameState.value = newName
        },
        onSaveClick = {
            currentUser?.let { user ->
                val userEmail = user.email

                db.collection("User")
                    .whereEqualTo("email", userEmail)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        if (querySnapshot.isEmpty) {
                            Log.d("ProfileSettingsScreen", "User with email $userEmail not found")
                        } else {
                            for (document in querySnapshot) {
                                val documentId = document.id
                                db.collection("User").document(documentId)
                                    .update("userName", userNameState.value)
                                    .addOnSuccessListener {
                                        Log.d("ProfileSettingsScreen", "User name updated successfully")
                                        // スナックバーを表示
                                        coroutineScope.launch {
                                            val Name = userNameState.value
                                            snackbarHostState.showSnackbar("名前が $Name に更新されました")
                                        }
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.e("ProfileSettingsScreen", "Error updating user name: ", exception)
                                    }
                            }
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.e("ProfileSettingsScreen", "Error getting documents: ", exception)
                    }
            }
        },
        snackbarHostState = snackbarHostState
    )
}

@Composable
fun ProfileSettingsScreen(
    userName: String,
    onUserNameChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 新しいユーザー名を表示
            Text(text = "New userName: $userName")

            // 名前入力フィールド
            OutlinedTextField(
                value = userName,
                onValueChange = onUserNameChange,
                label = { Text("新しい名前") },
                modifier = Modifier.padding(16.dp)
            )

            // 保存ボタン
            Button(
                onClick = onSaveClick,
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "保存")
            }
        }

        // スナックバーを中央に表示
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

