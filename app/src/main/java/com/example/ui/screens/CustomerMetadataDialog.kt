package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.LocalLedgerColors
import com.example.util.Formatters

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerMetadataDialog(
  initialName: String,
  initialPhone: String,
  initialInvoiceNumber: String,
  initialDateMillis: Long,
  initialNote: String,
  initialCurrency: String,
  initialShowProductName: Boolean = true,
  initialClaimedTotalStr: String = "",
  initialDeductionAmountStr: String = "",
  onDismiss: () -> Unit,
  onSave: (
    name: String,
    phone: String,
    invoice: String,
    dateMillis: Long,
    note: String,
    currency: String,
    showProductName: Boolean,
    claimedTotalStr: String,
    deductionAmountStr: String
  ) -> Unit
) {
  val ledgerColors = LocalLedgerColors.current

  var sellerName by remember { mutableStateOf(initialName) }
  var phone by remember { mutableStateOf(initialPhone) }
  var invoiceNumber by remember { mutableStateOf(initialInvoiceNumber) }
  var dateMillis by remember { mutableStateOf(initialDateMillis) }
  var note by remember { mutableStateOf(initialNote) }
  var currency by remember { mutableStateOf(initialCurrency) }
  var showProductName by remember { mutableStateOf(initialShowProductName) }
  var claimedTotalStr by remember { mutableStateOf(initialClaimedTotalStr) }
  var deductionAmountStr by remember { mutableStateOf(initialDeductionAmountStr) }
  var showDatePicker by remember { mutableStateOf(false) }

  val currencies = listOf("₹", "$", "€", "£", "AED", "৳")

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(16.dp),
      color = ledgerColors.ledgerPaperSurface,
      tonalElevation = 6.dp,
      modifier = Modifier
        .fillMaxWidth()
        .border(1.dp, ledgerColors.ruledLine, RoundedCornerShape(16.dp))
        .testTag("customer_metadata_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
          .verticalScroll(rememberScrollState())
      ) {
        // Dialog Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Paper Bill & Seller Details",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = ledgerColors.inkNavy
            )
            Text(
              text = "Audit handwritten parchi & adjustments",
              style = MaterialTheme.typography.bodySmall,
              color = ledgerColors.mutedCharcoal
            )
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("close_metadata_dialog")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = ledgerColors.mutedCharcoal
            )
          }
        }

        HorizontalDivider(
          modifier = Modifier.padding(vertical = 12.dp),
          color = ledgerColors.ruledLine
        )

        // Seller / Shop Name
        OutlinedTextField(
          value = sellerName,
          onValueChange = { sellerName = it },
          label = { Text("Seller / Shop / Wholesaler Name") },
          placeholder = { Text("e.g. Ramesh Kirana / Sharma Wholesale", color = ledgerColors.placeholderColor) },
          leadingIcon = {
            Icon(Icons.Default.Store, contentDescription = null, tint = ledgerColors.inkNavy)
          },
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = ledgerColors.charcoal,
            unfocusedTextColor = ledgerColors.charcoal,
            focusedBorderColor = ledgerColors.stampAmber,
            unfocusedBorderColor = ledgerColors.ruledLineStrong,
            focusedLabelColor = ledgerColors.stampAmber,
            unfocusedLabelColor = ledgerColors.mutedCharcoal
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_customer_name")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Seller's Claimed Bill Total on Paper (Audit Target)
        OutlinedTextField(
          value = claimedTotalStr,
          onValueChange = { claimedTotalStr = it },
          label = { Text("Seller's Written Paper Total ($currency)") },
          placeholder = { Text("e.g. 55000 (Amount written by seller)", color = ledgerColors.placeholderColor) },
          leadingIcon = {
            Icon(Icons.Default.Calculate, contentDescription = null, tint = ledgerColors.ledgerGreen)
          },
          singleLine = true,
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = ledgerColors.charcoal,
            unfocusedTextColor = ledgerColors.charcoal,
            focusedBorderColor = ledgerColors.ledgerGreen,
            unfocusedBorderColor = ledgerColors.ruledLineStrong,
            focusedLabelColor = ledgerColors.ledgerGreen,
            unfocusedLabelColor = ledgerColors.mutedCharcoal
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_claimed_total")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Advance Paid / Discount / Deduction Subtraction
        OutlinedTextField(
          value = deductionAmountStr,
          onValueChange = { deductionAmountStr = it },
          label = { Text("Advance Paid / Discount / Old Adjustment ($currency)") },
          placeholder = { Text("e.g. 5000 (Subtracted from final bill)", color = ledgerColors.placeholderColor) },
          leadingIcon = {
            Icon(Icons.Default.RemoveCircleOutline, contentDescription = null, tint = ledgerColors.softRed)
          },
          singleLine = true,
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = ledgerColors.charcoal,
            unfocusedTextColor = ledgerColors.charcoal,
            focusedBorderColor = ledgerColors.softRed,
            unfocusedBorderColor = ledgerColors.ruledLineStrong,
            focusedLabelColor = ledgerColors.softRed,
            unfocusedLabelColor = ledgerColors.mutedCharcoal
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_deduction_amount")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Invoice Number & Date Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = invoiceNumber,
            onValueChange = { invoiceNumber = it },
            label = { Text("Bill / Parchi #") },
            leadingIcon = {
              Icon(Icons.Default.Receipt, contentDescription = null, tint = ledgerColors.inkNavy)
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = ledgerColors.charcoal,
              unfocusedTextColor = ledgerColors.charcoal,
              focusedBorderColor = ledgerColors.stampAmber,
              unfocusedBorderColor = ledgerColors.ruledLineStrong,
              focusedLabelColor = ledgerColors.stampAmber,
              unfocusedLabelColor = ledgerColors.mutedCharcoal
            ),
            modifier = Modifier
              .weight(1.2f)
              .testTag("input_invoice_number")
          )

          OutlinedTextField(
            value = Formatters.formatDate(dateMillis),
            onValueChange = {},
            readOnly = true,
            label = { Text("Bill Date") },
            leadingIcon = {
              Icon(
                Icons.Default.CalendarToday,
                contentDescription = null,
                tint = ledgerColors.inkNavy,
                modifier = Modifier
                  .size(18.dp)
                  .clickable { showDatePicker = true }
              )
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = ledgerColors.charcoal,
              unfocusedTextColor = ledgerColors.charcoal,
              focusedBorderColor = ledgerColors.stampAmber,
              unfocusedBorderColor = ledgerColors.ruledLineStrong,
              focusedLabelColor = ledgerColors.stampAmber,
              unfocusedLabelColor = ledgerColors.mutedCharcoal
            ),
            modifier = Modifier
              .weight(1f)
              .clickable { showDatePicker = true }
              .testTag("input_bill_date")
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Seller Phone Number
        OutlinedTextField(
          value = phone,
          onValueChange = { phone = it },
          label = { Text("Seller / Shop Phone (Optional)") },
          placeholder = { Text("e.g. 9876543210", color = ledgerColors.placeholderColor) },
          leadingIcon = {
            Icon(Icons.Default.Phone, contentDescription = null, tint = ledgerColors.inkNavy)
          },
          singleLine = true,
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = ledgerColors.charcoal,
            unfocusedTextColor = ledgerColors.charcoal,
            focusedBorderColor = ledgerColors.stampAmber,
            unfocusedBorderColor = ledgerColors.ruledLineStrong,
            focusedLabelColor = ledgerColors.stampAmber,
            unfocusedLabelColor = ledgerColors.mutedCharcoal
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_customer_phone")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Note / Purpose Field
        OutlinedTextField(
          value = note,
          onValueChange = { note = it },
          label = { Text("Purchase Memo / Notes (Optional)") },
          placeholder = { Text("e.g. Wedding bulk purchase / Grocery stock", color = ledgerColors.placeholderColor) },
          leadingIcon = {
            Icon(Icons.Default.Notes, contentDescription = null, tint = ledgerColors.inkNavy)
          },
          maxLines = 2,
          colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = ledgerColors.charcoal,
            unfocusedTextColor = ledgerColors.charcoal,
            focusedBorderColor = ledgerColors.stampAmber,
            unfocusedBorderColor = ledgerColors.ruledLineStrong,
            focusedLabelColor = ledgerColors.stampAmber,
            unfocusedLabelColor = ledgerColors.mutedCharcoal
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_bill_note")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Mode Card: Product Name vs Fast Calculation
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = ledgerColors.ledgerPaperShaded,
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, ledgerColors.ruledLineStrong, RoundedCornerShape(8.dp))
            .clickable { showProductName = !showProductName }
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
              Text(
                text = "Show Item Names",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = ledgerColors.inkNavy
              )
              Text(
                text = if (showProductName) "Enter Item Name + Qty + Rate" else "Fast Mode: Qty × Rate only",
                fontSize = 11.5.sp,
                color = ledgerColors.mutedCharcoal
              )
            }

            androidx.compose.material3.Switch(
              checked = showProductName,
              onCheckedChange = { showProductName = it },
              modifier = Modifier.testTag("toggle_product_name_switch")
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Currency Selector
        Text(
          text = "Currency Symbol",
          style = MaterialTheme.typography.labelSmall,
          color = ledgerColors.inkNavy,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          currencies.forEach { curr ->
            FilterChip(
              selected = currency == curr,
              onClick = { currency = curr },
              label = { Text(curr, fontWeight = FontWeight.Bold) },
              modifier = Modifier.testTag("chip_currency_$curr")
            )
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Actions
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End,
          verticalAlignment = Alignment.CenterVertically
        ) {
          OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("cancel_metadata_button")
          ) {
            Text("Cancel", color = ledgerColors.charcoal)
          }

          Spacer(modifier = Modifier.width(10.dp))

          Button(
            onClick = {
              onSave(sellerName, phone, invoiceNumber, dateMillis, note, currency, showProductName, claimedTotalStr, deductionAmountStr)
              onDismiss()
            },
            colors = ButtonDefaults.buttonColors(
              containerColor = ledgerColors.stampAmber,
              contentColor = Color.White
            ),
            modifier = Modifier.testTag("save_metadata_button")
          ) {
            Text("Apply Details", fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }

  // Material Date Picker Dialog
  if (showDatePicker) {
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dateMillis)
    DatePickerDialog(
      onDismissRequest = { showDatePicker = false },
      confirmButton = {
        TextButton(
          onClick = {
            datePickerState.selectedDateMillis?.let { dateMillis = it }
            showDatePicker = false
          }
        ) {
          Text("OK")
        }
      },
      dismissButton = {
        TextButton(onClick = { showDatePicker = false }) {
          Text("Cancel")
        }
      }
    ) {
      DatePicker(state = datePickerState)
    }
  }
}
