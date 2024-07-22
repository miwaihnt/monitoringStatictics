package com.example.myapplication2.ViewModel

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject


@HiltViewModel
class FileUploadViewModel @Inject constructor(
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth,
    private  val db: FirebaseFirestore
) : ViewModel() {

    private val _imageUri =  MutableStateFlow<Uri?>(null)
    val imageUri = _imageUri.asStateFlow()
    val uid = auth.currentUser?.uid

    private val _uploadResult = MutableStateFlow<String?>(null)
    val uploadResult = _uploadResult.asStateFlow()

    fun uploadImage(uri:Uri) {
        _imageUri.value = uri
        val storageRef = storage.reference
        val imageRef = storageRef.child("profile/${uid}.jpg")
        val uploadTask = imageRef.putFile(uri)
        Log.d("uploadImage","image uri :${_imageUri.value}")
        uploadTask.addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener {downloadUrl ->
                _uploadResult.value = downloadUrl.toString()
                Log.d("FileUploadViewModel","url:${_uploadResult.value}")
                Log.d("FileUploadViewModel","url:${_uploadResult}")
                Log.d("FileUploadViewModel","url:$uploadResult")
                uploadImageFirestore()
            }
        }.addOnFailureListener{ Exception ->
            _uploadResult.value = "Upload failed: ${Exception.message}"
        }
    }

    //取得したダウンロードリンクをfirebaseのドキュメントに格納する
    private fun uploadImageFirestore() {
        if (uid !== null) {
           val userRef = db.collection("User").document(uid)
            userRef.update("profileImage",_uploadResult.value)
                .addOnSuccessListener {
                    Log.d("uploadImageFirestore","uploadImageFirestore success")
                }
                .addOnFailureListener {
                    Log.e("uploadImageFirestore","uploadImageFirestore fail")
                }
        }
    }


}





