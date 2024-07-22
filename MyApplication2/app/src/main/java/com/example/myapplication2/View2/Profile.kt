package com.example.myapplication2.View2

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication2.ViewModel.FileUploadViewModel
import com.example.myapplication2.ViewModel.CurrentUserViewModel
import com.example.myapplication2.ViewModel.UserResistrationViewModel


@Composable
fun Profile(
    currentUserViewModel: CurrentUserViewModel = viewModel(),
    fileUploadViewModel: FileUploadViewModel = viewModel(),
) {
    val user by currentUserViewModel.user.collectAsState()
    val userName by currentUserViewModel.userNameState.collectAsState()
    val uploadResult by fileUploadViewModel.uploadResult.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var newUserName by remember { mutableStateOf("") }



    //imageの変更
    val imageUri = fileUploadViewModel.imageUri
    val imagePickerLauncer = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = {uri: Uri? ->
            uri?.let {
                fileUploadViewModel.uploadImage(it)
            }
        }
    )


    //userInfoの取得
    LaunchedEffect(Unit) {
        currentUserViewModel.currentUserInfo()
        Log.d("Profile","user:$user")
    }

//    imageの再描画
    LaunchedEffect(uploadResult,userName) {
        Log.d("Profile","upload result change :$uploadResult")
        currentUserViewModel.currentUserInfo()
    }

        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {

            Text(
                text = "名前：${user.userName}",
                modifier = Modifier
                .clickable {
                    showDialog = true
                }
            )

            // showDialog が true の場合にダイアログを表示
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = {  },
                    title = { Text("名前を変更") },
                    text = {
                        BasicTextField(
                            value = newUserName,
                            onValueChange = { newUserName = it }
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            currentUserViewModel.updateUserName(newUserName)
                            showDialog = false
                        }) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("キャンセル")
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            val painter = rememberAsyncImagePainter(user.profileImage)
            Image(
                painter = painter,
                "",
                Modifier.clickable {imagePickerLauncer.launch("image/*")
                }
            )
        }
}