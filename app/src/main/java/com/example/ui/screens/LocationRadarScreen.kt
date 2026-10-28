package com.example.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AcademySector
import com.example.data.SectorProximity
import com.example.ui.K9ViewModel
import com.example.ui.components.AcademyStatusBadge
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CenturyGold
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
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun LocationRadarScreen(
    viewModel: K9ViewModel,
    modifier: Modifier = Modifier
) {
    val currentLat by viewModel.currentLatitude.collectAsState()
    val currentLon by viewModel.currentLongitude.collectAsState()
    val sectorProximities by viewModel.sectorProximities.collectAsState()
    val selectedSector by viewModel.selectedSector.collectAsState()

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            viewModel.refreshLocation()
        }
    }

    // Radar Sweep Animation
    val infiniteTransition = rememberInfiniteTransition(label = "RadarSweep")
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "SweepAngle"
    )

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
                        text = "TACTICAL SECTOR RADAR",
                        color = CenturyGold,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Real-time distance to official academy training sectors.",
                        color = SurfaceTextMuted,
                        fontSize = 12.sp
                    )
                }

                Button(
                    onClick = {
                        locationPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                        viewModel.refreshLocation()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBluePrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("refresh_gps_button")
                ) {
                    Icon(Icons.Default.GpsFixed, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Fix GPS", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Radar Canvas Display Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = NavyDark),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CenturyGold.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("tactical_radar_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Position status bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(NavyCard)
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(LaurelGreenLight)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "GPS FIX: %.4f, %.4f".format(currentLat, currentLon),
                                color = SurfaceTextWhite,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        AcademyStatusBadge(text = "SWEEP ACTIVE", color = LaurelGreenLight)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Radar circular canvas
                    Box(
                        modifier = Modifier
                            .size(260.dp)
                            .clip(CircleShape)
                            .background(NavyDarkest)
                            .border(2.dp, CenturyGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        RadarCanvas(
                            sweepAngle = sweepAngle,
                            proximities = sectorProximities,
                            selectedSector = selectedSector,
                            onSectorClick = { viewModel.selectSector(it) }
                        )

                        // Center Handler Marker
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(CenturyGold)
                                .border(2.dp, SurfaceTextWhite, CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        RadarLegendItem(color = CenturyGold, label = "Handler (You)")
                        RadarLegendItem(color = LaurelGreenLight, label = "Active Sector")
                        RadarLegendItem(color = RoyalBlueLight, label = "Sweep Radar")
                    }
                }
            }
        }

        // Sector List Header
        item {
            Text(
                text = "ACADEMY SECTOR COMPOUNDS (${sectorProximities.size})",
                color = SurfaceTextWhite,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        // List of Sectors with Distances & Active Drills
        items(sectorProximities) { item ->
            val isSelected = selectedSector?.id == item.sector.id
            SectorCardItem(
                proximity = item,
                isSelected = isSelected,
                onClick = {
                    viewModel.selectSector(if (isSelected) null else item.sector)
                }
            )
        }
    }
}

@Composable
fun RadarCanvas(
    sweepAngle: Float,
    proximities: List<SectorProximity>,
    selectedSector: AcademySector?,
    onSectorClick: (AcademySector) -> Unit
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.width / 2

        // Range rings (25%, 50%, 75%, 100%)
        val ringColor = Color(0xFF1B3A6B)
        drawCircle(color = ringColor, radius = radius * 0.33f, center = center, style = Stroke(width = 1.dp.toPx()))
        drawCircle(color = ringColor, radius = radius * 0.66f, center = center, style = Stroke(width = 1.dp.toPx()))
        drawCircle(color = ringColor, radius = radius * 0.98f, center = center, style = Stroke(width = 1.5.dp.toPx()))

        // Crosshairs
        drawLine(
            color = ringColor,
            start = Offset(center.x, 0f),
            end = Offset(center.x, size.height),
            strokeWidth = 1.dp.toPx()
        )
        drawLine(
            color = ringColor,
            start = Offset(0f, center.y),
            end = Offset(size.width, center.y),
            strokeWidth = 1.dp.toPx()
        )

        // Radar Sweep Line & Cone
        val sweepRad = Math.toRadians(sweepAngle.toDouble())
        val endX = center.x + radius * cos(sweepRad).toFloat()
        val endY = center.y + radius * sin(sweepRad).toFloat()

        drawLine(
            color = CenturyGold.copy(alpha = 0.8f),
            start = center,
            end = Offset(endX, endY),
            strokeWidth = 2.dp.toPx()
        )

        // Sector Blips
        val maxDist = proximities.maxOfOrNull { it.distanceMiles }?.coerceAtLeast(10.0) ?: 20.0
        proximities.forEach { prox ->
            val normDist = (prox.distanceMiles / maxDist).coerceIn(0.25, 0.88).toFloat() * radius
            val angleRad = Math.toRadians((prox.bearingDegrees - 90).toDouble())
            val blipX = center.x + normDist * cos(angleRad).toFloat()
            val blipY = center.y + normDist * sin(angleRad).toFloat()

            val isSel = selectedSector?.id == prox.sector.id
            val blipColor = if (isSel) CenturyGold else LaurelGreenLight

            if (isSel) {
                drawCircle(
                    color = CenturyGold.copy(alpha = 0.35f),
                    radius = 12.dp.toPx(),
                    center = Offset(blipX, blipY)
                )
            }

            drawCircle(
                color = blipColor,
                radius = if (isSel) 6.dp.toPx() else 4.dp.toPx(),
                center = Offset(blipX, blipY)
            )
        }
    }
}

@Composable
fun RadarLegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, color = SurfaceTextMuted, fontSize = 10.sp)
    }
}

@Composable
fun SectorCardItem(
    proximity: SectorProximity,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val sector = proximity.sector
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) NavyCardElevated else NavyCard
        ),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) CenturyGold else BorderSubtle
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("sector_card_${sector.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(NavyDark)
                            .border(1.dp, CenturyGold.copy(alpha = 0.6f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Security,
                            contentDescription = null,
                            tint = CenturyGold,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = sector.codeName,
                            color = CenturyGoldLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = sector.name,
                            color = SurfaceTextWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "%.1f mi".format(proximity.distanceMiles),
                        color = LaurelGreenLight,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Bearing %d°".format(proximity.bearingDegrees.toInt()),
                        color = SurfaceTextMuted,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = sector.focusDescription,
                color = SurfaceTextMuted,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(NavyDarkest)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Drills: ", color = CenturyGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text(sector.activeDrills, color = SurfaceTextWhite, fontSize = 11.sp)
                }
                Text("Lead: ${sector.chiefInstructor}", color = SurfaceTextMuted, fontSize = 10.sp)
            }
        }
    }
}
