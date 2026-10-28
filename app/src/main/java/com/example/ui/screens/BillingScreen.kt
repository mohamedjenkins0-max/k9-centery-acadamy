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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.EnrollmentEntity
import com.example.ui.K9ViewModel
import com.example.ui.components.AcademyStatusBadge
import com.example.ui.components.OfficialCertificateDialog
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
import com.example.ui.theme.SurfaceTextMuted
import com.example.ui.theme.SurfaceTextWhite
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BillingScreen(
    viewModel: K9ViewModel,
    modifier: Modifier = Modifier
) {
    val enrollments by viewModel.enrollments.collectAsState()
    val totalRevenue by viewModel.totalRevenue.collectAsState()
    val cadetCount by viewModel.cadetCount.collectAsState()

    var inspectingCadet by remember { mutableStateOf<EnrollmentEntity?>(null) }

    if (inspectingCadet != null) {
        OfficialCertificateDialog(
            cadet = inspectingCadet!!,
            onDismiss = { inspectingCadet = null }
        )
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
            Column {
                Text(
                    text = "TUITION & INVOICE LEDGER",
                    color = CenturyGold,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Audited records of cadet tuition authorizations and credentials.",
                    color = SurfaceTextMuted,
                    fontSize = 12.sp
                )
            }
        }

        // Financial Ledger Summary Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = NavyDark),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CenturyGold.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("billing_summary_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "TOTAL TUITION ACCREDITED",
                        color = SurfaceTextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$%,.2f".format(totalRevenue ?: 0.0),
                        color = CenturyGold,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(NavyCard)
                                .padding(10.dp)
                        ) {
                            Column {
                                Text("Registered Cadets", color = SurfaceTextMuted, fontSize = 10.sp)
                                Text("$cadetCount Dogs", color = SurfaceTextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(NavyCard)
                                .padding(10.dp)
                        ) {
                            Column {
                                Text("Average Authorization", color = SurfaceTextMuted, fontSize = 10.sp)
                                val avg = if (cadetCount > 0) (totalRevenue ?: 0.0) / cadetCount else 0.0
                                Text("$%,.0f".format(avg), color = LaurelGreenLight, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Transactions List Title
        item {
            Text(
                text = "AUTHENTICATED INVOICES (${enrollments.size})",
                color = SurfaceTextWhite,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        // Ledger Items
        if (enrollments.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = NavyCard),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No invoices registered. Complete an enrollment to process tuition.",
                        color = SurfaceTextMuted,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(enrollments) { item ->
                InvoiceCardItem(
                    enrollment = item,
                    onViewCertificate = { inspectingCadet = item },
                    onDelete = { viewModel.deleteEnrollment(item) }
                )
            }
        }
    }
}

@Composable
fun InvoiceCardItem(
    enrollment: EnrollmentEntity,
    onViewCertificate: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)
    val dateStr = dateFormat.format(Date(enrollment.paymentDate))

    Card(
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onViewCertificate() }
            .testTag("invoice_card_${enrollment.id}")
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
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(NavyDarkest)
                            .border(1.dp, CenturyGold.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Receipt,
                            contentDescription = null,
                            tint = CenturyGold,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "CADET ${enrollment.dogName.uppercase()}",
                            color = SurfaceTextWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Handler: ${enrollment.ownerName}",
                            color = SurfaceTextMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$%,.2f".format(enrollment.amountPaid),
                        color = LaurelGreenLight,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = dateStr,
                        color = SurfaceTextMuted,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Details strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(NavyDark)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CreditCard,
                        contentDescription = null,
                        tint = CenturyGoldLight,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${enrollment.cardBrand} •••• ${enrollment.cardLast4}",
                        color = SurfaceTextWhite,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Text(
                    text = "ID: K9CA-${enrollment.id.toString().padStart(4, '0')}",
                    color = SurfaceTextMuted,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = enrollment.courseTrack,
                    color = CenturyGoldLight,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    AcademyStatusBadge(text = enrollment.status, color = LaurelGreen)
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Discharge Record",
                            tint = AlertRed.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
