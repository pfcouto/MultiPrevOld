package pt.ipleiria.estg.dei.pi.mymultiprev.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.datetime.LocalDateTime
import pt.ipleiria.estg.dei.pi.mymultiprev.R
import pt.ipleiria.estg.dei.pi.mymultiprev.databinding.PredictDateTimeItemBinding
import pt.ipleiria.estg.dei.pi.mymultiprev.util.Util


class PrevisionDateAdapter(
    private val dates: List<LocalDateTime>
) : RecyclerView.Adapter<PrevisionDateAdapter.PrevisionDateViewHolder>() {
    inner class PrevisionDateViewHolder(private val binding: PredictDateTimeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(date: LocalDateTime, position: Int) {
            val realPosition = position + 1
            val predictedDate = Util.formatDateTime(date)

            binding.apply {
                val context = root.context
                dateString.text =
                    context.getString(
                        R.string.prevision_intake_number_date,
                        realPosition,
                        predictedDate
                    )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrevisionDateViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = PredictDateTimeItemBinding.inflate(layoutInflater, parent, false)
        return PrevisionDateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PrevisionDateViewHolder, position: Int) {
        holder.bind(dates[position], position)
    }

    override fun getItemCount(): Int = dates.size
}