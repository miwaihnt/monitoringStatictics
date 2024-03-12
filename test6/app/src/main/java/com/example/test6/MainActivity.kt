package com.example.test6

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.test6.ui.theme.Test6Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Test6Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Test6Theme {
        Greeting("Android")
    }
}

//分かったこと
//学籍番号を付与すること
//点数を判定すること
//withIndexを使って学籍番号で表示
//for文使って簡素化したい


//fun main(args: Array<String>) {
//    val input = readLine()
//    val input2 = readLine()
//    val input3 = readLine()
//    val input4 = readLine()
//    val input5 = readLine()
//    val input6 = readLine()
//
//
//    val element1 = input2?.split(" ")?.map { it.toInt() }
//    val afterScore = calculate(element1)
//    val element2 = input3?.split(" ")?.map { it.toInt() }
//    val afterScore2 = calculate(element2)
//    val element3 = input4?.split(" ")?.map { it.toInt() }
//    val afterScore3 = calculate(element3)
//    val element4 = input5?.split(" ")?.map { it.toInt() }
//    val afterScore4 = calculate(element4)
//    val element5 = input6?.split(" ")?.map { it.toInt() }
//    val afterScore5 = calculate(element5)
//
//    if( afterScore !== null && afterScore >= 25 ) {
//        println(afterScore)
//    } else {
//        null
//    }
//
//    if( afterScore2 !== null && afterScore2 >= 25 ) {
//        println(afterScore2)
//    } else {
//        null
//    }
//
//    if( afterScore3 !== null && afterScore3 >= 25 ) {
//        println(afterScore3)
//    } else {
//        null
//    }
//
//    if(afterScore4 !== null && afterScore4 >= 25 ) {
//        println(afterScore4)
//    } else {
//        null
//    }
//
//    if( afterScore5 !== null && afterScore5 >= 25 ) {
//        println(afterScore5)
//    } else {
//        null
//    }
//}
//
//fun calculate(scores:List<Int>?) :Int? {
//    return if (scores !== null && scores.size == 2 ) {
//        scores[0] - scores[1]*5
//    } else{
//        null
//    }
//}
//
//
