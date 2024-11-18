package programacionmovil.com

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.firestore.FirebaseFirestore
//import java.text.SimpleDateFormat
//import java.util.*

class HomePageA : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference
    private lateinit var imageViewHome: ImageView
    private lateinit var imageViewPagar: ImageView
    private lateinit var imageViewTransaction: ImageView
    private lateinit var imageViewSettings: ImageView
    private lateinit var textStartDate: TextView
    private lateinit var textEndDate: TextView
    private lateinit var getButton: Button
    private lateinit var creditTotal: TextView

    //private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page_a)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.homeconductor)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializar el botón getButton
        getButton = findViewById(R.id.getButton)

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference

        // Inicializar vistas

        creditTotal = findViewById(R.id.creditTotal)
        textStartDate = findViewById(R.id.textStartDate)
        textEndDate = findViewById(R.id.textEndDate)

        val btnStartDate: LinearLayoutCompat = findViewById(R.id.btnStartDate)
        val btnEndDate: LinearLayoutCompat = findViewById(R.id.btnEndDate)

        // Listener para getButton
        getButton.setOnClickListener {
//            db.collection("HistorialConductor").document().get().addOnSuccessListener {
//                transactionConductor.setText(it.get("NombreUsuario") as String?)
//
//            }

//            db.collection("HistorialConductor").get().addOnSuccessListener { documents ->
//                val nombresUsuarios = StringBuilder()  // Usamos StringBuilder para concatenar eficientemente
//
//                for (document in documents) {
//                    val nombreUsuario = document.getString("Monto")
//                    if (nombreUsuario != null) {
//                        nombresUsuarios.append(nombreUsuario).append("\n")  // Agrega cada nombre con un salto de línea
//                    }
//                }
//
//                transactionConductor.text = nombresUsuarios.toString()  // Establece el texto en el TextView
//            }

            //Ejemplo para subir datos

//            db.collection("HistorialConductor").document(idtansaccion).set(
//                hashMapOf(
//                    "provider" to provider,
//                    "adress" to addess.text.toString(),
//                    "phone" to phonetext.text.toString()
//                )
//            )


            //Para eliminar
//            db.collection("HistorialConductor").document().delete()
        //  db.collection("HistorialConductor").get().addOnSuccessListener { documents ->
               // val montos = StringBuilder()  // Usamos StringBuilder para concatenar eficientemente

              //  for (document in documents) {
                //    val monto = document.getDouble("Monto") // Cambia el nombre del campo según corresponda
                 //   if (monto != null) {
                //       montos.append("$monto\n")  // Agrega cada monto con un salto de línea y etiqueta
                //    }
               // }

              //  creditTotal.text = montos.toString()  // Establece el texto en el TextView
            }


        }

        // Verificar si el usuario está logueado
       // if (mAuth.currentUser == null) {
           // val intent = Intent(this, MainActivity::class.java).apply {
              //  flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
          //  }
          //  startActivity(intent)
          //  return
     //   }

        // Callback para manejar el botón "Atrás"
        //onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            //override fun handleOnBackPressed() {
               // moveTaskToBack(true) // Evita que el usuario cierre la app con el botón atrás
          //  }
       // })

        // Configurar los listeners de los botones de fechas
       // btnStartDate.setOnClickListener {
           // showDatePickerDialog { date ->
            //    textStartDate.text = date
          //  }
      //  }

       // btnEndDate.setOnClickListener {
            //showDatePickerDialog { date ->
              //  textEndDate.text = date
           // }
      //  }

        // Inicializar los ImageViews
       // imageViewHome = findViewById(R.id.imageView2)
       // imageViewPagar = findViewById(R.id.imageViewPagar)
       // imageViewTransaction = findViewById(R.id.imageViewTransaction)
       // imageViewSettings = findViewById(R.id.imageViewSettings)

        // Configurar listeners de los ImageViews
       // imageViewHome.setOnClickListener {
          //  startActivity(Intent(this, HomePageA::class.java))
       // }

        //imageViewPagar.setOnClickListener {
          //  startActivity(Intent(this, CobrarActivity::class.java))
       // }

        //imageViewTransaction.setOnClickListener {
           // startActivity(Intent(this, TransactionActivity::class.java))
      //  }

        //imageViewSettings.setOnClickListener {
          //  startActivity(Intent(this, CobrarActivity::class.java))
       // }

        // Ajustar los Insets de la ventana si es necesario
        //findViewById<LinearLayoutCompat>(R.id.main)?.let { mainLayout ->
          //  ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { v, insets ->
           //     val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
          //      v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
          //      insets
          //  }
      //  }
    //}

    // Método para mostrar el selector de fechas
   // private fun showDatePickerDialog(onDateSet: (String) -> Unit) {
       // val calendar = Calendar.getInstance()
       // val year = calendar.get(Calendar.YEAR)
       // val month = calendar.get(Calendar.MONTH)
        //val day = calendar.get(Calendar.DAY_OF_MONTH)

      //  val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
          //  val selectedDate = Calendar.getInstance().apply {
          //      set(selectedYear, selectedMonth, selectedDay)
          //  }
          //  val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate.time)
          //  onDateSet(formattedDate)
       // }, year, month, day)

      //  datePickerDialog.show()
   // }
}
