package com.example.smartresourceallocation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smartresourceallocation.R
import com.example.smartresourceallocation.databinding.ItemAdminReservationBinding
import com.example.smartresourceallocation.model.Reservation
import com.example.smartresourceallocation.utils.DateUtils

class AdminReservationAdapter(

    private var reservationList: List<Reservation>,

    private val onItemClick:(Reservation)->Unit

):RecyclerView.Adapter<AdminReservationAdapter.ViewHolder>(){

    inner class ViewHolder(

        val binding:ItemAdminReservationBinding

    ):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val binding=

            ItemAdminReservationBinding.inflate(

                LayoutInflater.from(parent.context),

                parent,

                false

            )



        return ViewHolder(binding)

    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val reservation=

            reservationList[position]

        val requestedResource =
            reservation.requestedResource

        val displayResource =
            if (
                reservation.status == "ALTERNATIVE_APPROVED"
            ) {
                reservation.allocatedResource
                    ?: requestedResource
            } else {
                requestedResource
            }

        holder.binding.tvResourceName.text =
            requestedResource?.name
                ?: "Resource Deleted"

        holder.binding.tvCategory.text =
            displayResource?.category
                ?: reservation.resourceCategory

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
            DateUtils.formatReservationDate(
                reservation.date
            )

        holder.binding.tvTime.text =
            "${reservation.startTime} • ${reservation.durationHours} Hour${
                if (reservation.durationHours > 1) "s" else ""
            }"

        holder.binding.tvParticipants.text=

            "Requested By : ${reservation.user.name}\n${reservation.user.email}"

        holder.binding.tvStatus.text=

            reservation.status

        when(reservation.status){

            "PENDING"->{

                holder.binding.tvStatus.setBackgroundResource(
                    R.drawable.status_pending
                )

            }

            "APPROVED"->{

                holder.binding.tvStatus.setBackgroundResource(
                    R.drawable.status_allocated
                )

            }
            "ALTERNATIVE_APPROVED" ->{
                holder.binding.tvStatus.setBackgroundResource(
                    R.drawable.status_allocated
                )
            }


            "WAITLISTED"->{

                holder.binding.tvStatus.setBackgroundResource(
                    R.drawable.status_waitlisted
                )

            }
            "REJECTED",
            "CANCELLED" -> {
                holder.binding.tvStatus.setBackgroundResource(
                    R.drawable.status_rejected
                )
            }

            else -> {
                holder.binding.tvStatus.setBackgroundResource(
                    R.drawable.status_pending
                )
            }

        }

        holder.binding.cardReservation.setOnClickListener{

            onItemClick(reservation)

        }

    }

    override fun getItemCount()=

        reservationList.size

    fun updateList(

        newList:List<Reservation>

    ){

        reservationList=newList

        notifyDataSetChanged()

    }

}