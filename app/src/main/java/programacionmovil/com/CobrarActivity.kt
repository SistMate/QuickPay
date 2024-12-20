package programacionmovil.com

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.google.zxing.BarcodeFormat
import programacionmovil.com.conductor.SettingsCActivity



class CobrarActivity : AppCompatActivity() {



    private lateinit var imageViewHome: ImageView
    private lateinit var imageViewPagar: ImageView
    private lateinit var imageViewTransaction: ImageView
    private lateinit var imageViewSettings: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cobrar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var ivCodigoQR: ImageView = findViewById(R.id.ivCodigoQR)
        var etDatos: EditText = findViewById(R.id.etDatos)
        var btnGenerar: Button = findViewById(R.id.btnGenerar)
        var btnDownload: Button = findViewById(R.id.btnDownload)

        imageViewHome = findViewById(R.id.imageView2)
        imageViewPagar = findViewById(R.id.imageViewPagar)
        imageViewTransaction = findViewById(R.id.imageViewTransaction)
        imageViewSettings = findViewById(R.id.imageViewSettings)



        imageViewHome.setOnClickListener {
            startActivity(Intent(this, HomePageA::class.java))
        }

        imageViewPagar.setOnClickListener {
            startActivity(Intent(this, CobrarActivity::class.java))
        }

        imageViewTransaction.setOnClickListener {
            startActivity(Intent(this, TransactionActivity::class.java))
        }

        imageViewSettings.setOnClickListener {
            startActivity(Intent(this, SettingsCActivity::class.java))
        }


        btnGenerar.setOnClickListener(android.view.View.OnClickListener{
            try {
                var barcodeEncoder: BarcodeEncoder = BarcodeEncoder()
                var bitmap: Bitmap = barcodeEncoder.encodeBitmap(
                    etDatos.text.toString(),
                    BarcodeFormat.QR_CODE,
                    750,
                    750
                )

                ivCodigoQR.setImageBitmap(bitmap)
            }catch (e: Exception){
                e.printStackTrace()
            }
        })


        //Funcion de descargar el qr
        btnDownload.setOnClickListener {
            try {
                val drawable = ivCodigoQR.drawable
                if (drawable != null && drawable is android.graphics.drawable.BitmapDrawable) {
                    val bitmap = drawable.bitmap
                    val message = saveQRCodeToStorage(bitmap)
                    android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_LONG).show()
                } else {
                    android.widget.Toast.makeText(this, "No hay un código QR para descargar", android.widget.Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                android.widget.Toast.makeText(this, "Error al descargar el QR", android.widget.Toast.LENGTH_SHORT).show()
            }
        }



    }
}