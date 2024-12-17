package programacionmovil.com

import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.text.InputType
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.database.*
import android.os.Handler



class MainActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var emailText: EditText
    private lateinit var passwordText: EditText
    private lateinit var logInButton: Button
    private lateinit var showPasswordIcon: ImageView
    private lateinit var mDatabase: DatabaseReference
    private var isPasswordVisible: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        // Retraso para aplicar el tema después de 8 segundos
        Handler(Looper.getMainLooper()).postDelayed({
            setTheme(R.style.Theme_MyLoggin)
        }, 10000)

        setTheme(R.style.Theme_MyLoggin)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference

        // Verifica si hay una sesión activa
        if (mAuth.currentUser != null) {
            val userId = mAuth.currentUser!!.uid
            mDatabase.child("Users").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val role = dataSnapshot.child("role").getValue(String::class.java)
                    if (role != null) {
                        when (role) {
                            "Pasajero" -> showHomePageP(mAuth.currentUser!!.email ?: "")
                            "Conductor" -> showHomePageC(mAuth.currentUser!!.email ?: "")
                            "Administrador" -> showHomePageA(mAuth.currentUser!!.email ?: "")
                            else -> showAlert("Rol desconocido")
                        }
                    } else {
                        showAlert("Rol no encontrado")
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    showAlert("Error de base de datos: ${databaseError.message}")
                }
            })
        }

        emailText = findViewById(R.id.emailText)
        passwordText = findViewById(R.id.passwordText)
        logInButton = findViewById(R.id.logInButton)
        showPasswordIcon = findViewById(R.id.showPasswordIcon)

        val tvGoRegister = findViewById<TextView>(R.id.tv_go_to_register)
        tvGoRegister.setOnClickListener {
            goToeleccionFormulario()
        }

        showPasswordIcon.setOnClickListener {
            togglePasswordVisibility()
        }

        setup()
    }

    private fun setup() {
        logInButton.setOnClickListener {
            val email = emailText.text.toString()
            val password = passwordText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    checkIfEmailExistsAndSignIn(email, password)
                } else {
                    emailText.error = "Por favor, ingrese un correo electrónico válido"
                    emailText.requestFocus()
                }
            } else {
                if (email.isEmpty()) {
                    emailText.error = "El correo electrónico no puede estar vacío"
                    emailText.requestFocus()
                }
                if (password.isEmpty()) {
                    passwordText.error = "La contraseña no puede estar vacía"
                    passwordText.requestFocus()
                }
            }
        }
    }

    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            passwordText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            showPasswordIcon.setBackgroundResource(R.drawable.eye_blocked)
        } else {
            passwordText.inputType = InputType.TYPE_CLASS_TEXT
            showPasswordIcon.setBackgroundResource(R.drawable.eye)
        }
        passwordText.setSelection(passwordText.text.length)
        isPasswordVisible = !isPasswordVisible
    }

    private fun checkIfEmailExistsAndSignIn(email: String, password: String) {
        mDatabase.child("Users").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // El correo electrónico está registrado, intenta iniciar sesión
                    signIn(email, password)
                } else {
                    // El correo electrónico no está registrado
                    showAlert("El correo electrónico no está registrado.")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                showAlert("Error de base de datos: ${databaseError.message}")
            }
        })
    }
    private fun signIn(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = mAuth.currentUser?.uid
                if (userId != null) {
                    mDatabase.child("Users").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val role = dataSnapshot.child("role").getValue(String::class.java)
                            if (role != null) {
                                when (role) {
                                    "Pasajero" -> showHomePageP(email)
                                    "Conductor" -> showHomePageC(email)
                                    "Administrador" -> showHomePageA(email)
                                    else -> showAlert("Rol desconocido")
                                }
                            } else {
                                showAlert("Rol no encontrado")
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            showAlert("Error de base de datos: ${databaseError.message}")
                        }
                    })
                }
            } else {
                val errorMessage = when (task.exception) {
                    is FirebaseAuthInvalidUserException -> "El correo electrónico no está registrado."
                    is FirebaseAuthInvalidCredentialsException -> "Contraseña incorrecta."
                    else -> "Error al iniciar sesión: ${task.exception?.message}"
                }
                showAlert(errorMessage)
            }
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

    private fun showHomePageP(email: String) {
        val homeIntent = Intent(this, HomePageP::class.java).apply {
            putExtra("email", email)
        }
        startActivity(homeIntent)
        finish()
    }

    private fun showHomePageC(email: String) {
        val homeIntent = Intent(this, HomePageC::class.java).apply {
            putExtra("email", email)
        }
        startActivity(homeIntent)
        finish()
    }

    private fun showHomePageA(email: String) {
        val homeIntent = Intent(this, HomePageA::class.java).apply {
            putExtra("email", email)
        }
        startActivity(homeIntent)
        finish()
    }

    private fun goToeleccionFormulario() {
        val i = Intent(this, Elecciondeformulario::class.java)
        startActivity(i)
    }
}

