package programacionmovil.com

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Toast
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

class ajustes_pasajero : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference

    private lateinit var tvHeader: TextView

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ajustes_pasajero)


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
        val tvGoCanjeo = findViewById<ImageView>(R.id.imageCanjeo)
        tvGoCanjeo.setOnClickListener{
            goToCanjeo()
        }
        val tvGoHome = findViewById<ImageView>(R.id.imageHome)
        tvGoHome.setOnClickListener{
            goToHome()
        }
        val tvGoTransacciones = findViewById<ImageView>(R.id.imageViewTransaction)
        tvGoTransacciones.setOnClickListener{
            goToTransaccion()
        }
        val tvGoAdministracion = findViewById<TextView>(R.id.tvAdminCuenta) 
        tvGoAdministracion.setOnClickListener {
            goToAdministracion()
        }
        val tvCentroAyuda = findViewById<TextView>(R.id.tvCentroAyuda)
        tvCentroAyuda.setOnClickListener { showHelpDialog() }
        val tvAcercaDe = findViewById<TextView>(R.id.tvAcercaDe)
        tvAcercaDe.setOnClickListener { showAboutDialog() }

    }




    private fun showAboutDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Acerca de la Aplicación")

        val scrollView = ScrollView(this)
        val linearLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
        }

        val tvAboutText = TextView(this).apply {
            text = """
                QuickPay es una aplicación móvil diseñada para facilitar el pago de viajes en micros, trufis y otros transportes públicos. 
                
                Funcionalidades principales:
                - **Pago mediante NFC**: Adquiere una tarjeta NFC, recárgala y paga fácilmente al chofer con un simple toque.
                - **Recargas Rápidas**: Puedes recargar tu tarjeta NFC a través de la aplicación de manera rápida y segura.
                - **Sistema de Puntos**: Por cada viaje que pagues, ganarás 1 punto. Acumula puntos suficientes y podrás canjearlos por recompensas.
                
                ¿Qué hace especial a QuickPay?
                QuickPay no solo facilita los pagos, sino que también te recompensa por utilizarlos. Con nuestro sistema de puntos, cada viaje te acerca más a emocionantes recompensas.

                ¡Únete a QuickPay y descubre una nueva forma de viajar en transporte público!
            """.trimIndent()
            setPadding(0, 16, 0, 16)
        }

        linearLayout.addView(tvAboutText)
        scrollView.addView(linearLayout)
        dialogBuilder.setView(scrollView)

        dialogBuilder.setPositiveButton("Cerrar") { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = dialogBuilder.create()
        alertDialog.show()
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

    private fun showHelpDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Centro de Ayuda")


        val linearLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
        }

        val etEmailSubject = EditText(this).apply {
            hint = "Asunto del correo"
            setText("Solicitud de Ayuda")
        }

        val etEmailDescription = EditText(this).apply {
            hint = "Descripción"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE
            setLines(4)
            setText("Describe brevemente tu problema o consulta")
        }

        linearLayout.addView(etEmailSubject)
        linearLayout.addView(etEmailDescription)

        dialogBuilder.setView(linearLayout)
        dialogBuilder.setPositiveButton("Enviar") { dialog, _ ->
            val subject = etEmailSubject.text.toString().trim()
            val description = etEmailDescription.text.toString().trim()
            sendEmail("quickpaycbba@gmail.com", subject, description)
            dialog.dismiss()
        }
        dialogBuilder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }

    private fun sendEmail(to: String, subject: String, body: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(to))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, "No hay aplicaciones de correo instaladas.", Toast.LENGTH_SHORT).show()
        }
    }


private fun goToCanjeo(){

        val i = Intent(this, CanjeoPuntos::class.java)
        startActivity(i)
    }
    private fun goToHome(){

        val i = Intent(this, HomePageP::class.java)
        startActivity(i)
    }
    private fun goToTransaccion(){

        val i = Intent(this, Transacciones_Pasajero::class.java)
        startActivity(i)
    }
    private fun goToAdministracion(){

        val i = Intent(this, Perfil_pasajero::class.java)
        startActivity(i)
    }
}