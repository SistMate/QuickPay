package programacionmovil.com

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class HomePageP : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference
    private lateinit var tvMonto: TextView
    private lateinit var tvPuntos: TextView

    private lateinit var tvHeader: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_page_p)

        // Configuración para ajustar la visualización con el borde de la pantalla
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializa el TextView que mostrará el nombre del usuario
        tvHeader = findViewById(R.id.tvHeader)
        tvMonto = findViewById(R.id.tvMonto) // Asocia el TextView de monto
        tvPuntos = findViewById(R.id.tvPuntos)

        // Llamar a la función para obtener el nombre del usuario desde Firebase
        getUserName()
        getUserData()

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference

// Verifica si hay una sesión activa
        if (mAuth.currentUser == null) {
            // Si no hay sesión activa, redirigir a MainActivity
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

        val tvGoHome = findViewById<ImageView>(R.id.imageHome)
        tvGoHome.setOnClickListener{
            goToHome()
        }
        val tvGoAjustes = findViewById<ImageView>(R.id.imageViewSettings)
        tvGoAjustes.setOnClickListener{
            goToAjustes()
        }
        val tvGoTransacciones = findViewById<ImageView>(R.id.imageViewTransaction)
        tvGoTransacciones.setOnClickListener{
            goToTransacciones()
        }
        val tvGoPagar = findViewById<ImageView>(R.id.imageViewPagar)
        tvGoPagar.setOnClickListener{
            goToPagar()
        }
    }

    private fun getUserName() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            // Obtén la referencia a la base de datos en Firebase (asegurándose de que la ruta sea la correcta)
            val database: FirebaseDatabase = FirebaseDatabase.getInstance()
            val myRef: DatabaseReference = database.getReference("Users")  // Usar "Users" en lugar de "usuarios"

            // Recupera el nombre del usuario de la base de datos utilizando el UID del usuario
            val userId = user.uid
            myRef.child(userId).child("name").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Recupera el nombre y lo convierte a mayúsculas antes de actualizar el TextView
                    val userName = dataSnapshot.getValue(String::class.java)?.toUpperCase() ?: "NOMBRE NO DISPONIBLE"
                    tvHeader.text = userName
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Maneja el error si ocurre
                    showAlert("Error al obtener el nombre del usuario")
                }
            })
        } else {
            showAlert("Usuario no autenticado")
        }
    }

    private fun getUserData() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val database: FirebaseDatabase = FirebaseDatabase.getInstance()
            val myRef: DatabaseReference = database.getReference("Users")
            val userId = user.uid

            myRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Recupera los datos de crédito y puntos acumulados
                    val monto = dataSnapshot.child("monto").getValue(Int::class.java) ?: 0
                    val puntos = dataSnapshot.child("puntosAcumulados").getValue(Int::class.java) ?: 0

                    // Actualiza los TextView con los valores obtenidos
                    tvMonto.text = "$monto Bs."
                    tvPuntos.text = "$puntos Pts." // Añadir " Pts." al final de los puntos
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    showAlert("Error al obtener los datos del usuario: ${databaseError.message}")
                }
            })
        } else {
            showAlert("Usuario no autenticado")
        }
    }


    // Método para mostrar alertas en caso de error
    private fun showAlert(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage(message)
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun goToHome(){
        val i = Intent(this, HomePageP::class.java)
        startActivity(i)
    }
    private fun goToAjustes(){

        val i = Intent(this, ajustes_pasajero::class.java)
        startActivity(i)
    }
    private fun goToTransacciones(){

        val i = Intent(this, Transacciones_Pasajero::class.java)
        startActivity(i)
    }
    private fun goToPagar(){

        val i = Intent(this, Pagar_Pasajero::class.java)
        startActivity(i)
    }
}
