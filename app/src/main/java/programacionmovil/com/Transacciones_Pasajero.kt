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

class Transacciones_Pasajero : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference

    private lateinit var tvHeader: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_transacciones_pasajero)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvHeader = findViewById(R.id.tvHeader)


        getUserName()
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference


        if (mAuth.currentUser == null) {
            // Si no hay sesión activa, redirigir a MainActivity
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }


        val btnLogout = findViewById<Button>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            mAuth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)

        }
        val tvGoAjustes = findViewById<ImageView>(R.id.imageViewSettings)
        tvGoAjustes.setOnClickListener{
            goToAjustes()
        }
        val tvGoHome = findViewById<ImageView>(R.id.imageHome)
        tvGoHome.setOnClickListener{
            goToHome()
        }
        val tvGoCanjear = findViewById<ImageView>(R.id.imageCanjeo)
        tvGoCanjear.setOnClickListener{
            goToCanjear()
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
                    // Maneja el error si ocurre
                    showAlert("Error al obtener el nombre del usuario")
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

    private fun goToAjustes(){

        val i = Intent(this,ajustes_pasajero::class.java)
        startActivity(i)
    }
    private fun goToHome(){

        val i = Intent(this, HomePageP::class.java)
        startActivity(i)
    }
    private fun goToCanjear(){

        val i = Intent(this, CanjeoPuntos::class.java)
        startActivity(i)
    }

}


