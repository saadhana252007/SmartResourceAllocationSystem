package com.example.smartresourceallocation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smartresourceallocation.R
import com.example.smartresourceallocation.databinding.ItemResourceBinding
import com.example.smartresourceallocation.model.Resource

class ResourceAdapter(
    private var resourceList: List<Resource>,
    private val onItemClick: (Resource) -> Unit
) : RecyclerView.Adapter<ResourceAdapter.ResourceViewHolder>() {

    inner class ResourceViewHolder(
        val binding: ItemResourceBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ResourceViewHolder {

        val binding = ItemResourceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ResourceViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ResourceViewHolder,
        position: Int
    ) {

        val resource = resourceList[position]

        holder.binding.tvName.text =
            resource.name

        holder.binding.tvCategory.text =
            resource.category

        holder.binding.tvLocation.text =
            resource.location

        if (
            resource.resourceType ==
            "CAPACITY_BASED"
        ) {

            holder.binding.imgCapacityType
                .setImageResource(
                    R.drawable.capacity_icon
                )

            holder.binding.tvResourceInfo.text =
                "Capacity : ${resource.capacity}"

        } else {

            holder.binding.imgCapacityType
                .setImageResource(
                    R.drawable.quantity_icon
                )

            holder.binding.tvResourceInfo.text =
                "Units : ${resource.availableUnits}"

        }

        when (resource.category) {

            "Meeting Room" -> {
                holder.binding.imgResource
                    .setImageResource(
                        R.drawable.meeting
                    )
            }

            "Laboratory Equipment" -> {
                holder.binding.imgResource
                    .setImageResource(
                        R.drawable.lab
                    )
            }

            "Projector" -> {
                holder.binding.imgResource
                    .setImageResource(
                        R.drawable.projector
                    )
            }

            "Sports Facility" -> {
                holder.binding.imgResource
                    .setImageResource(
                        R.drawable.sports
                    )
            }

            "Study Area" -> {
                holder.binding.imgResource
                    .setImageResource(
                        R.drawable.study
                    )
            }

            else -> {
                holder.binding.imgResource
                    .setImageResource(
                        R.drawable.meeting
                    )
            }
        }

        holder.binding.cardResource
            .setOnClickListener {

                onItemClick(resource)

            }

    }

    override fun getItemCount(): Int {

        return resourceList.size

    }

    fun updateList(
        newList: List<Resource>
    ) {

        resourceList = newList

        notifyDataSetChanged()

    }

}