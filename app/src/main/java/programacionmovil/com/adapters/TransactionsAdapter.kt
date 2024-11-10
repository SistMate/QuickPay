package programacionmovil.com.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import programacionmovil.com.R
import programacionmovil.com.models.Transaction

class TransactionsAdapter(private val transactions: MutableList<Transaction>) :
    RecyclerView.Adapter<TransactionsAdapter.TransactionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.amountText.text = transaction.amount.toString()
        holder.dateText.text = transaction.date
        holder.descriptionText.text = transaction.description
    }

    override fun getItemCount(): Int {
        return transactions.size
    }

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val amountText: TextView = itemView.findViewById(R.id.amountText)
        val dateText: TextView = itemView.findViewById(R.id.dateText)
        val descriptionText: TextView = itemView.findViewById(R.id.descriptionText)
    }
}
