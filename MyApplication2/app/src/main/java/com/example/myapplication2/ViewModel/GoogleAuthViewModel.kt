package com.example.myapplication2.ViewModel

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.myapplication2.Data.AuthState
import com.example.myapplication2.R
import com.example.myapplication2.sampledata.UserInfoRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class GoogleAuthViewModel @Inject constructor(
    private val repository: UserInfoRepository,
    private val db: FirebaseFirestore,
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState = _authState

    private lateinit var googleSignInClient: GoogleSignInClient
    private val auth = Firebase.auth

    private val TAG = "GoogleAuth"
    private var UID = ""

    fun signInWithGoogle(
        context: Context,
        activityResultLauncher: ActivityResultLauncher<Intent>,
    ) {
        //googleSignInOptionの設定
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        Log.d(TAG,"signInWithGoogle called")
        val googleSignInClient = GoogleSignIn.getClient(context,gso)
        val signInIntent = googleSignInClient.signInIntent

        // Google Sign Inを開始
        activityResultLauncher.launch(signInIntent)
    }

    fun firebaseAuthWithGoogle(
        idToken:String,
        navController: NavController,
    ) {
        Log.d(TAG,"idToken:$idToken")
        val credential  = GoogleAuthProvider.getCredential(idToken,null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener {task ->
                if(task.isSuccessful){
                    //現在接続しているユーザのuidを取得
                    UID = auth.currentUser!!.uid
                    Log.d(TAG,"uid:$UID")
                    //取得したuidがfirebaseで保持しているuidと一致するか
                    viewModelScope.launch {
                        val result = repository.checkUserExsits(UID)
                        Log.d(TAG,"result:${result}")
                        if (!result) {
                            navController.navigate("linkWithEmail")
                            Log.d(TAG,"uid:$UID")
                        } else {
                            navController.navigate("Home")
                        }

                    }
                }
            }
    }


    fun linkWithEmailPassoword(
        userName:String,
        email:String,
        password:String,
        navController:NavController
    ) {
        val currentUser = auth.currentUser
        //現在接続しているユーザのuidを取得
        UID = auth.currentUser!!.uid
        Log.d(TAG,"currentUser:$currentUser")
        Log.d(TAG,"linkwithEmail:userName$userName,email:$email,password:$password")
        Log.d(TAG,"linkwithEmail:UID:$UID")
        //メールリンクが作成されているかの確認
        val isLinkmail = currentUser?.providerData?.any{it.providerId == EmailAuthProvider.PROVIDER_ID} == true

        if (!isLinkmail) {
            //メールとパスワードを使ってクレデンシャルを作成
            val credential = EmailAuthProvider.getCredential(email,password)
            //google認証とのリンク
            currentUser?.linkWithCredential(credential)
                ?.addOnCompleteListener {task ->
                    if (task.isSuccessful) {
                        Log.d(TAG,"link is succesful credential:$credential")

                        val firebaseUser = hashMapOf(
                            "userName" to userName,
                            "email" to email,
                            "password" to password
                        )

                        db.collection("User")
                            .document(UID)
                            .set(firebaseUser)
                            .addOnSuccessListener {
                                val followData = hashMapOf(
                                    "followers" to emptyList<String>(),
                                    "following" to emptyList<String>(),
                                    "followreqesting" to emptyList<String>()
                                )
                                db.collection("User").document(UID).collection("FollowData")
                                    .add(followData)
                                    .addOnSuccessListener { subDocumentReference ->
                                        Log.d(TAG, "Subcollection added with ID: ${subDocumentReference.id}")
                                        navController.navigate("Home")
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e(TAG, "Error adding subCollection,e")
                                    }
                            }
                            .addOnFailureListener { e ->
                                Log.e(TAG, "Error adding document", e)
                            }
                    } else {
                        Log.d(TAG,"link is error ${task.exception}")
                    }
                }
        }

    }
}



