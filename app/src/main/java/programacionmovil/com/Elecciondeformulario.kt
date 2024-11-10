package programacionmovil.com

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Elecciondeformulario : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_elecciondeformulario)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val tvGoIniciarSesion = findViewById<ImageView>(R.id.iv_back)
        tvGoIniciarSesion.setOnClickListener{
            goToIniciarSesion()
        }
        val tvGoRegistroPasajero = findViewById<Button>(R.id.button_register_passenger)
        tvGoRegistroPasajero.setOnClickListener{
            goToRegistroPasajero()
        }
        val tvGoRegistroConductor = findViewById<Button>(R.id.button_register_driver)
        tvGoRegistroConductor.setOnClickListener{
            goToRegistroConductor()
        }

    }
    private fun goToIniciarSesion(){

        val i = Intent(this, MainActivity::class.java)
        startActivity(i)

    }
    private fun goToRegistroPasajero(){

        val i = Intent(this, Registro_pasajero::class.java)
        startActivity(i)

    }
    private fun goToRegistroConductor(){

        val i = Intent(this, Registro_conductor::class.java)
        startActivity(i)

    }
    }
