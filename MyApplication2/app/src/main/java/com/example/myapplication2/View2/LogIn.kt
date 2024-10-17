package com.example.myapplication2.View2

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication2.Data.AuthState
import com.example.myapplication2.R
import com.example.myapplication2.ViewModel.CurrentUserViewModel
import com.example.myapplication2.ViewModel.FileUploadViewModel
import com.example.myapplication2.ViewModel.GoogleAuthViewModel
import com.example.myapplication2.ViewModel.LoginViewModel
import com.example.myapplication2.ViewModel.UploadStatisticsViewModel
import com.example.myapplication2.ViewModel.UsageStatsViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException

@Composable
fun LogInView(
    loginViewModel: LoginViewModel = viewModel(),
    currentUserViewModel: CurrentUserViewModel = viewModel(),
    GoogleAuthViewModel: GoogleAuthViewModel = viewModel(),
    navController: NavController
) {

    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val usageStatsViewModel: UsageStatsViewModel = hiltViewModel()
    val statisticsViewModel: UploadStatisticsViewModel = hiltViewModel()
    val fileUploadViewModel: FileUploadViewModel = hiltViewModel()

    // ActivityResultLauncherの作成
    val activityResultLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Log.d("LoginView","result:$result")
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                account?.let {
                    GoogleAuthViewModel.firebaseAuthWithGoogle(it.idToken!!,navController)
                }
            } catch (e: ApiException) {
                GoogleAuthViewModel.authState.value = AuthState.Failure(e.message ?: "Google sign in failed")
            }
        } else {
            Log.d("LoginView","Activity Result: CANCELED, Code: ${result.resultCode}")
        }
    }


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ){
        Text(
            text = "ログイン",
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            textAlign = TextAlign.Center,
            fontSize = 24.sp,
        )


        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {

            // 共通の幅を指定
            val commonWidth = Modifier.width(300.dp)


            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier
                    .padding(16.dp)
                    .then(commonWidth)
            )

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier
                    .padding(16.dp)
                    .then(commonWidth)
            )

            Button(
                onClick = {
                    loginViewModel.logIn(
                        email,
                        password,
                        navController,
                        usageStatsViewModel, // UsageStatsViewModel を渡す
                        statisticsViewModel, // UploadStatisticsViewModel を渡す
                        fileUploadViewModel // FileUploadViewModel を渡す
                    )
                },
                colors =  ButtonDefaults.buttonColors(
                    containerColor  = Color.Gray, // 背景色をグレーに設定
                    contentColor = Color.Black    // テキストの色を白に設定
                ),
                modifier = Modifier
                    .padding(16.dp)
                    .then(commonWidth)
            ) {
                Text(text = "Login")
            }

            Button(
                onClick = {
                    navController.navigate("UserRegistrate")
                },

                colors =  ButtonDefaults.buttonColors(
                    containerColor  = Color.Gray, // 背景色をグレーに設定
                    contentColor = Color.Black    // テキストの色を白に設定
                ),

                modifier = Modifier
                    .padding(16.dp)
                    .then(commonWidth)
            ) {
                Text(text = "登録")
            }


            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .then(commonWidth)
                    .clickable {
                        GoogleAuthViewModel.signInWithGoogle(context, activityResultLauncher)
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.android_dark_sq_na),
                    contentDescription = "Google Logo",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Googleでログイン")
            }

        }

    }
}


//従来のLoginView
//{
//
//    val usageStatsViewModel: UsageStatsViewModel = hiltViewModel()
//    val statisticsViewModel: UploadStatisticsViewModel = hiltViewModel()
//    val fileUploadViewModel: FileUploadViewModel = hiltViewModel()
//
//
//    var email by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }
//
//
//    Column(
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        TextField(
//            value = email,
//            onValueChange = { email = it },
//            label = { Text("Email") },
//            modifier = Modifier.padding(16.dp)
//        )
//
//        TextField(
//            value = password,
//            onValueChange = { password = it },
//            label = { Text("Password") },
//            modifier = Modifier.padding(16.dp)
//        )
//
//        Button(
//            onClick = {
//                loginViewModel.logIn(
//                    email,
//                    password,
//                    navController,
//                    usageStatsViewModel, // UsageStatsViewModel を渡す
//                    statisticsViewModel, // UploadStatisticsViewModel を渡す
//                    fileUploadViewModel // FileUploadViewModel を渡す
//                )
//            },
//            modifier = Modifier
//                .padding(16.dp)
//                .width(200.dp)
//        ) {
//            Text(text = "Login")
//        }
//        Button(
//            onClick = {
//                navController.navigate("UserRegistrate")
//            },
//            modifier = Modifier
//                .padding(16.dp)
//                .width(200.dp)
//        ) {
//            Text(text = "登録")
//        }
//
//    }
//
//}
