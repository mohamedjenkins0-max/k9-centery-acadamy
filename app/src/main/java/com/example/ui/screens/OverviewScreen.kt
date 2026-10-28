package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.EnrollmentEntity
import com.example.ui.AppTab
import com.example.ui.K9ViewModel
import com.example.ui.components.AcademyStatusBadge
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
import com.example.ui.theme.RoyalBlueDark
import com.example.ui.theme.RoyalBlueLight
import com.example.ui.theme.RoyalBluePrimary
import com.example.ui.theme.SurfaceTextMuted
import com.example.ui.theme.SurfaceTextWhite

@Composable
fun OverviewScreen(
    viewModel: K9ViewModel,
    modifier: Modifier = Modifier
) {
    val cadetCount by viewModel.cadetCount.collectAsState()
    val totalRevenue by viewModel.totalRevenue.collectAsState()
    val enrollments by viewModel.enrollments.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(NavyDarkest)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Academy Brand Card with Crest
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = NavyDark),
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp,
                    Brush.verticalGradient(listOf(CenturyGold, RoyalBluePrimary))
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("overview_hero_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.k9_century_crest),
                        contentDescription = "Official K9 Century Academy Crest",
                        modifier = Modifier
                            .size(130.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Fit
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "K9 CENTURY ACADEMY",
                        color = CenturyGold,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.2.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Brush.horizontalGradient(listOf(RoyalBlueDark, RoyalBluePrimary)))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "“INSPECT WHAT YOU EXPECT.”",
                            color = SurfaceTextWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Elite security canine conditioning, executive protection certifications, tactical field tracking, and handler operational readiness.",
                        color = SurfaceTextMuted,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { viewModel.setTab(AppTab.ENROLLMENT) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CenturyGold,
                                contentColor = NavyDarkest
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("hero_enroll_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Enroll Cadet", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { viewModel.setTab(AppTab.RADAR) },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = CenturyGoldLight),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CenturyGold),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("hero_radar_button")
                        ) {
                            Icon(Icons.Default.Radar, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Sectors Radar", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // Academy Operational Metrics
        item {
            Text(
                text = "OPERATIONAL METRICS",
                color = SurfaceTextWhite,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Enrolled Cadets",
                    value = "$cadetCount",
                    subtitle = "4 Active Breeds",
                    icon = Icons.Default.Pets,
                    color = RoyalBlueLight,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Active Sectors",
                    value = "4",
                    subtitle = "100% Operational",
                    icon = Icons.Default.LocationOn,
                    color = LaurelGreenLight,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Passing Rate",
                    value = "99.4%",
                    subtitle = "Sentry Standards",
                    icon = Icons.Default.CheckCircle,
                    color = CenturyGold,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Tuition Processed",
                    value = "$%,.0f".format(totalRevenue ?: 0.0),
                    subtitle = "Secure Ledger",
                    icon = Icons.Default.Payment,
                    color = CenturyGoldLight,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Certification Tracks Section
        item {
            Text(
                text = "CERTIFIED TRAINING TRACKS",
                color = SurfaceTextWhite,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            CertificationTrackCard(
                title = "Sentry Perimeter & Guard",
                level = "Tier I Standard",
                weeks = "12 Weeks Intensive",
                description = "Boundary containment, bite control release, active night sentry patrol, handler escort commands.",
                icon = Icons.Default.Shield
            )
            Spacer(modifier = Modifier.height(10.dp))
            CertificationTrackCard(
                title = "Executive Protection Handler",
                level = "Tier II Tactical",
                weeks = "16 Weeks Advanced",
                description = "Close-quarter VIP guarding, vehicle ambush counter, civil agitation desensitization, defensive engagement.",
                icon = Icons.Default.Security
            )
            Spacer(modifier = Modifier.height(10.dp))
            CertificationTrackCard(
                title = "Scent & Detection Specialist",
                level = "Specialized Search",
                weeks = "14 Weeks Technical",
                description = "Odor discrimination, blind hide recovery, luggage and vehicle parcel sweeps, passive freeze alerts.",
                icon = Icons.Default.Radar
            )
            Spacer(modifier = Modifier.height(10.dp))
            CertificationTrackCard(
                title = "Puppy Sentry Foundation",
                level = "Developmental",
                weeks = "8 Weeks Foundation",
                description = "Early neurological stimulation, prey drive shaping, confident environmental grip, foundational obedience.",
                icon = Icons.Default.Star
            )
        }

        // Active Enrolled Squadron Cadets Preview
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RECENT CADET ENROLLMENTS",
                    color = SurfaceTextWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "View All (${enrollments.size})",
                    color = CenturyGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { viewModel.setTab(AppTab.BILLING) }
                        .padding(4.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (enrollments.isEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = NavyCard),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No cadets currently recorded. Tap 'Enroll Cadet' to register your first canine.",
                        color = SurfaceTextMuted,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                enrollments.take(3).forEach { cadet ->
                    CadetQuickItem(cadet = cadet)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, color = SurfaceTextMuted, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = value, color = SurfaceTextWhite, fontSize = 22.sp, fontWeight = FontWeight.Black)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, color = color, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun CertificationTrackCard(
    title: String,
    level: String,
    weeks: String,
    description: String,
    icon: ImageVector
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(NavyCardElevated)
                    .border(1.dp, CenturyGold.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = CenturyGold, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = title, color = SurfaceTextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    AcademyStatusBadge(text = level, color = CenturyGoldLight)
                }
                Text(text = weeks, color = LaurelGreenLight, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = description, color = SurfaceTextMuted, fontSize = 12.sp, lineHeight = 16.sp)
            }
        }
    }
}

@Composable
fun CadetQuickItem(cadet: EnrollmentEntity) {
    Card(
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(RoyalBlueDark),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = cadet.dogName.take(1).uppercase(),
                        color = CenturyGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(text = cadet.dogName, color = SurfaceTextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(text = "${cadet.breed} • ${cadet.courseTrack}", color = SurfaceTextMuted, fontSize = 11.sp)
                }
            }
            AcademyStatusBadge(
                text = cadet.status,
                color = if (cadet.status.contains("Certified")) LaurelGreenLight else CenturyGold
            )
        }
    }
}
