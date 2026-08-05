package com.example.smartresourceallocation.ui.admin.fragments.resources

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartresourceallocation.R
import com.example.smartresourceallocation.adapter.AdminResourceAdapter
import com.example.smartresourceallocation.databinding.AdminFragmentResourceBinding
import com.example.smartresourceallocation.model.Resource
import com.example.smartresourceallocation.viewmodel.ResourceViewModel
import androidx.core.widget.addTextChangedListener
import com.example.smartresourceallocation.ui.admin.resources.AddEditResourceActivity
import com.example.smartresourceallocation.ui.admin.fragments.resources.AdminResourceDetailsActivity
import com.example.smartresourceallocation.utils.SharedPrefManager

class ResourcesFragment : Fragment(R.layout.admin_fragment_resource) {

    private var _binding: AdminFragmentResourceBinding? = null
    private val binding get() = _binding!!

    private var allResources = listOf<Resource>()
    private lateinit var viewModel: ResourceViewModel
    private lateinit var adapter: AdminResourceAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = AdminFragmentResourceBinding.bind(view)

        viewModel = ViewModelProvider(this)[ResourceViewModel::class.java]

        setupRecyclerView()
        observeData()
        setupTabs()
        setupSearch()

        val token =
            SharedPrefManager(
                requireContext()
            ).getToken()

        if (token != null) {

            binding.shimmerLayout.visibility = View.VISIBLE
            binding.shimmerLayout.startShimmer()

            binding.rvResources.visibility = View.GONE

            viewModel.getMyResources(
                "Bearer $token"
            )

        }
        binding.fabAddResource.setOnClickListener {

            val intent = Intent(
                requireContext(),
                AddEditResourceActivity::class.java
            )

            intent.putExtra("MODE","ADD")

            startActivity(intent)

        }
    }

    private fun setupRecyclerView() {

        adapter = AdminResourceAdapter(
            emptyList()
        ) { resource ->

            val intent = Intent(
                requireContext(),
                AdminResourceDetailsActivity::class.java
            )

            intent.putExtra(
                "RESOURCE_ID",
                resource._id
            )

            startActivity(intent)

        }

        binding.rvResources.layoutManager =
            LinearLayoutManager(requireContext())

        binding.rvResources.adapter = adapter

    }

    private fun observeData() {

        viewModel.resources.observe(viewLifecycleOwner) { list ->

            binding.shimmerLayout.stopShimmer()
            binding.shimmerLayout.visibility = View.GONE

            binding.rvResources.visibility = View.VISIBLE

            allResources = list

            adapter.updateList(list)

            binding.tvTotalResources.text =
                "Total Resources : ${list.size}"

        }

        viewModel.errorMessage.observe(viewLifecycleOwner) {

            binding.shimmerLayout.stopShimmer()
            binding.shimmerLayout.visibility = View.GONE

            binding.rvResources.visibility = View.VISIBLE

            Toast.makeText(
                requireContext(),
                it,
                Toast.LENGTH_SHORT
            ).show()

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupTabs() {

        selectTab(binding.tabAll)

        binding.tabAll.setOnClickListener {

            selectTab(binding.tabAll)

            val token =
                SharedPrefManager(
                    requireContext()
                ).getToken()

            if(token!=null){

                viewModel.getMyResources(
                    "Bearer $token"
                )

            }

        }

        binding.tabMeeting.setOnClickListener {

            selectTab(binding.tabMeeting)

            val token = SharedPrefManager(requireContext()).getToken()

            if (token != null) {

                viewModel.getMyResourcesByCategory(

                    "Bearer $token",

                    "Meeting Room"

                )

            }

        }

        binding.tabLab.setOnClickListener {

            selectTab(binding.tabLab)

            val token = SharedPrefManager(requireContext()).getToken()

            if (token != null) {

                viewModel.getMyResourcesByCategory(

                    "Bearer $token",

                    "Laboratory Equipment"

                )

            }

        }

        binding.tabProjector.setOnClickListener {

            selectTab(binding.tabProjector)

            val token = SharedPrefManager(requireContext()).getToken()

            if (token != null) {

                viewModel.getMyResourcesByCategory(

                    "Bearer $token",

                    "Projector"

                )

            }

        }

        binding.tabSports.setOnClickListener {

            selectTab(binding.tabSports)

            val token = SharedPrefManager(requireContext()).getToken()

            if (token != null) {

                viewModel.getMyResourcesByCategory(

                    "Bearer $token",

                    "Sports Facility"

                )

            }

        }

        binding.tabStudy.setOnClickListener {

            selectTab(binding.tabStudy)

            val token = SharedPrefManager(requireContext()).getToken()

            if (token != null) {

                viewModel.getMyResourcesByCategory(

                    "Bearer $token",

                    "Study Area"

                )

            }

        }

    }
    private fun selectTab(selectedTab: TextView) {

        val tabs = listOf(
            binding.tabAll,
            binding.tabMeeting,
            binding.tabLab,
            binding.tabProjector,
            binding.tabSports,
            binding.tabStudy
        )

        tabs.forEach {

            it.isSelected = false

        }

        selectedTab.isSelected = true

    }
    private fun setupSearch() {

        binding.etSearch.addTextChangedListener {

            val query =
                it.toString().trim().lowercase()

            val filteredList =
                allResources.filter { resource ->

                    resource.name.lowercase().contains(query) ||

                            resource.category.lowercase().contains(query) ||

                            resource.location.lowercase().contains(query)

                }

            adapter.updateList(filteredList)

            binding.tvTotalResources.text =
                "Total Resources : ${filteredList.size}"

        }

    }
    override fun onResume() {

        super.onResume()

        val token =
            SharedPrefManager(
                requireContext()
            ).getToken()

        if (token != null) {

            binding.shimmerLayout.visibility = View.VISIBLE
            binding.shimmerLayout.startShimmer()

            binding.rvResources.visibility = View.GONE

            viewModel.getMyResources(
                "Bearer $token"
            )

        }

    }

}