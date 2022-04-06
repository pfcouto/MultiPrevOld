package pt.ipleiria.estg.dei.pi.mymultiprev.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import pt.ipleiria.estg.dei.pi.mymultiprev.R
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.Drug
import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.entities.PrescriptionItem
import pt.ipleiria.estg.dei.pi.mymultiprev.databinding.PrescriptionItemFullItemBinding
import pt.ipleiria.estg.dei.pi.mymultiprev.databinding.PrescriptionItemShortItemBinding
import java.util.concurrent.TimeUnit


class ActivePrescriptionAdapter(
    private val pairs: List<Pair<PrescriptionItem, Drug?>>,
    private val listener: ActivePrescriptionClickListener,
    private val layoutPref: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    inner class ShortViewHolder(private val binding: PrescriptionItemShortItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("UnsafeExperimentalUsageError")
        fun bind(pair: Pair<PrescriptionItem, Drug?>) {
            binding.apply {
                val prescriptionItem = pair.first
                val drug = pair.second
                this.prescriptionItem = prescriptionItem
                this.drug = drug

                prescriptionItemImage.apply {
                    transitionName = "prescription_item$adapterPosition"
                    Glide.with(context).load(prescriptionItem.imageLocation)
                        .placeholder(R.drawable.default_img).into(this)
                }

                binding.card.setOnClickListener {
                    listener.onSeeDetailsClick(binding.prescriptionItemImage, pair)
                }

                binding.apply {
                    if (prescriptionItem.acquiredAt == null) {
                        card.setBackgroundColor(root.context.getColor(R.color.light_grey))
                        buttonDetailsAndConfirm.apply {
                            setOnClickListener {
                                listener.onConfirmAcquisitionClick(pair)
                            }
                        }
                        activePrescriptionNextIntake.apply {
                            text =
                                root.context.getString(R.string.confirm_acquisition)
                            setTextColor(context.getColor(R.color.colorPrimary))
                        }

                    } else {
                        if (prescriptionItem.nextIntake != null) {
                            if (prescriptionItem.isOverdue) {
                                activePrescriptionNextIntake.apply {
                                    text =
                                        context.getString(R.string.next_dose_overdue)
                                    setTextColor(Color.RED)
                                }
                                buttonDetailsAndConfirm.apply {
                                    setOnClickListener {
                                        listener.onConfirmDoseClick(Pair(prescriptionItem, drug))
                                    }
                                }
                            } else {
                                val diffMillis = prescriptionItem.timeUntil()
                                val dayDiff = TimeUnit.MILLISECONDS.toDays(diffMillis!!)
                                val hourDiff =
                                    TimeUnit.MILLISECONDS.toHours(diffMillis) % TimeUnit.DAYS.toHours(
                                        1
                                    )
                                val minDiff =
                                    TimeUnit.MILLISECONDS.toMinutes(diffMillis) % TimeUnit.HOURS.toMinutes(
                                        1
                                    )

                                activePrescriptionNextIntake.apply {
                                    setTextColor(context.getColor(R.color.colorPrimary))
                                    if (dayDiff == 0L) {
                                        if (hourDiff == 0L) {
                                            text = if (minDiff == 0L)
                                                context.getString(
                                                    R.string.next_dose_info_text_short
                                                )
                                            else
                                                context.getString(
                                                    R.string.next_dose_info_text_short_minute,
                                                    minDiff
                                                )
                                        } else
                                            text = context.getString(
                                                R.string.next_dose_info_text_short_hour,
                                                hourDiff,
                                                minDiff
                                            )
                                    } else
                                        text = context.getString(
                                            R.string.next_dose_info_text_short_day,
                                            dayDiff,
                                            hourDiff
                                        )


                                    buttonDetailsAndConfirm.apply {
                                        setOnClickListener {
                                            listener.onSeeDetailsClick(prescriptionItemImage, pair)
                                        }
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }

    }

    inner class FullViewHolder(private val binding: PrescriptionItemFullItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(pair: Pair<PrescriptionItem, Drug?>) {
            binding.apply {
                val prescriptionItem = pair.first
                val drug = pair.second
                this.prescriptionItem = prescriptionItem
                this.drug = drug

                setAlarmImage(prescriptionItem)

                prescriptionItemImage.apply {
                    transitionName = "prescription_item$adapterPosition"
                    setOnClickListener {
                        listener.onSeeDetailsClick(this, pair)
                    }

                    Glide.with(context).load(prescriptionItem.imageLocation)
                        .placeholder(R.drawable.default_img).into(this)
                }


                buttonAlarm.setOnClickListener {
                    prescriptionItem.alarm = !prescriptionItem.alarm
                    setAlarmImage(prescriptionItem)
                    listener.onAlarmClick(prescriptionItem)
                }


                if (prescriptionItem.acquiredAt == null) {
                    card.setBackgroundColor(root.context.getColor(R.color.light_grey))
                    buttonAlarm.visibility = View.GONE
                    activePrescriptionNextIntake.visibility = View.GONE
                    buttonDetailsAndConfirm.apply {
                        text = context.getString(R.string.active_prescription_confirm_acquisition)
                        setOnClickListener {
                            listener.onConfirmAcquisitionClick(pair)
                        }
                    }
                } else {
                    buttonAlarm.visibility = View.VISIBLE
                    activePrescriptionNextIntake.visibility = View.VISIBLE
                    if (prescriptionItem.nextIntake != null) {
                        if (prescriptionItem.isOverdue) {
                            activePrescriptionNextIntake.apply {
                                text =
                                    context.getString(R.string.next_dose_overdue)
                                setTextColor(Color.RED)
                            }
                            buttonDetailsAndConfirm.apply {
                                text = context.getString(R.string.next_dose_confirm)
                                setOnClickListener {
                                    listener.onConfirmDoseClick(Pair(prescriptionItem, drug))
                                }
                            }
                        } else {
                            val diffMillis = prescriptionItem.timeUntil()
                            val dayDiff = TimeUnit.MILLISECONDS.toDays(diffMillis!!)
                            val hourDiff =
                                TimeUnit.MILLISECONDS.toHours(diffMillis) % TimeUnit.DAYS.toHours(
                                    1
                                )
                            val minDiff =
                                TimeUnit.MILLISECONDS.toMinutes(diffMillis) % TimeUnit.HOURS.toMinutes(
                                    1
                                )

                            activePrescriptionNextIntake.apply {
                                setTextColor(context.getColor(R.color.colorPrimary))
                                if (dayDiff == 0L) {
                                    if (hourDiff == 0L) {
                                        text = if (minDiff == 0L)
                                            context.getString(
                                                R.string.next_dose_info_text
                                            )
                                        else
                                            context.getString(
                                                R.string.next_dose_info_text_minute,
                                                minDiff
                                            )
                                    } else
                                        text = context.getString(
                                            R.string.next_dose_info_text_hour,
                                            hourDiff,
                                            minDiff
                                        )
                                } else
                                    text = context.getString(
                                        R.string.next_dose_info_text_day,
                                        dayDiff,
                                        hourDiff
                                    )

                                buttonDetailsAndConfirm.apply {
                                    text =
                                        context.getString(R.string.active_prescription_see_details)
                                    setOnClickListener {
                                        listener.onSeeDetailsClick(prescriptionItemImage, pair)
                                    }
                                }

                            }
                        }
                    }
                }
                executePendingBindings()
            }
        }

        private fun setAlarmImage(prescriptionItem: PrescriptionItem) {
            when (prescriptionItem.alarm) {
                true -> changeAlarmButtonImage(
                    binding.buttonAlarm,
                    R.drawable.ic_baseline_alarm_on_24
                )
                else -> changeAlarmButtonImage(
                    binding.buttonAlarm,
                    R.drawable.ic_baseline_alarm_off_24
                )
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (layoutPref) 0 else 1
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            0 -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = PrescriptionItemFullItemBinding.inflate(layoutInflater, parent, false)
                FullViewHolder(binding)
            }
            else -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =
                    PrescriptionItemShortItemBinding.inflate(layoutInflater, parent, false)
                ShortViewHolder(binding)
            }
        }
    }

//fun getDrugPositionById(drugId: Int): Int = activePrescriptions.indexOfFirst { it.id == drugId }

    private fun changeAlarmButtonImage(button: ImageView, resId: Int) =
        button.setImageResource(resId)

    override fun getItemCount(): Int = pairs.size

    interface ActivePrescriptionClickListener {
        fun onSeeDetailsClick(imageView: ImageView?, pair: Pair<PrescriptionItem, Drug?>)
        fun onConfirmDoseClick(pair: Pair<PrescriptionItem, Drug?>)
        fun onAlarmClick(prescriptionItem: PrescriptionItem)
        fun onConfirmAcquisitionClick(pair: Pair<PrescriptionItem, Drug?>)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val pair = pairs[position]
        when (holder.itemViewType) {
            0 -> (holder as FullViewHolder).bind(pair)
            1 -> (holder as ShortViewHolder).bind(pair)
        }
    }
}