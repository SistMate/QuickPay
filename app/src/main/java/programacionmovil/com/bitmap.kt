package programacionmovil.com

import android.os.Environment
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date

fun saveQRCodeToStorage(bitmap: Bitmap): String {
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val fileName = "QR_$timeStamp.png"

    // Ruta de almacenamiento
    val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)

    if (!storageDir.exists()) {
        storageDir.mkdirs()
    }

    val qrFile = File(storageDir, fileName)

    try {
        val outputStream = FileOutputStream(qrFile)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
        return "QR guardado en: ${qrFile.absolutePath}"
    } catch (e: Exception) {
        e.printStackTrace()
        return "Error al guardar el QR"
    }
}
