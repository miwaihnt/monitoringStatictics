package com.example.myapplication2

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.IconButton


val iconList = listOf (
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

@Composable
fun HomeIcon(){
    Row (
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement =  Arrangement.SpaceEvenly,
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
            println("Clicked!")
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


//@Composable
//fun HomeIcon(){
//    Row (
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 16.dp),
//        horizontalArrangement =  Arrangement.SpaceBetween,
//        verticalAlignment = Alignment.Bottom
//    ) {
//        // 画面の左端から1/4の位置にアイコンを配置
//        Spacer(modifier = Modifier.weight(1f))
//        IconButton(onClick = {
//            println("Home Clicked!")
//        }) {
//            Icon(imageVector = Icons.Default.Home, contentDescription = "Home")
//        }
//
//        // 画面の右端から1/4の位置にアイコンを配置
//        IconButton(onClick = {
//            println("Settings Clicked!")
//        }) {
//            Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
//        }
//        Spacer(modifier = Modifier.weight(1f))
//    }
//}
