package com.example.myapplication2

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.navigation.NavController

@Composable
fun StatisticsInfo(navController: NavController){
   Column ( 
       verticalArrangement = Arrangement.Center,
       horizontalAlignment = CenterHorizontally
   ) {
     
      Button(onClick = {

//          getStatistics()
      
        }
      
      ) {
            Text(text = "統計取得")
      }
       
   } 
    
}
