package com.example.firebase_personelkaydet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.firebase_personelkaydet.ui.theme.DarkColorScheme
import com.example.firebase_personelkaydet.ui.theme.LightColorScheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val myViewModel = MyViewModel()

        installSplashScreen().apply {
            setKeepOnScreenCondition {
                myViewModel.isLoading.value
            }
        }

        setContent {
            MaterialTheme(
                colorScheme =
                if (myViewModel.isDark.value){
                    DarkColorScheme
                }else{
                    LightColorScheme
                }
            ) {
                MainScreen(myViewModel)
            }

        }


    }
}






