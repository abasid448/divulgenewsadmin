package com.example.divulgeadmin.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.divulgeadmin.databinding.ActivityExclusiveNewsBinding
import com.example.divulgeadmin.repository.ExclusiveNewsRepositoryImpl
import com.example.divulgeadmin.ui.viewmodel.ExclusiveNewsViewModel
import com.example.divulgeadmin.ui.viewmodel.ExclusiveNewsViewModelProviderFactory

class ExclusiveNewsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExclusiveNewsBinding
    lateinit var viewModel: ExclusiveNewsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExclusiveNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeComponents()
    }

    private fun initializeComponents() {
        // Instantiate the news repository
        val exclusiveNewsRepository = ExclusiveNewsRepositoryImpl(application)
        val viewModelProviderFactory = ExclusiveNewsViewModelProviderFactory(application, exclusiveNewsRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory)[ExclusiveNewsViewModel::class.java]
    }
}