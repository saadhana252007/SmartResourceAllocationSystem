package com.example.smartresourceallocation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.smartresourceallocation.R
import com.example.smartresourceallocation.databinding.ItemAdminResourceBinding
import com.example.smartresourceallocation.model.Resource

class AdminResourceAdapter(
    private var resourceList: List<Resource>,
    private val onItemClick: (Resource) -> Unit
) : RecyclerView.Adapter<AdminResourceAdapter.ResourceViewHolder>() {

    inner class ResourceViewHolder(
        val binding: ItemAdminResourceBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ResourceViewHolder {

        val binding = ItemAdminResourceBinding.inflate(
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

        holder.binding.tvName.text = resource.name
        holder.binding.tvCategory.text = resource.category
        holder.binding.tvLocation.text = resource.location

        if (resource.resourceType == "CAPACITY_BASED") {

            holder.binding.imgCapacityType.setImageResource(
                R.drawable.capacity_icon
            )

            holder.binding.tvResourceInfo.text =
                "Capacity : ${resource.capacity}"

        } else {

            holder.binding.imgCapacityType.setImageResource(
                R.drawable.quantity_icon
            )

            holder.binding.tvResourceInfo.text =
                "Units : ${resource.availableUnits}"

        }

        val placeholder = when (resource.category) {
            "Meeting Room" -> R.drawable.meeting
            "Laboratory Equipment" -> R.drawable.lab
            "Projector" -> R.drawable.projector
            "Sports Facility" -> R.drawable.sports
            "Study Area" -> R.drawable.study
            else -> R.drawable.meeting
        }

        Glide.with(holder.binding.root)
            .load(resource.imageUrl)
            .placeholder(placeholder)
            .error(placeholder)
            .into(holder.binding.imgResource)

        holder.binding.cardResource.setOnClickListener {

            onItemClick(resource)

        }


    }

    override fun getItemCount(): Int = resourceList.size

    fun updateList(newList: List<Resource>) {
        resourceList = newList
        notifyDataSetChanged()
    }
}