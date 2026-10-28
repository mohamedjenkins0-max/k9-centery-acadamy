package com.example.ui.screens

import android.util.Patterns
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import androidx.compose.material3.MenuAnchorType
import com.example.ui.theme.NavyCardElevated
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyDarkest
import com.example.ui.theme.RoyalBluePrimary
import com.example.ui.theme.SurfaceTextMuted
import com.example.ui.theme.SurfaceTextWhite

val WORKING_BREEDS = listOf(
    "Belgian Malinois",
    "German Shepherd",
    "Dutch Shepherd",
    "Rottweiler",
    "Doberman Pinscher",
    "Giant Schnauzer",
    "Cane Corso",
    "Boxer (Working Line)"
)

val COURSE_TRACKS = listOf(
    "Sentry Perimeter & Guard",
    "Executive Protection Handler",
    "Scent & Detection Specialist",
    "Puppy Sentry Foundation"
)

val TUITION_PLANS = listOf(
    "Full Tuition ($2,850)",
    "50% Milestone Deposit ($1,425)",
    "Monthly Handler Dues ($475/mo)"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnrollmentScreen(
    viewModel: K9ViewModel,
    modifier: Modifier = Modifier
) {
    val formState by viewModel.formState.collectAsState()

    var breedExpanded by remember { mutableStateOf(false) }
    var trackExpanded by remember { mutableStateOf(false) }

    val isEmailValid = remember(formState.email) {
        formState.email.isEmpty() || Patterns.EMAIL_ADDRESS.matcher(formState.email).matches()
    }

    val isPhoneValid = remember(formState.phone) {
        val digits = formState.phone.filter { it.isDigit() }
        digits.isEmpty() || digits.length == 10
    }

    val cardBrand = remember(formState.cardNumber) {
        viewModel.detectCardBrand(formState.cardNumber)
    }

    // Modal Certificate on successful enrollment
    if (formState.submissionSuccess && formState.lastSubmittedCadet != null) {
        OfficialCertificateDialog(
            cadet = formState.lastSubmittedCadet!!,
            onDismiss = { viewModel.dismissSubmissionSuccess() }
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
        // Section Header
        item {
            Column {
                Text(
                    text = "CANINE SENTRY ENROLLMENT",
                    color = CenturyGold,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Register handler contact, canine pedigree, field training track, and authenticate tuition.",
                    color = SurfaceTextMuted,
                    fontSize = 12.sp
                )
            }
        }

        // Error Banner
        if (formState.errorMessage != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = AlertRed.copy(alpha = 0.15f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AlertRed),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = AlertRed)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = formState.errorMessage!!,
                            color = SurfaceTextWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Section 1: Handler & Client Information
        item {
            FormSectionCard(
                title = "1. Handler & Client Details",
                icon = Icons.Default.AccountCircle
            ) {
                OutlinedTextField(
                    value = formState.ownerName,
                    onValueChange = { viewModel.updateOwnerName(it) },
                    label = { Text("Full Name (Client / Handler)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_owner_name"),
                    colors = k9TextFieldColors(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = formState.email,
                    onValueChange = { viewModel.updateEmail(it) },
                    label = { Text("Email (Syllabus & Invoices)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_email"),
                    colors = k9TextFieldColors(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    isError = !isEmailValid,
                    supportingText = {
                        if (!isEmailValid) {
                            Text("Invalid email format (e.g., handler@domain.com)", color = AlertRed, fontSize = 11.sp)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = formState.phone,
                    onValueChange = { viewModel.updatePhone(it) },
                    label = { Text("Emergency Contact Phone") },
                    placeholder = { Text("(555) 000-0000") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_phone"),
                    colors = k9TextFieldColors(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    isError = !isPhoneValid,
                    supportingText = {
                        if (!isPhoneValid) {
                            Text("Must be 10 digits for field emergency alerts", color = AlertRed, fontSize = 11.sp)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = formState.streetAddress,
                    onValueChange = { viewModel.updateStreetAddress(it) },
                    label = { Text("Service & Field Residence Address") },
                    placeholder = { Text("123 Sentry Way, Los Angeles, CA") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_street_address"),
                    colors = k9TextFieldColors(),
                    singleLine = true
                )
            }
        }

        // Section 2: Canine Profile
        item {
            FormSectionCard(
                title = "2. Canine Profile & Credentials",
                icon = Icons.Default.Pets
            ) {
                OutlinedTextField(
                    value = formState.dogName,
                    onValueChange = { viewModel.updateDogName(it) },
                    label = { Text("Canine Call Name / Registered Name") },
                    placeholder = { Text("e.g., Ares, Valkyrie, Titan") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_dog_name"),
                    colors = k9TextFieldColors(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Breed Selector Dropdown
                ExposedDropdownMenuBox(
                    expanded = breedExpanded,
                    onExpandedChange = { breedExpanded = !breedExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = formState.breed,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Working Sentry Breed") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = breedExpanded) },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                            .fillMaxWidth()
                            .testTag("dropdown_breed"),
                        colors = k9TextFieldColors()
                    )
                    ExposedDropdownMenu(
                        expanded = breedExpanded,
                        onDismissRequest = { breedExpanded = false },
                        modifier = Modifier.background(NavyCard)
                    ) {
                        WORKING_BREEDS.forEach { breed ->
                            DropdownMenuItem(
                                text = { Text(breed, color = SurfaceTextWhite) },
                                onClick = {
                                    viewModel.updateBreed(breed)
                                    breedExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Dog Age Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Canine Age:", color = SurfaceTextWhite, fontSize = 13.sp)
                    AcademyStatusBadge(
                        text = "${formState.dogAgeMonths} Months (${String.format("%.1f", formState.dogAgeMonths / 12f)} yrs)",
                        color = CenturyGold
                    )
                }

                Slider(
                    value = formState.dogAgeMonths.toFloat(),
                    onValueChange = { viewModel.updateDogAge(it.toInt()) },
                    valueRange = 2f..96f,
                    steps = 47,
                    colors = SliderDefaults.colors(
                        thumbColor = CenturyGold,
                        activeTrackColor = CenturyGold,
                        inactiveTrackColor = BorderSubtle
                    ),
                    modifier = Modifier.testTag("slider_dog_age")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Course Track Dropdown
                ExposedDropdownMenuBox(
                    expanded = trackExpanded,
                    onExpandedChange = { trackExpanded = !trackExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = formState.courseTrack,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Academy Certification Track") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = trackExpanded) },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                            .fillMaxWidth()
                            .testTag("dropdown_course_track"),
                        colors = k9TextFieldColors()
                    )
                    ExposedDropdownMenu(
                        expanded = trackExpanded,
                        onDismissRequest = { trackExpanded = false },
                        modifier = Modifier.background(NavyCard)
                    ) {
                        COURSE_TRACKS.forEach { track ->
                            DropdownMenuItem(
                                text = { Text(track, color = SurfaceTextWhite) },
                                onClick = {
                                    viewModel.updateCourseTrack(track)
                                    trackExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // Section 3: Tuition & Payment Authentication
        item {
            FormSectionCard(
                title = "3. Tuition Billing & Card Authorization",
                icon = Icons.Default.CreditCard
            ) {
                Text("Select Tuition Option:", color = SurfaceTextMuted, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))

                TUITION_PLANS.forEach { plan ->
                    val isSelected = formState.tuitionPlan == plan
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) NavyCardElevated else NavyDark)
                            .border(
                                1.dp,
                                if (isSelected) CenturyGold else BorderSubtle,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { viewModel.updateTuitionPlan(plan) }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = plan,
                            color = if (isSelected) CenturyGoldLight else SurfaceTextWhite,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                        if (isSelected) {
                            AcademyStatusBadge(text = "Selected", color = CenturyGold)
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = formState.cardholderName,
                    onValueChange = { viewModel.updateCardholder(it) },
                    label = { Text("Cardholder Name (Printed on Card)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_cardholder_name"),
                    colors = k9TextFieldColors(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = formState.cardNumber,
                    onValueChange = { viewModel.updateCardNumber(it) },
                    label = { Text("Card Number (16 Digits)") },
                    placeholder = { Text("4242 •••• •••• 4242") },
                    trailingIcon = {
                        AcademyStatusBadge(
                            text = cardBrand,
                            color = if (cardBrand != "Card") CenturyGold else SurfaceTextMuted
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_card_number"),
                    colors = k9TextFieldColors(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = formState.expiryDate,
                        onValueChange = { viewModel.updateExpiryDate(it) },
                        label = { Text("Expires") },
                        placeholder = { Text("MM/YY") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_expiry"),
                        colors = k9TextFieldColors(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = formState.cvv,
                        onValueChange = { viewModel.updateCvv(it) },
                        label = { Text("CVV/CVC") },
                        placeholder = { Text("123") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_cvv"),
                        colors = k9TextFieldColors(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = formState.billingZip,
                        onValueChange = { viewModel.updateBillingZip(it) },
                        label = { Text("ZIP") },
                        placeholder = { Text("90001") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_zip"),
                        colors = k9TextFieldColors(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = LaurelGreenLight,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Encrypted in local Room database. Compliant with academy security standards.",
                        color = SurfaceTextMuted,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Submit Action Button
        item {
            Button(
                onClick = { viewModel.submitEnrollment() },
                enabled = !formState.isSubmitting,
                colors = ButtonDefaults.buttonColors(
                    containerColor = CenturyGold,
                    contentColor = NavyDarkest,
                    disabledContainerColor = CenturyGoldDark.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("submit_enrollment_button")
            ) {
                if (formState.isSubmitting) {
                    CircularProgressIndicator(
                        color = NavyDarkest,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Authenticating Cadet...", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                } else {
                    Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AUTHORIZE & ENROLL CADET",
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        letterSpacing = 0.8.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun FormSectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = NavyDark),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = CenturyGold, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = title, color = SurfaceTextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(14.dp))
            content()
        }
    }
}

@Composable
fun k9TextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = SurfaceTextWhite,
    unfocusedTextColor = SurfaceTextWhite,
    focusedContainerColor = NavyCard,
    unfocusedContainerColor = NavyCard,
    focusedBorderColor = CenturyGold,
    unfocusedBorderColor = BorderSubtle,
    focusedLabelColor = CenturyGold,
    unfocusedLabelColor = SurfaceTextMuted,
    focusedPlaceholderColor = SurfaceTextMuted,
    unfocusedPlaceholderColor = SurfaceTextMuted
)
