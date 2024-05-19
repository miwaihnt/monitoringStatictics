package com.example.myapplication2

import android.app.usage.UsageStats
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.IconButton
import androidx.compose.runtime.remember
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

val iconList = listOf(
    Icons.Default.Add,
    Icons.Default.AccountBox,
    Icons.Default.AccountCircle,
    Icons.Default.AddCircle,
    Icons.Default.List,
    Icons.Default.ArrowBack,
    Icons.Default.ArrowDropDown,
    Icons.Default.ArrowForward,
    Icons.Default.Build,
    Icons.Default.Call,
    Icons.Default.Check,
    Icons.Default.CheckCircle,
    Icons.Default.Clear,
    Icons.Default.Close,
    Icons.Default.Create,
    Icons.Default.DateRange,
    Icons.Default.Delete,
    Icons.Default.Done,
    Icons.Default.Edit,
    Icons.Default.Email,
    Icons.Default.ExitToApp,
    Icons.Default.Face,
    Icons.Default.Favorite,
    Icons.Default.FavoriteBorder,
    Icons.Default.Home,
    Icons.Default.Info,
    Icons.Default.KeyboardArrowDown,
    Icons.Default.KeyboardArrowLeft,
    Icons.Default.KeyboardArrowRight,
    Icons.Default.KeyboardArrowUp,
    Icons.Default.LocationOn,
    Icons.Default.Lock,
    Icons.Default.MailOutline,
    Icons.Default.Menu,
    Icons.Default.MoreVert,
    Icons.Default.Notifications,
    Icons.Default.Person,
    Icons.Default.Phone,
    Icons.Default.Place,
    Icons.Default.PlayArrow,
    Icons.Default.Refresh,
    Icons.Default.Search,
    Icons.Default.Send,
    Icons.Default.Settings,
    Icons.Default.Share,
    Icons.Default.ShoppingCart,
    Icons.Default.Star,
    Icons.Default.ThumbUp,
    Icons.Default.Warning,
)

//ユーザ登録
fun onRegisterClicked(
    email: String,
    password: String,
    userRegistration: UserRegistration,
    auth: FirebaseAuth,
    userName: String
) {
    userRegistration.registration(email, password, auth, userName)
}

//ログイン認証

fun performLogin(
    email: String,
    password: String,
    auth: FirebaseAuth,
    navController: NavController,
    FirebaseFirestore:FirebaseFirestore,
    usageStats:List<UsageStats>
) {

    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                navController.navigate("HomeIcon")
                val UploadStatictics: UploadStatictics
                UploadStatictics = UploadStatictics(FirebaseFirestore)
                UploadStatictics.uploadusestate(usageStats)
                Log.d("performLogin", "Succcess")
            } else {
                Log.d("performLogin", "Failed")
                val errorCode = task.exception?.message
                Log.e("performLogin", "ErrorCode: $errorCode")
            }
        }
}

//メールアドレス登録画面
@Composable
fun RegistrationScreen(
    navController: NavController,
    userRegistration: UserRegistration,
    auth: FirebaseAuth
) {
    Log.d("RegistrationScreen", "RegistrationScreen Composable is called")

    var userName by remember { mutableStateOf(value = "") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    )
    {

        //ユーザ名入力フィールド
        TextField(
            value = userName,
            onValueChange = { userName = it },
            label = { Text(text = "ユーザ名") },
            modifier = Modifier.padding(16.dp)
        )

        // Email入力フィールド
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.padding(16.dp)
        )

        // Password入力フィールド
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier
                .padding(16.dp)
        )
        Button(
            onClick = {
                onRegisterClicked(email, password, userRegistration, auth, userName)
                navController.navigate("Login")
            },
            modifier = Modifier
                .padding(16.dp)
                .width(200.dp)
        ) {
            Text("Register")
        }
    }
}


// ログイン画面
//　認証機能の実装
@Composable
fun Login(
    navController: NavController,
    auth: FirebaseAuth,
    FirebaseFirestore:FirebaseFirestore,
    usageStats:List<UsageStats>
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.padding(16.dp)
        )

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("password") },
            modifier = Modifier.padding(16.dp)
        )

        Button(
            onClick = {
                performLogin(email, password, auth, navController,FirebaseFirestore,usageStats)
            },
            modifier = Modifier
                .padding(16.dp)
                .width(200.dp)
        ) {
            Text(text = "Login")
        }
        Button(
            onClick = {
                navController.navigate("RegistrationScreen")
            },
            modifier = Modifier
                .padding(8.dp)
                .width(200.dp)
        ) {
            Text("新規登録")
        }
    }
}


//ホーム画面
@Composable
fun HomeIcon(navController: NavController, dbInfoGet: dbInfoGet, dbAddFollowData: dbAddFollowData) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { //userInfoGet
                dbInfoGet.getInfo()
                Log.d("HomeIcon", "calling HomeIconButton")
            }
        ) {
            Text(text = "ユーザ情報取得")
        }
        Button(
            onClick = {
                //friendSearchScreen
                Log.d("FriendIcon", "calling FriendIconButton")
                navController.navigate("FriendSearchScreen")
            }
        ) {
            Text(text = "友達を探す")
        }
        Button(
            onClick = {
                navController.navigate("StatisticsInfo")
            }
        ) {
            Text(text = "統計情報取得")
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        IconButton(
            onClick = {
                println("Clicked!")
            },
            modifier = Modifier
                .size(48.dp)
                .padding(vertical = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Add",

                )
        }
        IconButton(
            onClick = {
                // Settingボタンが押されたときの処理
                //
                Log.d("SettingIcon","calling ProfileSettingsScreen")
                navController.navigate("ProfileSettingsScreen")
            },
            modifier = Modifier
                .size(48.dp)
                .padding(vertical = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Add",
            )
        }
    }
}
