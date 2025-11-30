package org.cowary.ui.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

open class AirScreen {
    // TODO: Доработать параметры. Чтобы принимал String, а не TextFieldValue
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun StatusDropdown(
        status: String,
        onStatusChange: (TextFieldValue) -> Unit,
        modifier: Modifier = Modifier
    ) {
        var expanded by remember { mutableStateOf(false) }
        val statusOptions = listOf("Finished", "Dropped", "Planned", "Ongoing")

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = status,
                onValueChange = {}, // Не используем прямой ввод
                label = { Text("Статус") },
                modifier = modifier
                    .fillMaxWidth()
                    .menuAnchor(),     // TODO: Требуется изменить
                readOnly = true, // Запрещаем ручной ввод
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                statusOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onStatusChange(TextFieldValue(option))
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }
    }

    // TODO: Доработать параметры. Чтобы принимал String, а не TextFieldValue
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DateSelectionField(
        label: String,
        selectedDate: TextFieldValue,
        onDateSelected: (TextFieldValue) -> Unit
    ) {
        var showDatePicker by remember { mutableStateOf(false) }

        val localDate = remember(selectedDate.text) {
            if (selectedDate.text.isNotEmpty()) {
                try {
                    LocalDate.parse(selectedDate.text)
                } catch (e: Exception) {
                    LocalDate.now()
                }
            } else {
                LocalDate.now()
            }
        }

        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = selectedDate,
                onValueChange = {}, // Не редактируем вручную
                label = { Text(label) },
                modifier = Modifier.weight(1f),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Выбрать дату"
                        )
                    }
                }
            )
        }

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val newDate = Instant.ofEpochMilli(millis)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                onDateSelected(TextFieldValue(newDate))
                                showDatePicker = false
                            }
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Отмена")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }

    @Composable
    fun IntegerOnlyTextField(
        label: String,
        value: String,
        onValueChange: (TextFieldValue) -> Unit,
        modifier: Modifier = Modifier,
        maxValue: Int = 100
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                // Разрешаем только цифры и пустую строку
                if (newValue.isEmpty() || (newValue.all { it.isDigit() } && (newValue.toIntOrNull() ?: 0) <= maxValue)) {
                    onValueChange(TextFieldValue(newValue))
                }
            },
            label = { Text(label) },
            modifier = modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            singleLine = true
        )
    }
}