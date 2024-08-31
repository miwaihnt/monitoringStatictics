package com.example.myapplication2.View2

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    // imageの再描画
    LaunchedEffect(uploadResult,userName) {
        Log.d("Profile","upload result change :$uploadResult")
        currentUserViewModel.currentUserInfo()
    }

        Column(
            modifier = Modifier.background(color = Color.Black),
        ) {

            Text(
                text = "プロフィール変更",
                color = Color.White,
                fontSize = 24.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // 境界線を追加
            Divider(
                color = Color.White.copy(alpha = 0.3f),
                thickness = 1.dp,
            )

            Spacer(Modifier.padding(16.dp))

            Column {
                Text(
                    text = "名前",
                    color = Color.White,
                    fontSize = 12.sp
                )

                Spacer(Modifier.padding(4.dp))

                Row (
                    modifier = Modifier
                        .clickable {
                            showDialog = true
                        },
                ){
                    Text(
                        text = "${user.userName}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )

                    Spacer(Modifier.padding(8.dp))

                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = Color.White
                    )

                }
            }

            Spacer(Modifier.padding(16.dp))

            Column(

            ) {

                Text(
                    text = "ID",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Spacer(Modifier.padding(4.dp))

                Text(
                    text = "${user.docId}" ,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }



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

            Spacer(Modifier.padding(16.dp))
            val painter = rememberAsyncImagePainter(user.profileImage)
            //プロフィール画像の表示

            Column(

            ) {

                Text(
                    text = "プロフィール画像",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )

                Spacer(Modifier.padding(4.dp))

                Row(
                    Modifier
                        .clickable {
                            imagePickerLauncer.launch("image/*")
                        }
                ) {


                    Image(
                        painter = painter,
                        "",
                        Modifier
                            .height(256.dp)
                            .width(256.dp),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(Modifier.padding(4.dp))

                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = Color.White
                    )
                }

            }

        }
}