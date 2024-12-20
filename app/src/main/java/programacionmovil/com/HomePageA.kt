package programacionmovil.com

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import programacionmovil.com.adapters.TransactionConductorAdapter
import programacionmovil.com.conductor.SettingsCActivity
import androidx.core.view.ViewCompat
import programacionmovil.com.utils.RealtimeManager
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import programacionmovil.com.models.TransactionC
//import programacionmovil.com.conductor.SettingsCActivity
import java.text.SimpleDateFormat
import java.util.*

class HomePageA : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private val realtimeManager = RealtimeManager()

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference
    private lateinit var imageViewHome: ImageView
    private lateinit var imageViewPagar: ImageView
    private lateinit var imageViewTransaction: ImageView
    private lateinit var imageViewSettings: ImageView
    private lateinit var textStartDate: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var textEndDate: TextView
    private lateinit var getButton: Button
    private lateinit var creditTotal: TextView
    private lateinit var transactionAdapter: TransactionConductorAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page_a)

        setupUI()
        setupRecyclerView()
        setupDatePickers()


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.homeconductor)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }





        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference

        val currentUser = mAuth.currentUser

        creditTotal = findViewById(R.id.creditTotal)
        textStartDate = findViewById(R.id.textStartDate)
        textEndDate = findViewById(R.id.textEndDate)

        val btnStartDate: LinearLayoutCompat = findViewById(R.id.btnStartDate)
        val btnEndDate: LinearLayoutCompat = findViewById(R.id.btnEndDate)




        if (currentUser != null) {
            val userId = currentUser.uid // ID del usuario logueado

            // Accede al nodo correspondiente en Firebase Realtime Database
            mDatabase.child("Users").child(userId).get().addOnSuccessListener { dataSnapshot ->
                if (dataSnapshot.exists()) {
                    // Obtén el valor del "name"
                    val userName = dataSnapshot.child("name").value.toString()
                    val userIdInfo = dataSnapshot.child("userIdInfo").value.toString()

                    val formattedName = getString(R.string.nameuser, userName, userIdInfo)

                    // Actualiza el TextView para mostrar el nombre del usuario
                    val usuarioIdEspecifico: TextView = findViewById(R.id.usuarioIdEspecifico)
                    usuarioIdEspecifico.text = userName
                    val monto = dataSnapshot.child("monto").value.toString()
                    creditTotal.text = monto
                } else {
                    // Maneja el caso en el que no se encuentran los datos del usuario
                    val usuarioIdEspecifico: TextView = findViewById(R.id.usuarioIdEspecifico)
                    usuarioIdEspecifico.text = "Usuario no encontrado"
                    creditTotal.text = "0"
                }
            }.addOnFailureListener {
                // Manejo de errores
                val usuarioIdEspecifico: TextView = findViewById(R.id.usuarioIdEspecifico)
                usuarioIdEspecifico.text = "Error al obtener datos"
                creditTotal.text = "Error"
            }
        } else {
            // Manejo para usuarios no logueados
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }


//         Verificar si el usuario está logueado
        if (mAuth.currentUser == null) {
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
            return
        }

//         Callback para manejar el botón "Atrás"
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                moveTaskToBack(true) // Evita que el usuario cierre la app con el botón atrás
            }
        })

//         Configurar los listeners de los botones de fechas
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

//         Inicializar los ImageViews
        imageViewHome = findViewById(R.id.imageView2)
        imageViewPagar = findViewById(R.id.imageViewPagar)
        imageViewTransaction = findViewById(R.id.imageViewTransaction)
        imageViewSettings = findViewById(R.id.imageViewSettings)

//         Configurar listeners de los ImageViews
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
            startActivity(Intent(this, ajustes_pasajero::class.java))
        }

//         Ajustar los Insets de la ventana si es necesario
        findViewById<LinearLayoutCompat>(R.id.main)?.let { mainLayout ->
            ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }
    }

    val transactionList = listOf(
        TransactionC("CB - 15 de octubre", "Pago en QR", "Taxi: TXQR123", "Bs. 7.50"),
        TransactionC("CB - 03 de noviembre", "Pago en NFC", "Taxi: CAF567", "Bs. 10.00"),
        TransactionC("CB - 08 de diciembre", "Pago en QR", "Taxi: SPRM890", "Bs. 45.30"),
        TransactionC("CB - 20 de enero", "Pago en NFC", "Taxi: FRMC123", "Bs. 12.75"),
        TransactionC("CB - 15 de octubre", "Pago en QR", "Taxi: TXQR123", "Bs. 7.50"),
        TransactionC("CB - 03 de noviembre", "Pago en NFC", "Taxi: CAF567", "Bs. 10.00"),
        TransactionC("CB - 08 de diciembre", "Pago en QR", "Taxi: SPRM890", "Bs. 45.30"),
        TransactionC("CB - 20 de enero", "Pago en NFC", "Taxi: FRMC123", "Bs. 12.75"),
        TransactionC("CB - 15 de octubre", "Pago en QR", "Taxi: TXQR123", "Bs. 7.50"),
        TransactionC("CB - 03 de noviembre", "Pago en NFC", "Taxi: CAF567", "Bs. 10.00"),
        TransactionC("CB - 08 de diciembre", "Pago en QR", "Taxi: SPRM890", "Bs. 45.30"),
        TransactionC("CB - 20 de enero", "Pago en NFC", "Taxi: FRMC123", "Bs. 12.75")

    )


    private fun setupUI() {
        recyclerView = findViewById(R.id.reciclertransaction)
        textStartDate = findViewById(R.id.textStartDate)
        textEndDate = findViewById(R.id.textEndDate)
    }

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



//     Método para mostrar el selector de fechas
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
