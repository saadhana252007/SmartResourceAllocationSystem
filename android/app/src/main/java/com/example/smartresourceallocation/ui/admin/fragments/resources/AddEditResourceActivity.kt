package com.example.smartresourceallocation.ui.admin.resources

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.smartresourceallocation.databinding.ActivityAddEditResourceBinding
import android.net.Uri
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.smartresourceallocation.R
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.smartresourceallocation.model.CreateResourceRequest
import com.example.smartresourceallocation.utils.SharedPrefManager
import com.example.smartresourceallocation.viewmodel.ResourceViewModel
import com.example.smartresourceallocation.viewmodel.UploadViewModel
import okhttp3.MultipartBody
import okhttp3.MediaType.Companion.toMediaType
import java.io.File
import okhttp3.RequestBody.Companion.asRequestBody
import android.app.TimePickerDialog
import java.util.Locale

class AddEditResourceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditResourceBinding

    private var selectedImageUri: Uri? = null

    private var existingImageUrl = ""
    private lateinit var uploadViewModel: UploadViewModel

    private lateinit var resourceViewModel: ResourceViewModel

    private var mode = "ADD"

    private var resourceId = ""

    private var workingStartTime = "08:00"

    private var workingEndTime = "20:00"

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent())
        { uri ->
            if (uri != null) {
                selectedImageUri = uri
                binding.imgResource.setImageURI(uri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddEditResourceBinding.inflate(layoutInflater)

        setContentView(binding.root)

        resourceViewModel=
            ViewModelProvider(this)[ResourceViewModel::class.java]

        mode =
            intent.getStringExtra("MODE")
                ?: "ADD"

        resourceId =
            intent.getStringExtra("RESOURCE_ID")
                ?: ""

        if (mode == "EDIT") {

            binding.btnSaveResource.text =
                "UPDATE RESOURCE"

        }
        if(
            mode=="EDIT"
        ){



            val token =
                SharedPrefManager(this)
                    .getToken()

            if (token != null) {

                resourceViewModel.getResourceById(
                    "Bearer $token",
                    resourceId
                )

            }

        }

        binding.imgResource.setOnClickListener {

            imagePickerLauncher.launch("image/*")

        }

        binding.etWorkingStart.setOnClickListener {

            showTimePicker(true)

        }

        binding.etWorkingEnd.setOnClickListener {

            showTimePicker(false)

        }
        val categories = resources.getStringArray(
            R.array.resource_categories
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            categories
        )

        binding.actCategory.setAdapter(adapter)
        uploadViewModel=
            ViewModelProvider(this)[UploadViewModel::class.java]

        binding.btnSaveResource.setOnClickListener {

            if (!validateInputs()) {
                return@setOnClickListener
            }

            binding.loadingOverlay.visibility = View.VISIBLE
            binding.progressUpload.visibility = View.VISIBLE
            binding.btnSaveResource.isEnabled = false

            if (mode == "ADD") {

                if (selectedImageUri == null) {

                    val defaultImageUrl = when (binding.actCategory.text.toString()) {

                        "Meeting Room" ->
                            "https://res.cloudinary.com/dxs2zvsj1/image/upload/v1784881129/meeting_dmf48n.jpg"

                        "Laboratory Equipment" ->
                            "https://res.cloudinary.com/dxs2zvsj1/image/upload/v1784881152/lab_fnsz4p.jpg"

                        "Projector" ->
                            "https://res.cloudinary.com/dxs2zvsj1/image/upload/v1784881201/projector_gmgqj2.jpg"

                        "Sports Facility" ->
                            "https://res.cloudinary.com/dxs2zvsj1/image/upload/v1784881170/sports_jwq5nf.jpg"

                        "Study Area" ->
                            "https://res.cloudinary.com/dxs2zvsj1/image/upload/v1784881180/study_cx8jhi.jpg"

                        else -> ""

                    }

                    createOrUpdateResource(defaultImageUrl)

                    return@setOnClickListener

                }

                val image =
                    uriToMultipart(selectedImageUri!!)

                uploadViewModel.uploadImage(image)

            }

            else {

                if (selectedImageUri != null) {

                    val image =
                        uriToMultipart(selectedImageUri!!)

                    uploadViewModel.uploadImage(image)

                }

                else {

                    createOrUpdateResource(existingImageUrl)

                    uploadViewModel.imageUrl.value = null

                }

            }

        }



        uploadViewModel.imageUrl.observe(this) { imageUrl ->

            if (imageUrl.isNullOrBlank()) return@observe

            createOrUpdateResource(imageUrl)

        }
        resourceViewModel.createSuccess.observe(this){
            if(it){
                Toast.makeText(
                    this,
                    "Resource Created Successfully",
                    Toast.LENGTH_SHORT
                ).show()
                binding.loadingOverlay.visibility = View.GONE
                binding.progressUpload.visibility = View.GONE
                binding.btnSaveResource.isEnabled = true
                finish()
            }
        }
        resourceViewModel.updateSuccess.observe(this) {

            if (it) {

                Toast.makeText(
                    this,
                    "Resource Updated Successfully",
                    Toast.LENGTH_SHORT
                ).show()
                binding.loadingOverlay.visibility = View.GONE
                binding.progressUpload.visibility = View.GONE
                binding.btnSaveResource.isEnabled = true

                finish()

            }

        }
        resourceViewModel.errorMessage.observe(this) { error ->
            binding.loadingOverlay.visibility = View.GONE
            binding.progressUpload.visibility = View.GONE
            binding.btnSaveResource.isEnabled = true

            android.util.Log.e("CREATE_RESOURCE", error)

            Toast.makeText(
                this,
                error,
                Toast.LENGTH_LONG
            ).show()

        }
        uploadViewModel.error.observe(this){
            binding.loadingOverlay.visibility = View.GONE
            binding.progressUpload.visibility = View.GONE
            binding.btnSaveResource.isEnabled = true
            Toast.makeText(
                this,
                it,
                Toast.LENGTH_LONG
            ).show()

        }
        resourceViewModel.selectedResource.observe(this){

                resource->

            binding.etResourceName.setText(
                resource.name
            )

            binding.actCategory.setText(
                resource.category,
                false
            )

            binding.etDescription.setText(
                resource.description
            )

            binding.etLocation.setText(
                resource.location
            )

            binding.etBookingOpen.setText(
                resource.bookingOpenBeforeHours.toString()
            )

            binding.etBookingDuration.setText(
                resource.bookingWindowDurationHours.toString()
            )

            if(
                resource.resourceType==
                "CAPACITY_BASED"
            ){

                binding.etCapacityUnits.setText(
                    resource.capacity.toString()
                )

            }

            else{

                binding.etCapacityUnits.setText(
                    resource.availableUnits.toString()
                )

            }

            val placeholder = when (resource.category) {

                "Meeting Room" -> R.drawable.meeting

                "Laboratory Equipment" -> R.drawable.lab

                "Projector" -> R.drawable.projector

                "Sports Facility" -> R.drawable.sports

                "Study Area" -> R.drawable.study

                else -> R.drawable.meeting
            }

            Glide.with(this)
                .load(resource.imageUrl)
                .placeholder(placeholder)
                .error(placeholder)
                .into(binding.imgResource)

            existingImageUrl = resource.imageUrl

            workingStartTime = resource.workingStartTime

            workingEndTime = resource.workingEndTime

            binding.etWorkingStart.setText(
                workingStartTime
            )

            binding.etWorkingEnd.setText(
                workingEndTime
            )

        }
    }
    private fun uriToMultipart(uri: Uri): MultipartBody.Part {

        val inputStream = contentResolver.openInputStream(uri)!!

        val file = File(
            cacheDir,
            "resource_${System.currentTimeMillis()}.jpg"
        )

        file.outputStream().use { output ->
            inputStream.copyTo(output)
        }

        val requestFile = file.asRequestBody("image/*".toMediaType())

        return MultipartBody.Part.createFormData(
            "image",
            file.name,
            requestFile
        )
    }
    private fun createOrUpdateResource(

        imageUrl: String

    ) {

        val category =
            binding.actCategory.text.toString()

        val resourceType =
            when(category){

                "Meeting Room" -> "CAPACITY_BASED"

                "Study Area" -> "CAPACITY_BASED"

                else -> "QUANTITY_BASED"

            }

        val request =
            CreateResourceRequest(

                name =
                    binding.etResourceName.text.toString(),

                category = category,

                imageUrl = imageUrl,

                description =
                    binding.etDescription.text.toString(),

                location =
                    binding.etLocation.text.toString(),

                resourceType = resourceType,

                capacity =
                    if(resourceType=="CAPACITY_BASED")
                        binding.etCapacityUnits.text.toString().toInt()
                    else
                        0,

                availableUnits =
                    if(resourceType=="QUANTITY_BASED")
                        binding.etCapacityUnits.text.toString().toIntOrNull() ?:0
                    else
                        0,

                bookingOpenBeforeHours =
                    binding.etBookingOpen.text.toString().toIntOrNull() ?:0,

                bookingWindowDurationHours =
                    binding.etBookingDuration.text.toString().toIntOrNull() ?:0,

                workingStartTime =
                    workingStartTime,

                workingEndTime =
                    workingEndTime

            )

        val savedToken = SharedPrefManager(this).getToken()

        if (savedToken == null) {

            Toast.makeText(
                this,
                "Please login again",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val token = "Bearer $savedToken"
        if(mode=="ADD"){
            android.util.Log.d("CREATE_RESOURCE", request.toString())

            resourceViewModel.createResource(
                token,
                request
            )

        }

        else{
            android.util.Log.d("UPDATE_RESOURCE", request.toString())

            resourceViewModel.updateResource(
                token,
                resourceId,
                request
            )

        }

    }
    private fun showTimePicker(isStart: Boolean) {

        val picker = TimePickerDialog(
            this,
            { _, hour, minute ->

                val time = String.format(
                    Locale.getDefault(),
                    "%02d:%02d",
                    hour,
                    minute
                )

                if (isStart) {

                    workingStartTime = time

                    binding.etWorkingStart.setText(time)

                } else {

                    workingEndTime = time

                    binding.etWorkingEnd.setText(time)

                }

            },
            8,
            0,
            true
        )

        picker.show()

    }
    private fun validateInputs(): Boolean {

        if (binding.etResourceName.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter resource name", Toast.LENGTH_SHORT).show()
            return false
        }

        if (binding.actCategory.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show()
            return false
        }

        if (binding.etLocation.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter location", Toast.LENGTH_SHORT).show()
            return false
        }

        if (binding.etCapacityUnits.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter capacity/available units", Toast.LENGTH_SHORT).show()
            return false
        }

        if (binding.etBookingOpen.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter booking open time", Toast.LENGTH_SHORT).show()
            return false
        }

        if (binding.etBookingDuration.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter booking window duration", Toast.LENGTH_SHORT).show()
            return false
        }

        if (binding.etWorkingStart.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "Please select working start time", Toast.LENGTH_SHORT).show()
            return false
        }

        if (binding.etWorkingEnd.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "Please select working end time", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }
}