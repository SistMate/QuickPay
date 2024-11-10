package programacionmovil.com

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.Calendar

class Registro_pasajero : AppCompatActivity() {

    private lateinit var mEditTextName: EditText
    private lateinit var mEditTextEmail: EditText
    private lateinit var mEditTextPassword: EditText
    private lateinit var mEditTextCI: EditText
    private lateinit var mEditTextCel: EditText
    private lateinit var mTextViewDate: TextView
    private lateinit var nButtonRegister: Button

    // Variables de datos a Registrar
    private var name: String = ""
    private var email: String = ""
    private var password: String = ""
    private var ci: String = ""
    private var fechaNacimiento: String = ""
    private var celular: String = ""

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro_pasajero)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val tvGoAtras = findViewById<ImageView>(R.id.iv_back)
        tvGoAtras.setOnClickListener {
            goToAtras()
        }

        // Configurar el DatePickerDialog
        val textViewDate = findViewById<TextView>(R.id.textViewDate)
        val imageViewIconDate = findViewById<ImageView>(R.id.imageViewIconDate)

        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            val selectedDate = "$day/${month + 1}/$year"
            textViewDate.text = selectedDate
            fechaNacimiento = selectedDate
        }

        val eighteenYearsAgo = Calendar.getInstance()
        eighteenYearsAgo.set(currentYear - 18, currentMonth, currentDay)

        val datePickerDialog = DatePickerDialog(
            this,
            datePickerListener,
            currentYear - 18, // Año inicial
            currentMonth,
            currentDay
        )
        datePickerDialog.datePicker.maxDate = eighteenYearsAgo.timeInMillis // Establece la fecha máxima

        textViewDate.setOnClickListener {
            datePickerDialog.show()
        }

        imageViewIconDate.setOnClickListener {
            datePickerDialog.show()
        }

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference

        mEditTextEmail = findViewById(R.id.editTextEmail)
        mEditTextPassword = findViewById(R.id.editTextPassword)
        mEditTextName = findViewById(R.id.editTextUser)
        mEditTextCI = findViewById(R.id.editTextCI)
        mEditTextCel = findViewById(R.id.editTextCel)
        mTextViewDate = findViewById(R.id.textViewDate)
        nButtonRegister = findViewById(R.id.btnRegister)

        nButtonRegister.setOnClickListener {
            name = mEditTextName.text.toString()
            email = mEditTextEmail.text.toString()
            password = mEditTextPassword.text.toString()
            ci = mEditTextCI.text.toString()
            celular = mEditTextCel.text.toString()
            fechaNacimiento = mTextViewDate.text.toString()

            if (validateInputs()) {
                checkIfEmailExists(email)
            }
        }
    }

    private fun validateInputs(): Boolean {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Por favor, ingrese un correo electrónico válido", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password.length < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
            return false
        }
        if (name.length !in 8..50) {
            Toast.makeText(this, "El nombre debe tener entre 8 y 50 caracteres", Toast.LENGTH_SHORT).show()
            return false
        }
        if (ci.length != 8) {
            Toast.makeText(this, "El CI debe tener exactamente 8 caracteres", Toast.LENGTH_SHORT).show()
            return false
        }
        if (fechaNacimiento.isEmpty()) {
            Toast.makeText(this, "Debe seleccionar una fecha de nacimiento", Toast.LENGTH_SHORT).show()
            return false
        }
        if (celular.length != 8) {
            Toast.makeText(this, "El celular debe tener exactamente 8 caracteres", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun checkIfEmailExists(email: String) {
        mDatabase.child("Users").orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Toast.makeText(this@Registro_pasajero, "El correo electrónico ya está registrado", Toast.LENGTH_SHORT).show()
                    } else {
                        registerUser()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(this@Registro_pasajero, "Error de base de datos: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun registerUser() {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val user = mAuth.currentUser
                val userId = user?.uid

                if (userId != null) {
                    val userMap = mapOf(
                        "name" to name,
                        "email" to email,
                        "ci" to ci,
                        "fechaNacimiento" to fechaNacimiento,
                        "celular" to celular,
                        "role" to "Pasajero"
                    )
                    mDatabase.child("Users").child(userId).setValue(userMap)
                        .addOnCompleteListener { dbTask ->
                            if (dbTask.isSuccessful) {
                                Toast.makeText(this, "Registro exitoso. Redirigiendo...", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this@Registro_pasajero, HomePageP::class.java))
                                finish()
                            } else {
                                Toast.makeText(this, "No se pudieron crear los datos correctamente", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            } else {
                Toast.makeText(this, "No se pudo registrar el usuario: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun goToAtras() {
        val i = Intent(this, Elecciondeformulario::class.java)
        startActivity(i)
    }
}
