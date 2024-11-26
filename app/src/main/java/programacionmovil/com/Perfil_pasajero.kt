package programacionmovil.com

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
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
import com.google.firebase.database.*
import android.graphics.Color
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.firebase.auth.EmailAuthProvider
import java.util.Calendar


class Perfil_pasajero : AppCompatActivity() {

    private lateinit var mDatabase: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private lateinit var nameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var ciEditText: EditText
    private lateinit var fechaNacimientoEditText: EditText
    private lateinit var numeroCelularEditText: EditText

    // Variables para almacenar los valores iniciales
    private var ciInicial: String = ""
    private var initialFechaNacimiento: String = ""
    private var initialCelular: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_perfil_pasajero)

        // Configurar diseño con insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializar Firebase Auth y Database
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference

        // Referencias a los elementos del layout
        initializeUI()

        // Recuperar y mostrar la información del usuario
        fetchUserData { name, email, ci, fechaNacimiento, celular ->
            ciInicial = ci ?: ""
            initialFechaNacimiento = fechaNacimiento ?: ""
            initialCelular = celular ?: ""

            // Asignar el nombre y correo a los TextView
            nameTextView.text = name ?: "Nombre no disponible"
            emailTextView.text = email ?: "Correo no disponible"
        }
        val fechaNacimientoEditText = findViewById<EditText>(R.id.textViewFechaNacimiento)

        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

// Restricción para que la fecha máxima sea hace 18 años
        val eighteenYearsAgo = Calendar.getInstance()
        eighteenYearsAgo.set(currentYear - 18, currentMonth, currentDay)

        val datePickerListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            val selectedDate = "%02d/%02d/%04d".format(day, month + 1, year)
            fechaNacimientoEditText.setText(selectedDate) // Actualiza el texto del EditText
        }

        fechaNacimientoEditText.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                this,
                datePickerListener,
                currentYear - 18, // Año inicial (18 años atrás)
                currentMonth,
                currentDay
            )
            datePickerDialog.datePicker.maxDate = eighteenYearsAgo.timeInMillis // Establece la fecha máxima
            datePickerDialog.show()
        }
        // Configurar el botón de cambiar contraseña
        val changePasswordButton = findViewById<Button>(R.id.btn_change_password)
        changePasswordButton.setOnClickListener {
            showChangePasswordDialog()
        }

    }

    private fun initializeUI() {
        nameTextView = findViewById(R.id.textViewName)
        emailTextView = findViewById(R.id.textViewEmail)
        ciEditText = findViewById(R.id.textViewCedula)
        fechaNacimientoEditText = findViewById(R.id.textViewFechaNacimiento)
        numeroCelularEditText = findViewById(R.id.textViewNumeroCelular)

        // Configuración de botones de navegación
        findViewById<ImageView>(R.id.imageAtras).setOnClickListener { goToAjustes() }
        findViewById<ImageView>(R.id.imageCanjeo).setOnClickListener { goToCanjeo() }
        findViewById<ImageView>(R.id.imageHome).setOnClickListener { goToHome() }
        findViewById<ImageView>(R.id.imageViewTransaction).setOnClickListener { goToTransaccion() }
        findViewById<ImageView>(R.id.imageViewSettings).setOnClickListener { goToAjuste() }


        val btnSaveChanges = findViewById<Button>(R.id.btnSaveChanges)
        btnSaveChanges.setOnClickListener { saveUserData() }
    }
    private fun setupChangeListeners() {
        val btnSaveChanges = findViewById<Button>(R.id.btnSaveChanges)

        // Listener genérico para detectar cambios
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateButtonState(btnSaveChanges)
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        ciEditText.addTextChangedListener(textWatcher)
        fechaNacimientoEditText.addTextChangedListener(textWatcher)
        numeroCelularEditText.addTextChangedListener(textWatcher)
    }
    private fun updateButtonState(btnSaveChanges: Button) {
        val newCi = ciEditText.text.toString()
        val newFechaNacimiento = fechaNacimientoEditText.text.toString()
        val newCelular = numeroCelularEditText.text.toString()

        if (newCi != ciInicial || newFechaNacimiento != initialFechaNacimiento || newCelular != initialCelular) {
            // Cambios detectados, activa el botón y cambia el color
            btnSaveChanges.isEnabled = true
            btnSaveChanges.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAzulmedio))
        } else {
            // Sin cambios, desactiva el botón y vuelve al color original
            btnSaveChanges.isEnabled = false
            btnSaveChanges.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAzulsuave))
        }
    }

    private fun saveUserData() {
        val userId = mAuth.currentUser?.uid
        if (userId != null) {
            val newCi = ciEditText.text.toString()
            val newFechaNacimiento = fechaNacimientoEditText.text.toString()
            val newCelular = numeroCelularEditText.text.toString()

            // Validación de número de celular (debe tener 8 dígitos)
            if (newCelular.length != 8) {
                Toast.makeText(this, "El número de celular debe tener 8 dígitos.", Toast.LENGTH_SHORT).show()
                return
            }

            // Validación de cédula (debe tener entre 7 y 8 dígitos)
            if (newCi.length !in 7..8) {
                Toast.makeText(this, "La cédula debe tener entre 7 y 8 dígitos.", Toast.LENGTH_SHORT).show()
                return
            }

            // Comparar valores iniciales con nuevos
            val changes = mutableListOf<String>()
            if (ciInicial != newCi) changes.add("Cédula: $ciInicial -> $newCi")
            if (initialFechaNacimiento != newFechaNacimiento) changes.add("Fecha de Nacimiento: $initialFechaNacimiento -> $newFechaNacimiento")
            if (initialCelular != newCelular) changes.add("Número de Celular: $initialCelular -> $newCelular")

            if (changes.isEmpty()) {
                Toast.makeText(this, "No se realizó ningún cambio", Toast.LENGTH_SHORT).show()
                return
            }

            // Mostrar advertencia con cambios detectados
            showConfirmationDialog(userId, changes, newCi, newFechaNacimiento, newCelular)
        } else {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showConfirmationDialog(
        userId: String,
        changes: List<String>,
        newCi: String,
        newFechaNacimiento: String,
        newCelular: String
    ) {
        val message = """
        Se actualizarán los siguientes datos:
        ${changes.joinToString("\n")}
        
        ¿Desea continuar?
    """.trimIndent()

        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Confirmar Cambios")
            .setMessage(message)
            .setPositiveButton("Aceptar") { _, _ ->
                updateUserData(userId, newCi, newFechaNacimiento, newCelular)
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        dialog.show()
    }

    private fun updateUserData(userId: String, ci: String, fechaNacimiento: String, celular: String) {
        // Crear un mapa con los nuevos valores
        val updates = mapOf(
            "ci" to ci,
            "fechaNacimiento" to fechaNacimiento,
            "celular" to celular
        )

        // Actualizar los datos en Firebase
        mDatabase.child("Users").child(userId).updateChildren(updates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error al actualizar datos", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun fetchUserData(onDataLoaded: (String?, String?, String?, String?, String?) -> Unit) {
        val userId = mAuth.currentUser?.uid
        if (userId != null) {
            mDatabase.child("Users").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val name = snapshot.child("name").value?.toString()
                    val email = snapshot.child("email").value?.toString()
                    val ci = snapshot.child("ci").value?.toString()
                    val fechaNacimiento = snapshot.child("fechaNacimiento").value?.toString()
                    val celular = snapshot.child("celular").value?.toString()

                    // Llamar al callback y pasar los datos recuperados
                    onDataLoaded(name, email, ci, fechaNacimiento, celular)
                    ciEditText.setText(ci ?: "")
                    fechaNacimientoEditText.setText(fechaNacimiento ?: "")
                    numeroCelularEditText.setText(celular ?: "")
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@Perfil_pasajero, "Error al obtener datos: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
        }
    }

    // Método para mostrar el AlertDialog de cambio de contraseña
    private fun showChangePasswordDialog() {
        // Crear EditText para la contraseña actual y la nueva
        val currentPasswordEditText = EditText(this)
        currentPasswordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        currentPasswordEditText.hint = "Contraseña actual"

        val newPasswordEditText = EditText(this)
        newPasswordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        newPasswordEditText.hint = "Nueva contraseña"

        // Crear un LinearLayout para contener los EditText
        val linearLayout = LinearLayout(this)
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.addView(currentPasswordEditText)
        linearLayout.addView(newPasswordEditText)

        // Crear el AlertDialog
        val dialog = AlertDialog.Builder(this)
            .setTitle("Cambiar Contraseña")
            .setView(linearLayout)
            .setPositiveButton("Aceptar") { dialogInterface, i ->
                val currentPassword = currentPasswordEditText.text.toString()
                val newPassword = newPasswordEditText.text.toString()

                if (currentPassword.isEmpty() || newPassword.isEmpty()) {
                    Toast.makeText(this, "Por favor ingrese ambas contraseñas", Toast.LENGTH_SHORT).show()
                } else if (newPassword.length < 6) {
                    // Verificar que la nueva contraseña tenga al menos 6 caracteres
                    Toast.makeText(this, "La nueva contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                } else {
                    // Verificar la contraseña actual y cambiarla
                    changePassword(currentPassword, newPassword)
                }
            }
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()
    }

    // Método para cambiar la contraseña en Firebase
    // Método para cambiar la contraseña en Firebase
    private fun changePassword(currentPassword: String, newPassword: String) {
        val user = mAuth.currentUser

        // Primero, volvemos a autenticar al usuario con la contraseña actual
        val credential = EmailAuthProvider.getCredential(user!!.email!!, currentPassword)

        user.reauthenticate(credential)
            .addOnCompleteListener { reauthenticateTask ->
                if (reauthenticateTask.isSuccessful) {
                    // Contraseña actual es correcta, ahora cambiamos la contraseña
                    user.updatePassword(newPassword)
                        .addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                Toast.makeText(this, "Contraseña cambiada exitosamente", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this, "Error al cambiar la contraseña", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    // La contraseña actual es incorrecta
                    Toast.makeText(this, "La contraseña actual es incorrecta", Toast.LENGTH_SHORT).show()
                }
            }
    }



    private fun goToAjustes() {
        val i = Intent(this, ajustes_pasajero::class.java)
        startActivity(i)
    }

    private fun goToCanjeo() {
        val i = Intent(this, CanjeoPuntos::class.java)
        startActivity(i)
    }

    private fun goToHome() {
        val i = Intent(this, HomePageP::class.java)
        startActivity(i)
    }

    private fun goToTransaccion() {
        val i = Intent(this, Transacciones_Pasajero::class.java)
        startActivity(i)
    }

    private fun goToAjuste() {
        val i = Intent(this, ajustes_pasajero::class.java)
        startActivity(i)
    }
}
