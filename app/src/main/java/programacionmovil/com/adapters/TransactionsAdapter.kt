package programacionmovil.com.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import programacionmovil.com.R
import programacionmovil.com.models.TransactionC

class TransactionConductorAdapterC(
    private var transactionList: List<TransactionC>
) : RecyclerView.Adapter<TransactionConductorAdapterC.TransactionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactionList[position]
        holder.textDate.text = transaction.title ?: "Sin título" // Verificación null
        holder.textDescription.text = transaction.description ?: "Descripción no disponible"
        holder.textDetails.text = transaction.details ?: "Detalles no disponibles"
        holder.textAmount.text = transaction.monto?.let { it } ?: "Monto no especificado" // Formateo
    }

    override fun getItemCount(): Int = transactionList.size

    fun updateData(newTransactionList: List<TransactionC>) {
        transactionList = newTransactionList
        notifyDataSetChanged() // Mejora: Considera usar DiffUtil para mayor eficiencia
    }

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textDate: TextView = itemView.findViewById(R.id.textDate)
        val textDescription: TextView = itemView.findViewById(R.id.textDescription)
        val textDetails: TextView = itemView.findViewById(R.id.textDetails)
        val textAmount: TextView = itemView.findViewById(R.id.textAmount)
    }
}
