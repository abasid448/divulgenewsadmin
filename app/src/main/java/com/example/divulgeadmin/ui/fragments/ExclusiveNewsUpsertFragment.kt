package com.example.divulgeadmin.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.divulgeadmin.R
import com.example.divulgeadmin.Session
import com.example.divulgeadmin.adapters.CountriesSpinnerAdapter
import com.example.divulgeadmin.databinding.FragmentExclusiveNewsUpsertBinding
import com.example.divulgeadmin.models.Country
import com.example.divulgeadmin.models.ExclusiveNews
import com.example.divulgeadmin.ui.ExclusiveNewsActivity
import com.example.divulgeadmin.ui.dialogues.CustomRoundProgressBar
import com.example.divulgeadmin.ui.viewmodel.ExclusiveNewsViewModel
import com.example.divulgeadmin.util.Constants
import com.example.divulgeadmin.util.Constants.Companion.DEFAULT_EXCLUSIVE_NEWS_SOURCE
import com.example.divulgeadmin.util.Constants.Companion.FORM_SUBMIT_MODE_ADD
import com.example.divulgeadmin.util.Constants.Companion.FORM_SUBMIT_MODE_UPDATE
import com.example.divulgeadmin.util.Constants.Companion.IS_DEBUG
import com.example.divulgeadmin.util.Constants.Companion.USER_DEFAULT_COUNTRY_VALUE
import com.example.divulgeadmin.util.PublicFunctions
import com.example.divulgeadmin.util.UiState

class ExclusiveNewsUpsertFragment : Fragment(R.layout.fragment_exclusive_news_upsert) {
    private lateinit var binding: FragmentExclusiveNewsUpsertBinding
    private lateinit var viewModel: ExclusiveNewsViewModel
    private lateinit var spinnerAdapter: CountriesSpinnerAdapter
    private lateinit var selectedCountry: Country
    private var exclusiveNewsImageUri: Uri? = null
    private lateinit var formSubmitMode: String
    private lateinit var customRoundProgressBar: CustomRoundProgressBar
    private lateinit var exclusiveNews: ExclusiveNews
    private val args: ExclusiveNewsUpsertFragmentArgs by navArgs()

    companion object {
        const val IMAGE_REQUEST_CODE = 100
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExclusiveNewsUpsertBinding.inflate(
            layoutInflater
        )
        return binding.root
    }

    override fun onViewCreated(
        view: View, savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        initializeComponents()
        if (IS_DEBUG) {
            dummyDataExclusiveNews()
        }
    }

    private fun initializeComponents() {
        viewModel = (activity as ExclusiveNewsActivity).viewModel

        createCountrySpinner()

        // Get the clicked or current exclusive news passed from the other fragment.
        formSubmitMode = args.formSubmitMode
        when (formSubmitMode) {
            FORM_SUBMIT_MODE_ADD -> {}
            FORM_SUBMIT_MODE_UPDATE -> {
                exclusiveNews = args.exclusiveNews!!
                setExclusiveNewsToForms(exclusiveNews)
            }
        }

        customRoundProgressBar = CustomRoundProgressBar(activity as ExclusiveNewsActivity)

        // OnClick listeners
        binding.ivExclusiveNewsImage.setOnClickListener {
            // Create an intent to open the gallery
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(
                intent, IMAGE_REQUEST_CODE
            )
        }

        binding.btnSaveExclusiveNews.setOnClickListener {
            exclusiveNews = getExclusiveNewsDataFromForms()

            if (formValidation(exclusiveNews)) {
                customRoundProgressBar.setCancelable(false)
                customRoundProgressBar.show()
                when (formSubmitMode) {
                    FORM_SUBMIT_MODE_ADD -> {
                        if (exclusiveNewsImageUri != null) {
                            saveExclusiveNewsWithImageFirebaseStorage(exclusiveNews)
                        } else {
                            Toast.makeText(activity, "Image required", Toast.LENGTH_SHORT).show()
                            customRoundProgressBar.dismiss()
                        }
                    }
                    FORM_SUBMIT_MODE_UPDATE -> {
                        if (exclusiveNewsImageUri != null) {
                            saveExclusiveNewsWithImageFirebaseStorage(exclusiveNews)
                        } else {
                            updateExclusiveNewsDataFireStore(exclusiveNews)
                        }
                    }
                }
            } else {
                Toast.makeText(activity, "Form validation failed, please complete required fields.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createCountrySpinner() {
        // Set up countries spinner.
        spinnerAdapter = CountriesSpinnerAdapter(
            activity as ExclusiveNewsActivity, Constants.COUNTRIES
        )
        binding.spinnerCountries.adapter = spinnerAdapter
        binding.spinnerCountries.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                selectedCountry = parent?.getItemAtPosition(position) as Country
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setExclusiveNewsToForms(exclusiveNews: ExclusiveNews) {
        // Set spinner item position.
        val adapter = binding.spinnerCountries.adapter as CountriesSpinnerAdapter
        val position = adapter.getPosition(getCountryCodePosition(exclusiveNews.countryCode ?: USER_DEFAULT_COUNTRY_VALUE))
        binding.spinnerCountries.setSelection(position)
        exclusiveNews.apply {
            binding.etAuthor.setText(author)
            binding.etTitle.setText(title)
            binding.etContent.setText(content)
            binding.etDescription.setText(description)
            binding.switchIsNewsActive.isChecked = activeStatus ?: true
            binding.etNewsUrl.setText(url)
        }
    }

    private fun getCountryCodePosition(countryCode: String): Country {
        for (country in Constants.COUNTRIES) {
            if (country.value == countryCode.trim()) {
                return country
            }
        }
        return Constants.COUNTRIES[0]
    }

    private fun formValidation(
        exclusiveNews: ExclusiveNews
    ): Boolean {
        if (exclusiveNews.author == "" || exclusiveNews.title == "" || exclusiveNews.content == "" || exclusiveNews.description == "" || exclusiveNews.publishedAt == "" || exclusiveNews.countryCode == "" || exclusiveNews.source == "" || exclusiveNews.url == "" || exclusiveNews.createdBy == "") {
            return false
        }
        return true
    }

    private fun getExclusiveNewsDataFromForms(): ExclusiveNews {
        val position = binding.spinnerCountries.selectedItemPosition
        val selectedItem = binding.spinnerCountries.adapter.getItem(
            position
        ) as Country

        val author = binding.etAuthor.text.toString().trim()
        val title = binding.etTitle.text.toString().trim()
        val content = binding.etContent.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val publishedAt = PublicFunctions.getCurrentDateTime()
        val activeStatus = binding.switchIsNewsActive.isChecked
        val country = selectedItem.value
        val source = DEFAULT_EXCLUSIVE_NEWS_SOURCE
        val url = binding.etNewsUrl.text.toString().trim()
        val createdBy = Session.user.uid

        when (formSubmitMode) {
            FORM_SUBMIT_MODE_ADD -> {
                return ExclusiveNews(
                    null, author, title, content, description, publishedAt, createdBy, activeStatus, country, source, url, null
                )
            }
            else -> {
                return exclusiveNews.apply {
                    this.author = author
                    this.title = title
                    this.content = content
                    this.description = description
                    this.activeStatus = activeStatus
                    this.countryCode = country
                    this.url = url
                }
            }
        }
    }

    private fun saveExclusiveNewsWithImageFirebaseStorage(exclusiveNews: ExclusiveNews) {
        viewModel.saveExclusiveNewsImageFirebaseStorage(exclusiveNewsImageUri!!) {
            when (it) {
                is UiState.Failure -> {}
                is UiState.Loading -> {}
                is UiState.Success -> {
                    exclusiveNews.urlToImage = it.data

                    when (formSubmitMode) {
                        FORM_SUBMIT_MODE_ADD -> {
                            saveExclusiveNewsDataFireStore(exclusiveNews)
                            customRoundProgressBar.dismiss()
                        }
                        FORM_SUBMIT_MODE_UPDATE -> {
                            updateExclusiveNewsDataFireStore(exclusiveNews)
                            customRoundProgressBar.dismiss()
                        }
                    }
                }
            }
        }
    }

    private fun saveExclusiveNewsDataFireStore(exclusiveNews: ExclusiveNews) {
        viewModel.saveExclusiveNews(exclusiveNews) {
            when (it) {
                is UiState.Failure -> {
                    Toast.makeText(activity, "Couldn't not save data, please try again.", Toast.LENGTH_SHORT).show()
                }
                is UiState.Loading -> {

                }
                is UiState.Success -> {
                    customRoundProgressBar.dismiss()
                    Toast.makeText(activity, "New Exclusive news saved", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateExclusiveNewsDataFireStore(exclusiveNews: ExclusiveNews) {
        viewModel.updateExclusiveNewsData(exclusiveNews) {
            when (it) {
                is UiState.Failure -> {
                    customRoundProgressBar.dismiss()
                    Toast.makeText(activity, "Something went wrong, couldn't complete update", Toast.LENGTH_SHORT).show()
                }
                is UiState.Loading -> {}
                is UiState.Success -> {
                    customRoundProgressBar.dismiss()
                    Toast.makeText(activity, "Exclusive News updated", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun dummyDataExclusiveNews() {
        binding.etAuthor.setText("ESPN France")
        binding.etTitle.setText("Crypto Exchange OKX to Publish Proof of Reserves Every Month")
        binding.etDescription.setText("The world’s second-largest crypto exchange by trading volume, OKX, has published its second Proof-of-Reserves (PoR) on its website. As part of its dedication to transparency, OKX will release its PoR on the 22nd of every month. Using open-source software.")
        binding.etNewsUrl.setText("https://biztoc.com/x/d88515d30111ecbc")
        binding.etContent.setText("The worlds second-largest crypto exchange by trading volume, OKX, has published its second Proof-of-Reserves (PoR) on its website. As part of its dedication to transparency, OKX will release its PoR.")
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(
        requestCode: Int, resultCode: Int, data: Intent?
    ) {
        super.onActivityResult(
            requestCode, resultCode, data
        )
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            exclusiveNewsImageUri = data?.data!!
            // Get image bitmap and reduce the size of image.
            val bitmapStudentImage = PublicFunctions.resizeBitmap(
                MediaStore.Images.Media.getBitmap(
                    activity?.contentResolver, exclusiveNewsImageUri
                )
            )
            binding.ivExclusiveNewsImage.setImageBitmap(
                bitmapStudentImage
            )
        }
    }
}