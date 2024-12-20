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

class CanjeoPuntos : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference


    private lateinit var tvHeader: TextView
    private lateinit var tvPointsAccumulated: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_canjeo_puntos)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        tvHeader = findViewById(R.id.tvHeader)
        tvPointsAccumulated = findViewById(R.id.tvPointsAccumulated)


        getUserName()
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference

        if (mAuth.currentUser == null) {
            // Si no hay sesión activa, redirigir a MainActivity
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

        val tvGoTransacciones = findViewById<ImageView>(R.id.imageViewTransaction)
        tvGoTransacciones.setOnClickListener{
            goToTransacciones()
        }
        val tvGoPuntos = findViewById<ImageView>(R.id.imageCanjeo)
        tvGoPuntos.setOnClickListener{
            goToPuntos()
        }
        getUserData()

    findViewById<Button>(R.id.btnRedeem1).setOnClickListener { redeemPoints(10, 1) }
        findViewById<Button>(R.id.btnRedeem2).setOnClickListener { redeemPoints(50, 6) }
        findViewById<Button>(R.id.btnRedeem3).setOnClickListener { redeemPoints(100, 11) }
        findViewById<Button>(R.id.btnRedeem4).setOnClickListener { redeemPoints(500, 50) }
        findViewById<Button>(R.id.btnRedeem5).setOnClickListener { redeemPoints(1000, 100) }


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

                    tvPointsAccumulated.text = "$puntos Pts."
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    showAlert("Error al obtener los datos del usuario: ${databaseError.message}")
                }
            })
        } else {
            showAlert("Usuario no autenticado")
        }
    }

    private fun redeemPoints(requiredPoints: Int, credit: Int) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userId = user.uid
            val userRef = mDatabase.child("Users").child(userId)

            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val currentMonto = dataSnapshot.child("monto").getValue(Int::class.java) ?: 0
                    val currentPuntos = dataSnapshot.child("puntosAcumulados").getValue(Int::class.java) ?: 0

                    if (currentPuntos >= requiredPoints) {
                        // Suficientes puntos para canjear
                        val newMonto = currentMonto + credit
                        val newPuntos = currentPuntos - requiredPoints
                        userRef.child("monto").setValue(newMonto)
                        userRef.child("puntosAcumulados").setValue(newPuntos)
                        tvPointsAccumulated.text = "$newPuntos Pts." // Actualizar TextView
                        showAlert("¡Canjeo exitoso! Tienes $newMonto Bs para realizar más viajes.")
                    } else {
                        // No suficientes puntos
                        showAlert("No tienes suficientes puntos para este canje.")
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    showAlert("Error al canjear puntos: ${databaseError.message}")
                }
            })
        } else {
            showAlert("Usuario no autenticado")
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

        val i = Intent(this, ajustes_pasajero::class.java)
        startActivity(i)
    }
    private fun goToHome(){

        val i = Intent(this, HomePageP::class.java)
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