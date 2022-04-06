package pt.ipleiria.estg.dei.pi.mymultiprev.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pt.ipleiria.estg.dei.pi.mymultiprev.R
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.Intake
import pt.ipleiria.estg.dei.pi.mymultiprev.databinding.IntakeItemBinding
import pt.ipleiria.estg.dei.pi.mymultiprev.util.Util

class IntakeAdapter(
    private val intakes: List<Intake>
) : RecyclerView.Adapter<IntakeAdapter.IntakeViewHolder>() {

    inner class IntakeViewHolder(private val binding: IntakeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(intake: Intake, position: Int) {
            binding.apply {
                val context = root.context
                labelIntakeId.text = context.getString(R.string.intake_number, position + 1)
                var took = context.getString(R.string.intake_not_taken)

                labelDate.apply {
                    if (intake.took) {
                        took = context.getString(R.string.intake_taken)
                        text = Util.formatDateTime(intake.intakeDate!!)
                    } else {
                        text = Util.formatDateTime(intake.expectedAt!!)
                        setTextColor(binding.root.context.getColor(R.color.colorRed))
                    }
                }
                labelTook.text = took
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntakeViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = IntakeItemBinding.inflate(layoutInflater, parent, false)
        return IntakeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IntakeViewHolder, position: Int) {
        holder.bind(intakes[position], position)
    }

    override fun getItemCount(): Int = intakes.size
}