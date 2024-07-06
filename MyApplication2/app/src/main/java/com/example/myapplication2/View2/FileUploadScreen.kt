package com.example.myapplication2.View2

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication2.ViewModel.FileUploadViewModel
import coil.compose.rememberAsyncImagePainter

@Composable
fun FileUploadScreen(viewModel: FileUploadViewModel = viewModel()) {

    val imageUri = viewModel.imageUri.collectAsState()

    val imagePickerLauncer = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = {uri:Uri? ->
            uri?.let {
            viewModel.uploadImage(it)
            }
        }
    )

    Column (
        modifier = Modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    )
    {
        Button(onClick = { imagePickerLauncer.launch("image/*")}) {
            Text(text = "pick image")
        }
        Spacer(modifier = Modifier.height(16.dp))

        viewModel.uploadResult?.let { result ->
            Text(text = "")
        }

        imageUri.value?.let {uri ->
            Log.d("FileUploadScreen","uri:$uri")
            val painter = rememberAsyncImagePainter(uri)
            Log.d("FileUploadScreen","painter:$painter")
            Image(painter = painter, contentDescription = null)
        }
    }
}