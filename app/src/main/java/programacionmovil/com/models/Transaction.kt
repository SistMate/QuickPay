package programacionmovil.com.models

data class Transaction(
    val amount: Double,
    val date: String,
    val description: String
)

    data class TransactionC(
        val title: String,
        val paymentMethod: String,
        val trufi: String,
        val amount: String
    )