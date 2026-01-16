package com.example.diba_print

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.diba_print.models.PrintOrder
import com.example.diba_print.models.Receipt
import kotlin.math.min

class MainActivity : AppCompatActivity() {

    private lateinit var spinnerType: Spinner
    private lateinit var spinnerSize: Spinner
    private lateinit var quantityPicker: NumberPicker
    private lateinit var phoneInput: EditText
    private lateinit var discountInput: EditText
    private lateinit var deliverySwitch: Switch
    private lateinit var placeOrderBtn: Button

    private val photoSizes = mapOf("4x6" to 6.99, "6x8" to 8.99, "8x12" to 10.99)
    private val canvasSizes = mapOf("12x16" to 14.99, "16x20" to 18.99, "18x24" to 22.99)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        spinnerType = findViewById(R.id.spinnerType)
        spinnerSize = findViewById(R.id.spinnerSize)
        quantityPicker = findViewById(R.id.quantityPicker)
        phoneInput = findViewById(R.id.phoneInput)
        discountInput = findViewById(R.id.discountInput)
        deliverySwitch = findViewById(R.id.deliverySwitch)
        placeOrderBtn = findViewById(R.id.placeOrderBtn)

        setupSpinners()
        setupQuantityPicker()

        placeOrderBtn.setOnClickListener { handleOrder() }
    }

    private fun setupSpinners() {
        val types = listOf("Photo", "Canvas")
        val typeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, types)
        spinnerType.adapter = typeAdapter

        spinnerType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                val sizes = if (types[position] == "Photo") photoSizes.keys.toList() else canvasSizes.keys.toList()
                val sizeAdapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_dropdown_item, sizes)
                spinnerSize.adapter = sizeAdapter

                quantityPicker.minValue = if (types[position] == "Photo") 1 else 3
                quantityPicker.maxValue = 10
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupQuantityPicker() {
        quantityPicker.minValue = 1
        quantityPicker.maxValue = 10
    }

    private fun handleOrder() {
        val type = spinnerType.selectedItem.toString()
        val size = spinnerSize.selectedItem.toString()
        val quantity = quantityPicker.value
        val phone = phoneInput.text.toString().trim()
        val discountCode = discountInput.text.toString().trim()
        val delivery = deliverySwitch.isChecked

        // Validate phone number (must be exactly 10 digits)
        if (phone.isEmpty() || phone.length != 10 || !phone.all { it.isDigit() }) {
            phoneInput.error = "Enter valid 10-digit phone number"
            return
        }

        // Validate discount code
        if (discountCode.isNotEmpty()) {
            if (!discountCode.startsWith("PRINT")) {
                discountInput.error = "Invalid discount code"
                discountInput.setText("")  // Clear the invalid code
                return  // Stop processing the order
            }
        }

        val order = PrintOrder(type, size, quantity, phone, discountCode, delivery)
        val receipt = calculateReceipt(order)

        AlertDialog.Builder(this)
            .setTitle("Order Confirmation")
            .setMessage(receipt.generateSummary(order))
            .setPositiveButton("OK", null)
            .show()
    }

    private fun calculateReceipt(order: PrintOrder): Receipt {
        val priceMap = if (order.type == "Photo") photoSizes else canvasSizes
        val basePrice = priceMap[order.size] ?: 0.0
        var subtotal = basePrice * order.quantity

        var discount = 0.0
        order.discountCode?.let {
            if (it.startsWith("PRINT")) discount = min(subtotal * 0.30, 15.0)
        }

        val afterDiscount = subtotal - discount
        val tax = afterDiscount * 0.13
        val deliveryFee = if (order.delivery) 5.99 else 0.0
        val total = afterDiscount + tax + deliveryFee

        return Receipt(subtotal, discount, tax, deliveryFee, total)
    }
}
