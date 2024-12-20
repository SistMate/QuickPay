package programacionmovil.com

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import programacionmovil.com.adapters.TransactionConductorAdapter
import programacionmovil.com.conductor.SettingsCActivity
import programacionmovil.com.models.TransactionC
import programacionmovil.com.utils.RealtimeManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TransactionActivity : AppCompatActivity() {
    // Firebase Firestore instance
    private val db = FirebaseFirestore.getInstance()
    private val realtimeManager = RealtimeManager()

    // UI Components
    private lateinit var imageViewHome: ImageView
    private lateinit var imageViewPagar: ImageView
    private lateinit var imageViewTransaction: ImageView
    private lateinit var imageViewSettings: ImageView
    private lateinit var textStartDate: TextView
    private lateinit var textEndDate: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var transactionAdapter: TransactionConductorAdapter

    // User-specific variables
    private val usuarioIdEspecifico = "Mateo Villagomez"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction)

        // Initialize UI components
        setupUI()
        setupRecyclerView()
        setupDatePickers()
        setupBottomNavigation()

        // Set button click listeners
//        getButton.setOnClickListener { fetchFirestoreTransactions() }
//
//        // Fetch real-time transactions (if using a RealtimeManager)
//        fetchTransactions()
    }

    private fun setupUI() {
        recyclerView = findViewById(R.id.reciclertransaction)
        textStartDate = findViewById(R.id.textStartDate)
        textEndDate = findViewById(R.id.textEndDate)
    }


    val transactionList = listOf(
        TransactionC("CB - 15 de octubre", "Pago en QR", "Taxi: TXQR123", "Bs. 7.50"),
        TransactionC("CB - 03 de noviembre", "Pago en NFC", "Taxi: CAF567", "Bs. 10.00"),
        TransactionC("CB - 08 de diciembre", "Pago en QR", "Taxi: SPRM890", "Bs. 45.30"),
        TransactionC("CB - 20 de enero", "Pago en NFC", "Taxi: FRMC123", "Bs. 12.75"),
        TransactionC("CB - 15 de octubre", "Pago en QR", "Taxi: TXQR123", "Bs. 7.50"),
        TransactionC("CB - 03 de noviembre", "Pago en NFC", "Taxi: CAF567", "Bs. 10.00"),
        TransactionC("CB - 08 de diciembre", "Pago en QR", "Taxi: SPRM890", "Bs. 45.30"),
        TransactionC("CB - 20 de enero", "Pago en NFC", "Taxi: FRMC123", "Bs. 12.75"),TransactionC("CB - 15 de octubre", "Pago en QR", "Taxi: TXQR123", "Bs. 7.50"),
        TransactionC("CB - 03 de noviembre", "Pago en NFC", "Taxi: CAF567", "Bs. 10.00"),
        TransactionC("CB - 08 de diciembre", "Pago en QR", "Taxi: SPRM890", "Bs. 45.30"),
        TransactionC("CB - 20 de enero", "Pago en NFC", "Taxi: FRMC123", "Bs. 12.75")

        )

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        transactionAdapter = TransactionConductorAdapter(emptyList())
//        recyclerView.adapter = transactionAdapter
        recyclerView.adapter = TransactionConductorAdapter(transactionList)

    }




    private fun setupDatePickers() {
        val btnStartDate: LinearLayoutCompat = findViewById(R.id.btnStartDate)
        val btnEndDate: LinearLayoutCompat = findViewById(R.id.btnEndDate)

        btnStartDate.setOnClickListener {
            showDatePickerDialog { date -> textStartDate.text = date }
        }

        btnEndDate.setOnClickListener {
            showDatePickerDialog { date -> textEndDate.text = date }
        }
    }

    private fun setupBottomNavigation() {
        imageViewHome = findViewById(R.id.imageView2)
        imageViewPagar = findViewById(R.id.imageViewPagar)
        imageViewTransaction = findViewById(R.id.imageViewTransaction)
        imageViewSettings = findViewById(R.id.imageViewSettings)

        imageViewHome.setOnClickListener {
            startActivity(Intent(this, HomePageA::class.java))
        }

        imageViewPagar.setOnClickListener {
            startActivity(Intent(this, CobrarActivity::class.java))
        }

        imageViewTransaction.setOnClickListener {
            startActivity(Intent(this, TransactionActivity::class.java))
        }

        imageViewSettings.setOnClickListener {
            startActivity(Intent(this, SettingsCActivity::class.java))
        }
    }

//    private fun fetchFirestoreTransactions() {
//        db.collection("HistorialConductor")
//            .whereEqualTo("NombreUsuario", usuarioIdEspecifico)
//            .get()
//            .addOnSuccessListener { documents ->
//                val transactionList = mutableListOf<TransactionC>()
//                for (document in documents) {
//                    val date = document.getString("Fecha") ?: "Sin fecha"
//                    val description = document.getString("Descripcion") ?: "Sin descripción"
//                    val details = document.getString("Detalles") ?: "Sin detalles"
//                    val monto = document.getString("Monto") ?: "Bs. 0.00"
//
//                    transactionList.add(TransactionC(date, description, details, monto))
//                }
//                transactionAdapter.updateData(transactionList)
//            }
//            .addOnFailureListener { exception ->
//                // Manejo de error
//                exception.printStackTrace()
//            }
//    }
//
//    private fun fetchTransactions() {
//        lifecycleScope.launch {
//            realtimeManager.getTransactionsForCurrentUser().collect { transactions ->
//                val transactionCList = transactions.map { transaction ->
//                    TransactionC(
//                        title = transaction.date,
//                        description = transaction.description,
//                        details = "Monto: Bs. ${transaction.amount}",
//                        monto = "Bs. ${transaction.amount}"
//                    )
//                }
//                transactionAdapter.updateData(transactionCList)
//            }
//        }
//    }



    private fun showDatePickerDialog(onDateSet: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = Calendar.getInstance().apply {
                set(selectedYear, selectedMonth, selectedDay)
            }
            val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate.time)
            onDateSet(formattedDate)
        }, year, month, day)

        datePickerDialog.show()
    }
}
