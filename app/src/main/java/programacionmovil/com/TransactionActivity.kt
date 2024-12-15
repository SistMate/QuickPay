package programacionmovil.com

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import programacionmovil.com.adapters.TransactionConductorAdapter
import programacionmovil.com.conductor.SettingsCActivity
import programacionmovil.com.models.TransactionC
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class TransactionActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var imageViewHome: ImageView
    private lateinit var imageViewPagar: ImageView
    private lateinit var imageViewTransaction: ImageView
    private lateinit var imageViewSettings: ImageView
    private lateinit var textStartDate: TextView
    private lateinit var textEndDate: TextView
    private lateinit var getButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_transaction)

        // Configuración del RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.reciclertransaction)

        // Crear una lista de datos de ejemplo
        val transactionList = listOf(
            TransactionC("CB - 27 deasfds septiembre", "Pago en NFC", "Trufi: TDSF450", "Bs. 4.35"),
            TransactionC("CB -sdse septiembre", "Pago en NFC", "Trufi: TDSF451", "Bs. 5.00"),

            )

        // Configurar el RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = TransactionConductorAdapter(transactionList)

        getButton = findViewById(R.id.getButton)
        textStartDate = findViewById(R.id.textStartDate)
        textEndDate = findViewById(R.id.textEndDate)

        val btnStartDate: LinearLayoutCompat = findViewById(R.id.btnStartDate)
        val btnEndDate: LinearLayoutCompat = findViewById(R.id.btnEndDate)

        btnStartDate.setOnClickListener {
            showDatePickerDialog { date ->
                textStartDate.text = date
            }
        }

        btnEndDate.setOnClickListener {
            showDatePickerDialog { date ->
                textEndDate.text = date
            }
        }




        // Configurar las imágenes del bottom navigation
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
        val usuarioIdEspecifico = "Mateo Villagomez"


        getButton.setOnClickListener {
            db.collection("HistorialConductor")
                .whereEqualTo("NombreUsuario", usuarioIdEspecifico)
                .get().addOnSuccessListener { documents ->
                    val nombresUsuarios = StringBuilder()

                    for (document in documents) {
                        val nombreUsuario = document.getString("Monto")
                        if (nombreUsuario != null) {
                            nombresUsuarios.append(nombreUsuario).append("\n")
                        }
                    }

//                    creditTotal.text = nombresUsuarios.toString()

                }
        }
    }


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