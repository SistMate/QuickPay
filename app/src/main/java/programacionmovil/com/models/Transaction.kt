package programacionmovil.com.models

data class Transaction(
    val key: String? = null,
    val description: String,
    val date: String,
    val amount: Double,
)

    data class TransactionC(
        val title: String,
        val description: String,
        val details: String,
        val monto: String
    )