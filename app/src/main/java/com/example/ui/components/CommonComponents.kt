package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.EnrollmentEntity
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CenturyGold
import com.example.ui.theme.CenturyGoldDark
import com.example.ui.theme.CenturyGoldLight
import com.example.ui.theme.LaurelGreen
import com.example.ui.theme.NavyCard
import com.example.ui.theme.NavyCardElevated
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyDarkest
import com.example.ui.theme.RoyalBluePrimary
import com.example.ui.theme.SurfaceTextMuted
import com.example.ui.theme.SurfaceTextWhite
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AcademyTopHeader(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = NavyDark,
        tonalElevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Official K9 Century Academy Emblem
                Image(
                    painter = painterResource(id = R.drawable.k9_century_crest),
                    contentDescription = "K9 Century Academy Crest",
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, CenturyGold.copy(alpha = 0.6f), RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "K9 CENTURY",
                            color = SurfaceTextWhite,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ACADEMY",
                            color = CenturyGold,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                    Text(
                        text = "INSPECT WHAT YOU EXPECT.",
                        color = CenturyGoldLight,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.5.sp
                    )
                }
            }
        }
    }
}

@Composable
fun AcademyStatusBadge(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = CenturyGold,
    backgroundColor: Color = NavyCardElevated
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(backgroundColor)
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun OfficialCertificateDialog(
    cadet: EnrollmentEntity,
    onDismiss: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.US)
    val dateStr = dateFormat.format(Date(cadet.paymentDate))

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = CenturyGold, contentColor = NavyDarkest),
                modifier = Modifier.testTag("dismiss_certificate_btn")
            ) {
                Text("Official Confirmation Received", fontWeight = FontWeight.Bold)
            }
        },
        title = null,
        text = {
            Card(
                colors = CardDefaults.cardColors(containerColor = NavyDark),
                border = androidx.compose.foundation.BorderStroke(2.dp, CenturyGold),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.k9_century_crest),
                        contentDescription = "Official Academy Crest",
                        modifier = Modifier.size(90.dp),
                        contentScale = ContentScale.Fit
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "K9 CENTURY ACADEMY",
                        color = CenturyGold,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        letterSpacing = 1.2.sp
                    )

                    Text(
                        text = "“INSPECT WHAT YOU EXPECT.”",
                        color = SurfaceTextWhite,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = CenturyGoldDark.copy(alpha = 0.5f), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "CERTIFICATE OF SENTRY ENROLLMENT",
                        color = SurfaceTextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = cadet.dogName.uppercase(Locale.US),
                        color = CenturyGoldLight,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "${cadet.breed} • ${cadet.dogAgeMonths} Months",
                        color = SurfaceTextWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(NavyCard, RoundedCornerShape(8.dp))
                            .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Registered Handler:", color = SurfaceTextMuted, fontSize = 11.sp)
                                Text(cadet.ownerName, color = SurfaceTextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Assigned Track:", color = SurfaceTextMuted, fontSize = 11.sp)
                                Text(cadet.courseTrack, color = CenturyGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Tuition Status:", color = SurfaceTextMuted, fontSize = 11.sp)
                                Text("$%.2f (Paid)".format(cadet.amountPaid), color = LaurelGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Date Authenticated:", color = SurfaceTextMuted, fontSize = 11.sp)
                                Text(dateStr, color = SurfaceTextWhite, fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Credential ID: K9CA-${cadet.id.toString().padStart(5, '0')}",
                        color = SurfaceTextMuted,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        },
        containerColor = Color.Transparent
    )
}

@Composable
fun BorderSubtleBrush(): Brush {
    return Brush.linearGradient(
        colors = listOf(CenturyGold.copy(alpha = 0.4f), RoyalBluePrimary.copy(alpha = 0.4f))
    )
}
