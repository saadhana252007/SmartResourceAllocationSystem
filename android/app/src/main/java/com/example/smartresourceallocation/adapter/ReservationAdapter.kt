package com.example.smartresourceallocation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smartresourceallocation.R
import com.example.smartresourceallocation.databinding.ItemReservationBinding
import com.example.smartresourceallocation.model.Reservation
import com.example.smartresourceallocation.utils.DateUtils

class ReservationAdapter(

    private var reservationList:
    List<Reservation>,

    private val onItemClick:
        (Reservation) -> Unit

) : RecyclerView.Adapter<
        ReservationAdapter.ReservationViewHolder>() {

    inner class ReservationViewHolder(

        val binding:
        ItemReservationBinding

    ) : RecyclerView.ViewHolder(
        binding.root
    )

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReservationViewHolder {

        val binding =
            ItemReservationBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ),
                parent,
                false
            )

        return ReservationViewHolder(
            binding
        )

    }

    override fun onBindViewHolder(
        holder: ReservationViewHolder,
        position: Int
    ) {

        val reservation =
            reservationList[position]

        holder.binding.tvResourceName.text =
            reservation.requestedResource?.name
                ?: "${reservation.resourceCategory} - Resource Deleted"

        if (
            reservation.status == "ALTERNATIVE_APPROVED" &&
            reservation.allocatedResource != null
        ) {

            holder.binding.tvAllocatedLabel.visibility =
                View.VISIBLE

            holder.binding.tvAllocatedResource.visibility =
                View.VISIBLE

            holder.binding.tvAllocatedResource.text =
                reservation.allocatedResource.name

        } else {

            holder.binding.tvAllocatedLabel.visibility =
                View.GONE

            holder.binding.tvAllocatedResource.visibility =
                View.GONE

        }

        holder.binding.tvDate.text =
            "Date: ${DateUtils.formatReservationDate(reservation.date)}"

        holder.binding.tvTime.text =
            if (
                reservation.allocationType ==
                "ALTERNATE_TIME"
            ) {

                "Allocated Time: ${reservation.startTime}"

            } else {

                "Time: ${reservation.startTime}"

            }

        holder.binding.tvPurpose.text =
            "Purpose: ${reservation.purpose}"

        holder.binding.tvStatus.text =
            reservation.status

        when (reservation.status) {

            "PENDING" -> {

                holder.binding.tvStatus
                    .setBackgroundResource(
                        R.drawable.status_pending
                    )

            }

            "APPROVED" -> {

                holder.binding.tvStatus
                    .setBackgroundResource(
                        R.drawable.status_approved
                    )

            }

            "WAITLISTED" -> {

                holder.binding.tvStatus
                    .setBackgroundResource(
                        R.drawable.status_waitlisted
                    )

            }

            "REJECTED" -> {

                holder.binding.tvStatus
                    .setBackgroundResource(
                        R.drawable.status_rejected
                    )

            }

            "ALTERNATIVE_APPROVED" -> {

                holder.binding.tvStatus
                    .setBackgroundResource(
                        R.drawable.status_alternative
                    )

            }
            "CANCELLED" -> {

                holder.binding.tvStatus
                    .setBackgroundResource(
                        R.drawable.status_rejected
                    )

            }

        }

        holder.binding.root.setOnClickListener {

            onItemClick(
                reservation
            )

        }

    }

    override fun getItemCount(): Int {

        return reservationList.size

    }

    fun updateList(
        newList: List<Reservation>
    ) {

        reservationList = newList

        notifyDataSetChanged()

    }

}