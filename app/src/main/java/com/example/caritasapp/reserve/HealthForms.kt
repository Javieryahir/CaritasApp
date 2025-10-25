package com.example.caritasapp.reserve

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.caritasapp.composables.HyperlinkText
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val Accent = Color(0xFF009CA6)

@Composable
fun HealthFormsScreen(navController: NavController, count: Int) {
    // Debug logging to confirm HealthForms screen is reached
    LaunchedEffect(Unit) {
        println("🔍 HealthForms Screen Loaded:")
        println("  count: $count")
        println("  navController: $navController")
    }
    
    val firstNames    = remember(count) { MutableList(count) { mutableStateOf("") } }
    val lastNames     = remember(count) { MutableList(count) { mutableStateOf("") } }
    val birthDates    = remember(count) { MutableList(count) { mutableStateOf("") } }
    
    // Dynamic lists for health information
    val allergiesList = remember(count) { MutableList(count) { mutableStateOf(listOf("")) } }
    val disabilitiesList = remember(count) { MutableList(count) { mutableStateOf(listOf("")) } }
    val medsList = remember(count) { MutableList(count) { mutableStateOf(listOf("")) } }
    
    // Track which fields are marked as "none"
    val allergiesNone = remember(count) { MutableList(count) { mutableStateOf(false) } }
    val disabilitiesNone = remember(count) { MutableList(count) { mutableStateOf(false) } }
    val medsNone = remember(count) { MutableList(count) { mutableStateOf(false) } }
    
    // Track card expansion state
    val cardExpanded = remember(count) { MutableList(count) { mutableStateOf(true) } }

    val scroll = rememberScrollState()

    // Validación global
    val areFirstNamesValid = firstNames.all { it.value.isNotBlank() }
    val areLastNamesValid = lastNames.all { it.value.isNotBlank() }
    val areBirthDatesValid = birthDates.all { state ->
        val dateStr = state.value
        if (dateStr.isBlank()) false
        else {
            try {
                val parts = dateStr.split("-")
                if (parts.size != 3) false
                else {
                    val year = parts[0].toIntOrNull() ?: return@all false
                    val month = parts[1].toIntOrNull() ?: return@all false
                    val day = parts[2].toIntOrNull() ?: return@all false
                    year in 1900..2025 && month in 1..12 && day in 1..31
                }
            } catch (_: Exception) {
                false
            }
        }
    }
    val isFormValid = areFirstNamesValid && areLastNamesValid && areBirthDatesValid
    
    // Debug validation state
    LaunchedEffect(areFirstNamesValid, areLastNamesValid, areBirthDatesValid, isFormValid) {
        println("🔍 HealthForms Validation Debug:")
        println("  areFirstNamesValid: $areFirstNamesValid")
        println("  areLastNamesValid: $areLastNamesValid") 
        println("  areBirthDatesValid: $areBirthDatesValid")
        println("  isFormValid: $isFormValid")
        println("  First names: ${firstNames.map { it.value }}")
        println("  Last names: ${lastNames.map { it.value }}")
        println("  Birth dates: ${birthDates.map { it.value }}")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .imePadding()
    ) {
        Column(Modifier.fillMaxSize()) {
            // Header with gradient background
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Accent,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Formulario de Salud",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Información médica de $count ${if (count == 1) "persona" else "personas"}",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Scrollable content area
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(scroll)
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                repeat(count) { idx ->
                    // Spacing between cards
                    if (idx > 0) {
                        Spacer(Modifier.height(20.dp))
                    }

                    PersonFormCard(
                        index = idx,
                        total = count,
                        firstName = firstNames[idx].value,
                        onFirstName = { firstNames[idx].value = it },
                        lastName = lastNames[idx].value,
                        onLastName = { lastNames[idx].value = it },
                        birthDate = birthDates[idx].value,
                        onBirthDate = { value -> birthDates[idx].value = value },
                        allergiesList = allergiesList[idx].value,
                        onAllergiesList = { allergiesList[idx].value = it },
                        allergiesNone = allergiesNone[idx].value,
                        onAllergiesNone = { allergiesNone[idx].value = it },
                        disabilitiesList = disabilitiesList[idx].value,
                        onDisabilitiesList = { disabilitiesList[idx].value = it },
                        disabilitiesNone = disabilitiesNone[idx].value,
                        onDisabilitiesNone = { disabilitiesNone[idx].value = it },
                        medsList = medsList[idx].value,
                        onMedsList = { medsList[idx].value = it },
                        medsNone = medsNone[idx].value,
                        onMedsNone = { medsNone[idx].value = it },
                        isExpanded = cardExpanded[idx].value,
                        onExpandedChange = { cardExpanded[idx].value = it }
                    )
                }

                Spacer(Modifier.height(18.dp))

                HyperlinkText(
                    fullText = "Política de privacidad",
                    hyperlinks = mapOf(
                        "Política de privacidad" to
                                "https://tecmx-my.sharepoint.com/:b:/r/personal/a01782862_tec_mx/Documents/AppMovil/PoliticasdePrivacidad/AVISO%20DE%20PRIVACIDAD%202025.pdf?csf=1&web=1&e=Tum1V9"
                    ),
                    fontSize = 22.sp,
                    textColor = Color(0xFFE7E7E7),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(12.dp))
            }

            // Fixed bottom buttons with modern design
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back button
                    OutlinedButton(
                        onClick = { navController.popBackStack() },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(56.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Accent
                        ),
                        border = BorderStroke(2.dp, Accent)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Atrás", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }


                    // Continue button
                    Button(
                        onClick = { 
                            // Prepare data for API call
                            val personsData = preparePersonsData(
                                firstNames, lastNames, birthDates,
                                allergiesList, allergiesNone,
                                disabilitiesList, disabilitiesNone,
                                medsList, medsNone
                            )
                            
                            // Debug logging
                            println("🔍 HealthForms Debug:")
                            println("  personsData size: ${personsData.size}")
                            if (personsData.isNotEmpty()) {
                                println("  First person: ${personsData[0].firstName} ${personsData[0].lastName}")
                                println("  First person birthDate: ${personsData[0].birthDate}")
                            }
                            
                            // Store data for API call in multiple back stack entries for robustness
                            navController.currentBackStackEntry?.savedStateHandle?.set("personsData", personsData)
                            navController.getBackStackEntry("search").savedStateHandle["personsData"] =
                                personsData
                            navController.getBackStackEntry("shelter").savedStateHandle["personsData"] =
                                personsData
                            println("  ✅ personsData stored in currentBackStackEntry, search, and shelter entries")
                            
                            // Verify storage by reading it back
                            val storedData = navController.currentBackStackEntry?.savedStateHandle?.get<List<PersonData>>("personsData")
                            println("  🔍 Verification - stored data size: ${storedData?.size ?: "null"}")
                            if (storedData != null && storedData.isNotEmpty()) {
                                println("  ✅ Data successfully stored and retrieved!")
                            } else {
                                println("  ❌ Data storage failed!")
                            }
                            
                            // Pass dates through to confirmation
                            val currentHandle = navController.currentBackStackEntry?.savedStateHandle
                            val startDate = currentHandle?.get<String>("startDate")
                            val endDate = currentHandle?.get<String>("endDate")
                            
                            if (startDate != null && endDate != null) {
                                navController.currentBackStackEntry?.savedStateHandle?.apply {
                                    set("startDate", startDate)
                                    set("endDate", endDate)
                                }
                            }
                            
                            navController.navigate("confirm") 
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Accent,
                            contentColor = Color.White,
                            disabledContainerColor = Accent.copy(alpha = 0.6f),
                            disabledContentColor = Color.White.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier
                            .height(56.dp)
                            .weight(1f)
                            .testTag("continueButton"),
                        enabled = isFormValid
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CalendarToday,
                            contentDescription = "Continuar",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Continuar", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}


/* ---------- UI de un formulario por persona ---------- */

@Composable
private fun PersonFormCard(
    index: Int,
    total: Int,
    firstName: String,
    onFirstName: (String) -> Unit,
    lastName: String,
    onLastName: (String) -> Unit,
    birthDate: String,
    onBirthDate: (String) -> Unit,
    allergiesList: List<String>,
    onAllergiesList: (List<String>) -> Unit,
    allergiesNone: Boolean,
    onAllergiesNone: (Boolean) -> Unit,
    disabilitiesList: List<String>,
    onDisabilitiesList: (List<String>) -> Unit,
    disabilitiesNone: Boolean,
    onDisabilitiesNone: (Boolean) -> Unit,
    medsList: List<String>,
    onMedsList: (List<String>) -> Unit,
    medsNone: Boolean,
    onMedsNone: (Boolean) -> Unit,
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Collapsible header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onExpandedChange(!isExpanded) },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Persona ${index + 1}",
                        color = Accent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Text(
                        text = "${index + 1}/$total",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Icon(
                    imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (isExpanded) "Contraer" else "Expandir",
                    tint = Accent,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Animated content
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    Spacer(Modifier.height(20.dp))

                    // Name fields in a row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // First name
                        Column(modifier = Modifier.weight(1f)) {
                            ModernFieldLabel("Nombre")
                            ModernField(
                                value = firstName,
                                onChange = onFirstName,
                                placeholder = "Nombre",
                                isError = firstName.isNotBlank() && firstName.isBlank(),
                                modifier = Modifier.testTag("firstNameField-$index")
                            )
                        }

                        // Last name
                        Column(modifier = Modifier.weight(1f)) {
                            ModernFieldLabel("Apellidos")
                            ModernField(
                                value = lastName,
                                onChange = onLastName,
                                placeholder = "Apellidos",
                                isError = lastName.isNotBlank() && lastName.isBlank(),
                                modifier = Modifier.testTag("lastNameField-$index")
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Birth date field with date picker
                    ModernFieldLabel("Fecha de nacimiento")
                    DatePickerField(
                        value = birthDate,
                        onValueChange = onBirthDate,
                        isError = birthDate.isNotBlank() && !isValidDateFormat(birthDate),
                        modifier = Modifier.testTag("birthDateField-$index")
                    )

                    Spacer(Modifier.height(20.dp))

                    // Health information section
                    Text(
                        text = "Información de Salud",
                        color = Color.Gray,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Dynamic Allergies List
                    DynamicListField(
                        label = "Alergias",
                        noneLabel = "Ninguna",
                        items = allergiesList,
                        onItemsChange = onAllergiesList,
                        isNone = allergiesNone,
                        onNoneChange = onAllergiesNone,
                        placeholder = "Especificar alergia"
                    )

                    Spacer(Modifier.height(16.dp))

                    // Dynamic Disabilities List
                    DynamicListField(
                        label = "Discapacidades",
                        noneLabel = "Ninguna",
                        items = disabilitiesList,
                        onItemsChange = onDisabilitiesList,
                        isNone = disabilitiesNone,
                        onNoneChange = onDisabilitiesNone,
                        placeholder = "Especificar discapacidad"
                    )

                    Spacer(Modifier.height(16.dp))

                    // Dynamic Medications List
                    DynamicListField(
                        label = "Medicamentos",
                        noneLabel = "Ninguno",
                        items = medsList,
                        onItemsChange = onMedsList,
                        isNone = medsNone,
                        onNoneChange = onMedsNone,
                        placeholder = "Especificar medicamento"
                    )
                }
            }
        }
    }
}

/* ---------- Modern UI Components ---------- */

@Composable
private fun ModernFieldLabel(text: String) {
    Text(
        text = text,
        color = Color.Gray,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(bottom = 6.dp        )
    )
}

@Composable
private fun DynamicListField(
    label: String,
    noneLabel: String,
    items: List<String>,
    onItemsChange: (List<String>) -> Unit,
    isNone: Boolean,
    onNoneChange: (Boolean) -> Unit,
    placeholder: String
) {
    Column {
        ModernFieldLabel(label)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Marca '$noneLabel' si no aplica",
                color = Color.Gray,
                fontSize = 12.sp
            )

            FilterChip(
                onClick = {
                    onNoneChange(!isNone)
                    if (!isNone) onItemsChange(listOf(""))
                },
                label = { Text(noneLabel, fontSize = 12.sp) },
                selected = isNone,
                modifier = Modifier.testTag("none-$label"),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Accent.copy(alpha = 0.1f),
                    selectedLabelColor = Accent
                )
            )
        }

        Spacer(Modifier.height(8.dp))

        if (!isNone) {
            // Show individual items
            items.forEachIndexed { index, item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ModernField(
                        value = item,
                        onChange = { newValue ->
                            val newItems = items.toMutableList()
                            newItems[index] = newValue
                            onItemsChange(newItems)
                        },
                        placeholder = placeholder,
                        isError = false,
                        enabled = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input-$label-$index")
                    )

                    // Remove button (only show if more than one item)
                    if (items.size > 1) {
                        IconButton(
                            onClick = {
                                val newItems = items.toMutableList()
                                newItems.removeAt(index)
                                onItemsChange(newItems)
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .testTag("remove-$label-$index")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Remove,
                                contentDescription = "Eliminar",
                                tint = Color.Red,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Add button
            OutlinedButton(
                onClick = {
                    val newItems = items.toMutableList()
                    newItems.add("")
                    onItemsChange(newItems)
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("add-$label"),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Accent
                ),
                border = BorderStroke(1.dp, Accent)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Agregar",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("Agregar $label", fontSize = 14.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModernField(
    value: String,
    onChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        singleLine = true,
        isError = isError,
        enabled = enabled,
        placeholder = { Text(placeholder, color = Color.Gray) },
        textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFF8F9FA),
            unfocusedContainerColor = Color(0xFFF8F9FA),
            disabledContainerColor = Color(0xFFF8F9FA),
            focusedBorderColor = Accent,
            unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
            errorBorderColor = Color.Red,
            cursorColor = Accent,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            disabledTextColor = Color.Gray,
            focusedPlaceholderColor = Color.Gray,
            unfocusedPlaceholderColor = Color.Gray
        )
    )
}


/* ---------- Date Picker Component ---------- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerField(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showDatePicker by remember { mutableStateOf(false) }
    
    // Parse current date or use today as default
    val currentDate = remember(value) {
        if (value.isNotEmpty() && isValidDateFormat(value)) {
            try {
                val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                format.parse(value) ?: Date()
            } catch (_: Exception) {
                Date()
            }
        } else {
            Date()
        }
    }
    
    val selectedDate = remember { mutableStateOf(currentDate) }
    
    // Format date for display
    val displayDate = remember(selectedDate.value) {
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        format.format(selectedDate.value)
    }
    
    // Format date for storage (YYYY-MM-DD)
    val storageDate = remember(selectedDate.value) {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        format.format(selectedDate.value)
    }
    
    // Update storage when date changes
    LaunchedEffect(selectedDate.value) {
        onValueChange(storageDate)
    }
    
    // Date picker dialog
    if (showDatePicker) {
        val datePickerDialog = android.app.DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val calendar = Calendar.getInstance()
                calendar.set(year, month, dayOfMonth)
                selectedDate.value = calendar.time
                showDatePicker = false
            },
            selectedDate.value.year + 1900,
            selectedDate.value.month,
            selectedDate.value.date
        )
        
        // Set max date to today (can't be born in the future)
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        
        // Set min date to 120 years ago
        val minDate = Calendar.getInstance()
        minDate.add(Calendar.YEAR, -120)
        datePickerDialog.datePicker.minDate = minDate.timeInMillis
        
        LaunchedEffect(Unit) {
            datePickerDialog.show()
        }
    }

    // Custom field with calendar icon
    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .clickable { showDatePicker = true },
        value = displayDate,
        onValueChange = { }, // Read-only, only opens picker
        readOnly = true,
        isError = isError,
        placeholder = {
            Text(
                "Seleccionar fecha",
                color = Color.Gray
            )
        },
        textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFF8F9FA),
            unfocusedContainerColor = Color(0xFFF8F9FA),
            disabledContainerColor = Color(0xFFF8F9FA),
            focusedBorderColor = Accent,
            unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
            errorBorderColor = Color.Red,
            cursorColor = Accent,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            disabledTextColor = Color.Gray,
            focusedPlaceholderColor = Color.Gray,
            unfocusedPlaceholderColor = Color.Gray
        ),
        trailingIcon = {
            IconButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.CalendarToday,
                    contentDescription = "Seleccionar fecha",
                    tint = Accent,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    )

}

/* ---------- Helper Functions ---------- */

private fun isValidDateFormat(dateStr: String): Boolean {
    if (dateStr.isBlank()) return false
    return try {
        val parts = dateStr.split("-")
        if (parts.size != 3) false
        else {
            val year = parts[0].toIntOrNull() ?: return false
            val month = parts[1].toIntOrNull() ?: return false
            val day = parts[2].toIntOrNull() ?: return false
            year in 1900..2025 && month in 1..12 && day in 1..31
        }
    } catch (_: Exception) {
        false
    }
}

/* ---------- Data Preparation for API ---------- */

@Serializable
data class PersonData(
    val firstName: String,
    val lastName: String,
    val birthDate: String,
    val allergies: Array<String>,
    val disabilities: Array<String>,
    val medicines: Array<String>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PersonData

        if (firstName != other.firstName) return false
        if (lastName != other.lastName) return false
        if (birthDate != other.birthDate) return false
        if (!allergies.contentEquals(other.allergies)) return false
        if (!disabilities.contentEquals(other.disabilities)) return false
        if (!medicines.contentEquals(other.medicines)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = firstName.hashCode()
        result = 31 * result + lastName.hashCode()
        result = 31 * result + birthDate.hashCode()
        result = 31 * result + allergies.contentHashCode()
        result = 31 * result + disabilities.contentHashCode()
        result = 31 * result + medicines.contentHashCode()
        return result
    }
}

private fun preparePersonsData(
    firstNames: MutableList<MutableState<String>>,
    lastNames: MutableList<MutableState<String>>,
    birthDates: MutableList<MutableState<String>>,
    allergiesList: MutableList<MutableState<List<String>>>,
    allergiesNone: MutableList<MutableState<Boolean>>,
    disabilitiesList: MutableList<MutableState<List<String>>>,
    disabilitiesNone: MutableList<MutableState<Boolean>>,
    medsList: MutableList<MutableState<List<String>>>,
    medsNone: MutableList<MutableState<Boolean>>
): List<PersonData> {
    return firstNames.indices.map { index ->
        PersonData(
            firstName = firstNames[index].value,
            lastName = lastNames[index].value,
            birthDate = birthDates[index].value,
            allergies = if (allergiesNone[index].value) {
                emptyArray()
            } else {
                allergiesList[index].value
                    .filter { it.isNotBlank() }
                    .toTypedArray()
            },
            disabilities = if (disabilitiesNone[index].value) {
                emptyArray()
            } else {
                disabilitiesList[index].value
                    .filter { it.isNotBlank() }
                    .toTypedArray()
            },
            medicines = if (medsNone[index].value) {
                emptyArray()
            } else {
                medsList[index].value
                    .filter { it.isNotBlank() }
                    .toTypedArray()
            }
        )
    }
}
