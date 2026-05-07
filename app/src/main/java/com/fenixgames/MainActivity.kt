package com.fenixgames

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.fenixgames.ui.navigation.AppNavigation
import com.fenixgames.ui.theme.FenixGamesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FenixGamesTheme {
                AppNavigation()
            }
        }
    }
}

