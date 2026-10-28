package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AnalyticsEventEntity
import com.example.ui.K9ViewModel
import com.example.ui.components.AcademyStatusBadge
import com.example.ui.components.BorderSubtleBrush
import com.example.ui.theme.AlertRed
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CenturyGold
import com.example.ui.theme.CenturyGoldDark
import com.example.ui.theme.CenturyGoldLight
import com.example.ui.theme.LaurelGreen
import com.example.ui.theme.LaurelGreenLight
import com.example.ui.theme.NavyCard
import com.example.ui.theme.NavyCardElevated
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyDarkest
import com.example.ui.theme.RoyalBlueLight
import com.example.ui.theme.RoyalBluePrimary
import com.example.ui.theme.SurfaceTextMuted
import com.example.ui.theme.SurfaceTextWhite
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DiagnosticsScreen(
    viewModel: K9ViewModel,
    modifier: Modifier = Modifier
) {
    val telemetry by viewModel.telemetry.collectAsState()
    val analyticsEvents by viewModel.recentAnalytics.collectAsState()

    var selectedFilter by remember { mutableStateOf("ALL") }

    val filteredEvents = remember(analyticsEvents, selectedFilter) {
        if (selectedFilter == "ALL") analyticsEvents
        else analyticsEvents.filter { it.eventType == selectedFilter }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(NavyDarkest)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "DEVICE & SYSTEM DIAGNOSTICS",
                        color = CenturyGold,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Hardware telemetry, live memory audit, and academy logs.",
                        color = SurfaceTextMuted,
                        fontSize = 12.sp
                    )
                }

                IconButton(
                    onClick = { viewModel.refreshTelemetry() },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(NavyCard)
                        .testTag("refresh_telemetry_btn")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = CenturyGold)
                }
            }
        }

        // Live Resource Gauges Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = NavyDark),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CenturyGold.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("live_resources_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "HARDWARE TELEMETRY & RESOURCES",
                        color = SurfaceTextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // RAM Progress Bar
                    val usedRam = (telemetry.totalRamMb - telemetry.availableRamMb).coerceAtLeast(0)
                    val ramRatio = if (telemetry.totalRamMb > 0) usedRam.toFloat() / telemetry.totalRamMb else 0.5f

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Memory, contentDescription = null, tint = CenturyGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("RAM Memory Allocation:", color = SurfaceTextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Text(
                            text = "$usedRam MB / ${telemetry.totalRamMb} MB (${(ramRatio * 100).toInt()}%)",
                            color = if (ramRatio > 0.85f) AlertRed else LaurelGreenLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { ramRatio },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = if (ramRatio > 0.85f) AlertRed else CenturyGold,
                        trackColor = NavyCardElevated
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Battery & Network Strip
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TelemetryMiniCard(
                            title = "Battery Power",
                            value = "${telemetry.batteryPercentage}%",
                            subtitle = if (telemetry.isCharging) "Charging (AC/USB)" else "Discharging",
                            icon = Icons.Default.BatteryChargingFull,
                            color = if (telemetry.batteryPercentage < 20) AlertRed else LaurelGreenLight,
                            modifier = Modifier.weight(1f)
                        )

                        TelemetryMiniCard(
                            title = "Network Uplink",
                            value = telemetry.networkType,
                            subtitle = "Field Connection",
                            icon = Icons.Default.NetworkCheck,
                            color = RoyalBlueLight,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Hardware & OS Specs
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = NavyCard),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtleBrush()),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "OS & SYSTEM SPECIFICATIONS",
                        color = CenturyGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    SpecRow(label = "Device Model", value = "${telemetry.manufacturer} ${telemetry.model}")
                    SpecRow(label = "Android Release", value = "Android ${telemetry.androidVersion} (API ${telemetry.sdkInt})")
                    SpecRow(label = "Security Patch Level", value = telemetry.securityPatch)
                    SpecRow(label = "Hardware Architecture", value = telemetry.hardware)
                    SpecRow(label = "Display Geometry", value = "${telemetry.screenResolution} (${telemetry.screenDensityDpi} dpi)")
                    SpecRow(label = "Low Memory Flag", value = if (telemetry.isLowMemory) "ALERT (Low)" else "Nominal (Safe)")
                }
            }
        }

        // Live Event Audit Log Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Terminal, contentDescription = null, tint = CenturyGold, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "LIVE ACADEMY AUDIT LOG",
                        color = SurfaceTextWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                OutlinedButton(
                    onClick = { viewModel.clearAuditLogs() },
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AlertRed),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AlertRed.copy(alpha = 0.5f)),
                    modifier = Modifier.testTag("clear_logs_button")
                ) {
                    Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Clear", fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filters = listOf("ALL", "ENROLLMENT", "RADAR_GPS", "BILLING_PAYMENT", "SYSTEM_START", "DIAGNOSTIC_QUERY")
                items(filters) { filter ->
                    val isSelected = selectedFilter == filter
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) CenturyGold else NavyCard)
                            .border(1.dp, if (isSelected) CenturyGold else BorderSubtle, RoundedCornerShape(16.dp))
                            .clickable { selectedFilter = filter }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = filter,
                            color = if (isSelected) NavyDarkest else SurfaceTextWhite,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Event Log Items
        if (filteredEvents.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = NavyCard),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No audit events logged for filter: $selectedFilter",
                        color = SurfaceTextMuted,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }
        } else {
            items(filteredEvents) { event ->
                AuditLogItem(event = event)
            }
        }
    }
}

@Composable
fun TelemetryMiniCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(NavyCard)
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(title, color = SurfaceTextMuted, fontSize = 10.sp)
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, color = SurfaceTextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = color, fontSize = 10.sp)
        }
    }
}

@Composable
fun SpecRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = SurfaceTextMuted, fontSize = 12.sp)
        Text(
            text = value,
            color = SurfaceTextWhite,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
fun AuditLogItem(event: AnalyticsEventEntity) {
    val dateFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.US)
    val timeStr = dateFormat.format(Date(event.timestamp))

    val badgeColor = when (event.eventType) {
        "ENROLLMENT" -> CenturyGold
        "RADAR_GPS" -> RoyalBlueLight
        "BILLING_PAYMENT" -> LaurelGreenLight
        "SYSTEM_START" -> CenturyGoldLight
        else -> SurfaceTextMuted
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AcademyStatusBadge(text = event.eventType, color = badgeColor)
                Text(
                    text = timeStr,
                    color = SurfaceTextMuted,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = event.description,
                color = SurfaceTextWhite,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }
    }
}
