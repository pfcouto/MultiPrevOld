package pt.ipleiria.estg.dei.pi.mymultiprev.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import pt.ipleiria.estg.dei.pi.mymultiprev.R
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.Drug
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.PrescriptionItem
import pt.ipleiria.estg.dei.pi.mymultiprev.databinding.PrescriptionItemHistoryItemBinding

class PrescriptionItemHistoryAdapter(
    private var pairs: List<Pair<PrescriptionItem, Drug?>>,
    private val listener: PrescriptionItemHistoryClickListener
) : RecyclerView.Adapter<PrescriptionItemHistoryAdapter.PrescriptionItemHistoryViewHolder>() {

    inner class PrescriptionItemHistoryViewHolder(private val binding: PrescriptionItemHistoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(pair: Pair<PrescriptionItem, Drug?>) {
            binding.apply {
                val prescriptionItem = pair.first
                this.prescriptionItem = prescriptionItem
                drug = pair.second

                prescriptionItemTotalIntakes.text = root.context.getString(
                    R.string.intakes_taken_count,
                    prescriptionItem.intakesTakenCount ?: 0
                )

                prescriptionItemImage.apply {
                    transitionName = "prescription_item$adapterPosition"
                    Glide.with(context).load(prescriptionItem.imageLocation)
                        .placeholder(R.drawable.default_img).into(this)
                }

                card.setOnClickListener {
                    listener.onSeeDetailsClick(pair)
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PrescriptionItemHistoryViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = PrescriptionItemHistoryItemBinding.inflate(layoutInflater, parent, false)
        return PrescriptionItemHistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PrescriptionItemHistoryViewHolder, position: Int) {
        holder.bind(pairs[position])
    }

    override fun getItemCount(): Int = pairs.size

    fun updatePairs(newPairs: List<Pair<PrescriptionItem, Drug?>>) {
        this.pairs = newPairs
        notifyDataSetChanged()
    }

    interface PrescriptionItemHistoryClickListener {
        fun onSeeDetailsClick(pair: Pair<PrescriptionItem, Drug?>)
    }
}