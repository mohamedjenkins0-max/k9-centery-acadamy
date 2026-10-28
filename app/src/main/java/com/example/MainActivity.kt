package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AppTab
import com.example.ui.K9ViewModel
import com.example.ui.components.AcademyTopHeader
import com.example.ui.screens.BillingScreen
import com.example.ui.screens.DiagnosticsScreen
import com.example.ui.screens.EnrollmentScreen
import com.example.ui.screens.LocationRadarScreen
import com.example.ui.screens.OverviewScreen
import com.example.ui.theme.CenturyGold
import com.example.ui.theme.CenturyGoldLight
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyDarkest
import com.example.ui.theme.RoyalBluePrimary
import com.example.ui.theme.SurfaceTextMuted
import com.example.ui.theme.SurfaceTextWhite

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                K9AcademyApp()
            }
        }
    }
}

@Composable
fun K9AcademyApp(
    viewModel: K9ViewModel = viewModel()
) {
    val currentTab by viewModel.currentTab.collectAsState()

    // Handle system back gesture: return to Overview tab if on a sub-tab
    BackHandler(enabled = currentTab != AppTab.OVERVIEW) {
        viewModel.setTab(AppTab.OVERVIEW)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = NavyDarkest,
        topBar = {
            AcademyTopHeader()
        },
        bottomBar = {
            NavigationBar(
                containerColor = NavyDark,
                contentColor = SurfaceTextWhite
            ) {
                NavigationBarItem(
                    selected = currentTab == AppTab.OVERVIEW,
                    onClick = { viewModel.setTab(AppTab.OVERVIEW) },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Overview") },
                    label = { Text("Overview", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = k9NavColors(),
                    modifier = Modifier.testTag("nav_item_overview")
                )
                NavigationBarItem(
                    selected = currentTab == AppTab.ENROLLMENT,
                    onClick = { viewModel.setTab(AppTab.ENROLLMENT) },
                    icon = { Icon(Icons.Default.Pets, contentDescription = "Enrollment") },
                    label = { Text("Enroll", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = k9NavColors(),
                    modifier = Modifier.testTag("nav_item_enrollment")
                )
                NavigationBarItem(
                    selected = currentTab == AppTab.RADAR,
                    onClick = { viewModel.setTab(AppTab.RADAR) },
                    icon = { Icon(Icons.Default.Radar, contentDescription = "Radar") },
                    label = { Text("Radar", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = k9NavColors(),
                    modifier = Modifier.testTag("nav_item_radar")
                )
                NavigationBarItem(
                    selected = currentTab == AppTab.BILLING,
                    onClick = { viewModel.setTab(AppTab.BILLING) },
                    icon = { Icon(Icons.Default.Payment, contentDescription = "Billing") },
                    label = { Text("Billing", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = k9NavColors(),
                    modifier = Modifier.testTag("nav_item_billing")
                )
                NavigationBarItem(
                    selected = currentTab == AppTab.DIAGNOSTICS,
                    onClick = { viewModel.setTab(AppTab.DIAGNOSTICS) },
                    icon = { Icon(Icons.Default.Analytics, contentDescription = "Diagnostics") },
                    label = { Text("Audit", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = k9NavColors(),
                    modifier = Modifier.testTag("nav_item_diagnostics")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                AppTab.OVERVIEW -> OverviewScreen(viewModel = viewModel)
                AppTab.ENROLLMENT -> EnrollmentScreen(viewModel = viewModel)
                AppTab.RADAR -> LocationRadarScreen(viewModel = viewModel)
                AppTab.BILLING -> BillingScreen(viewModel = viewModel)
                AppTab.DIAGNOSTICS -> DiagnosticsScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun k9NavColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = NavyDarkest,
    selectedTextColor = CenturyGoldLight,
    indicatorColor = CenturyGold,
    unselectedIconColor = SurfaceTextMuted,
    unselectedTextColor = SurfaceTextMuted
)
