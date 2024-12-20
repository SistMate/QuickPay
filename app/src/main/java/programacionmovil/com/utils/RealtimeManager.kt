package programacionmovil.com.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

// Asegúrate de que Transaction coincida con los datos en Firebase
data class Transaction(
    val key: String? = null,
    val amount: Double = 0.0,
    val date: String = "",
    val description: String = ""
)

class RealtimeManager {
    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Obtener las transacciones del usuario logueado
    fun getTransactionsForCurrentUser(): Flow<List<Transaction>> = callbackFlow {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            close(Exception("Usuario no autenticado"))
            return@callbackFlow
        }

        // Referencia a las transacciones del usuario
        val transactionsRef = databaseReference.child(userId).child("transactions")

        val listener = transactionsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val transactions = snapshot.children.mapNotNull { snap ->
                    snap.getValue(Transaction::class.java)?.copy(key = snap.key)
                }
                trySend(transactions).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                close(Exception("Error en la base de datos: ${error.message}"))
            }
        })

        awaitClose { transactionsRef.removeEventListener(listener) }
    }
}
