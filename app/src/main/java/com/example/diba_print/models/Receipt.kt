package com.example.diba_print.models

data class Receipt(
    val subtotal: Double,
    val discount: Double,
    val tax: Double,
    val deliveryFee: Double,
    val total: Double
) {
    fun generateSummary(order: PrintOrder): String {
        return """
            Order Summary
            --------------------------
            Print Type: ${order.type}
            Size: ${order.size}
            Quantity: ${order.quantity}
            Subtotal: $${"%.2f".format(subtotal)}
            Discount: $${"%.2f".format(discount)}
            Tax (13%): $${"%.2f".format(tax)}
            Delivery: $${"%.2f".format(deliveryFee)}
            
            Final Total: $${"%.2f".format(total)}
        """.trimIndent()
    }
}
