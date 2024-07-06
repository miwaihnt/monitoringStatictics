package com.example.myapplication2.View2

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import com.example.myapplication2.ViewModel.FileUploadViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication2.ViewModel.CurrentUserViewModel

@Composable
fun Profile(
    currentUserViewModel: CurrentUserViewModel = viewModel(),
    fileUploadViewModel: FileUploadViewModel = viewModel()
) {
    val user by currentUserViewModel.user.collectAsState()
    Log.d("Profile","user:$user")

    LaunchedEffect(true) {
        currentUserViewModel.currentUserInfo()
    }
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(text = "名前：${user.userName}")
            Spacer(modifier = Modifier.height(16.dp))
            val painter = rememberAsyncImagePainter(user.profileImage)
            Image(painter = painter,"")
        }
}