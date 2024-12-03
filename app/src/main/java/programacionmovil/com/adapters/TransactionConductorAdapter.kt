package programacionmovil.com.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import programacionmovil.com.R;
import programacionmovil.com.models.TransactionC

// Modelo de datos
data class Transaction(val id: Int, val description: String)

        // Adaptador de RecyclerView
        class TransactionConductorAdapter(private val transactions: List<TransactionC>) :
        RecyclerView.Adapter<TransactionConductorAdapter.ViewHolder>() {

        // ViewHolder que representa los elementos de la lista
        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    // Aquí puedes inicializar vistas específicas de tu elemento, por ejemplo:
    // val textView: TextView = view.findViewById(R.id.textView)
}

        // Inflar la vista de cada elemento
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
    return ViewHolder(view)
}

        // Enlazar datos con la vista
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val transaction = transactions[position]
    // Aquí puedes enlazar los datos del modelo a las vistas del ViewHolder
    // Por ejemplo: holder.textView.text = transaction.description
}

        // Cantidad de elementos en la lista
        override fun getItemCount(): Int {
    return transactions.size
}
}
//
//
//package programacionmovil.com.adapters
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import programacionmovil.com.R
//import programacionmovil.com.models.TransactionC
//
//// Adaptador de RecyclerView para mostrar las transacciones
//class TransactionConductorAdapter(private val transactions: List<TransactionC>) :
//    RecyclerView.Adapter<TransactionConductorAdapter.ViewHolder>() {
//
//    // ViewHolder que representa los elementos de la lista
//    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val titleText: TextView = view.findViewById(R.id.titleText)
//        val paymentMethodText: TextView = view.findViewById(R.id.paymentMethodText)
//        val trufiText: TextView = view.findViewById(R.id.trufiText)
//        val amountText: TextView = view.findViewById(R.id.amountText)
//    }
//
//    // Inflar la vista de cada elemento
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.item_transaction, parent, false)
//        return ViewHolder(view)
//    }
//
//    // Enlazar datos con la vista
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val transaction = transactions[position]
//        holder.titleText.text = transaction.title
//        holder.paymentMethodText.text = transaction.paymentMethod
//        holder.trufiText.text = transaction.trufi
//        holder.amountText.text = transaction.amount
//    }
//
//    // Cantidad de elementos en la lista
//    override fun getItemCount(): Int {
//        return transactions.size
//    }
//}
