package com.example.navigation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.navigation.ui.theme.NavigationTheme
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment
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
import androidx.navigation.compose.NavHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NavigationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DisplayNav()
                }
            }
        }
    }
}

@Composable
fun DisplayNav(){
    val navController = rememberNavController()

    NavHost(navController = navController,
            startDestination = "Greeting"){
        composable(route = "Greeting") {
            Greeting(navController = navController)
        }
        composable(route = "Greeting2"){
            Greeting2(navController = navController )
        }
    }
}


@Composable
fun Greeting(navController: NavController) {


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    )
    {
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
            modifier = Modifier.padding(16.dp)
        )

    Text(
        text = "Hello",
    )
}

@Composable
fun Greeting2(navController: NavController){

    Button(onClick = { navController.navigate("Greeting")}) {
        Text("Go to 1")
    }
    Text(
        text = "Hello Hello Hello",
    )
}

