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
    private lateinit var tvTransacciones: TextView
    private lateinit var tvHeader: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_page_p)



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        tvHeader = findViewById(R.id.tvHeader)
        tvMonto = findViewById(R.id.tvMontoTotal)
        tvPuntos = findViewById(R.id.tvPuntos)



        getUserName()
        getUserData()


        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference


        if (mAuth.currentUser == null) {

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

        val tvGoPuntos = findViewById<ImageView>(R.id.imageCanjeo)
        tvGoPuntos.setOnClickListener{
            goToPuntos()
        }
    }

    private fun getUserName() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {

            val database: FirebaseDatabase = FirebaseDatabase.getInstance()
            val myRef: DatabaseReference = database.getReference("Users")


            val userId = user.uid
            myRef.child(userId).child("name").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    val userName = dataSnapshot.getValue(String::class.java)?.toUpperCase() ?: "NOMBRE NO DISPONIBLE"
                    tvHeader.text = userName
                }

                override fun onCancelled(databaseError: DatabaseError) {

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

                    val monto = dataSnapshot.child("monto").getValue(Int::class.java) ?: 0
                    val puntos = dataSnapshot.child("puntosAcumulados").getValue(Int::class.java) ?: 0


                    tvMonto.text = "$monto Bs."
                    tvPuntos.text = "$puntos Pts."
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    showAlert("Error al obtener los datos del usuario: ${databaseError.message}")
                }
            })
        } else {
            showAlert("Usuario no autenticado")
        }
    }




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

    private fun goToPuntos(){

        val i = Intent(this, CanjeoPuntos::class.java)
        startActivity(i)
    }


}
