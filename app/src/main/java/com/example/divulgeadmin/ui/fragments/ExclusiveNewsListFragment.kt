package com.example.divulgeadmin.ui.fragments

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.divulgeadmin.R
import com.example.divulgeadmin.adapters.NewsRecyclerAdapter
import com.example.divulgeadmin.databinding.FragmentExclusiveNewsListBinding
import com.example.divulgeadmin.ui.ExclusiveNewsActivity
import com.example.divulgeadmin.ui.UserActivity
import com.example.divulgeadmin.ui.viewmodel.ExclusiveNewsViewModel
import com.example.divulgeadmin.util.Constants.Companion.FORM_SUBMIT_MODE
import com.example.divulgeadmin.util.Constants.Companion.FORM_SUBMIT_MODE_ADD
import com.example.divulgeadmin.util.Constants.Companion.FORM_SUBMIT_MODE_UPDATE
import com.example.divulgeadmin.util.Constants.Companion.MODEL_EXCLUSIVE_NEWS
import com.google.firebase.auth.FirebaseAuth

class ExclusiveNewsListFragment : Fragment(R.layout.fragment_exclusive_news_list) {

    private lateinit var binding: FragmentExclusiveNewsListBinding
    private lateinit var viewModel: ExclusiveNewsViewModel
    private lateinit var exclusiveNewsAdapter: NewsRecyclerAdapter
    private lateinit var logoutAlertDialog: AlertDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentExclusiveNewsListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as ExclusiveNewsActivity).viewModel
        setupRecyclerView()

        // Set on item click listener for recycler view.
        exclusiveNewsAdapter.setOnItemClickListener {
            // Get the clicked the article and put into a bundle and attach the bundle to a navigation component.
            val bundle = Bundle().apply {
                putSerializable(MODEL_EXCLUSIVE_NEWS, it)
                putString(FORM_SUBMIT_MODE, FORM_SUBMIT_MODE_UPDATE)
            }
            // Fragment transition.
            findNavController().navigate(
                R.id.action_newsListFragment_to_newsUpsertFragment, bundle
            )
        }

        viewModel.getExclusiveNews()
        viewModel.exclusiveNewsFromFireStore.observe(viewLifecycleOwner, Observer { exclusiveNews ->
            exclusiveNewsAdapter.differ.submitList(exclusiveNews)
        })

        binding.btnAddExclusiveNews.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable(MODEL_EXCLUSIVE_NEWS, null)
                putString(FORM_SUBMIT_MODE, FORM_SUBMIT_MODE_ADD)
            }

            Navigation.findNavController(binding.root).navigate(
                R.id.action_newsListFragment_to_newsUpsertFragment, bundle
            )
        }

        // Initialize logout alert dialog.
        logoutAlertDialog =
            AlertDialog.Builder(activity as ExclusiveNewsActivity).setTitle("Logout").setMessage("Are you sure want to logout?")
                .setIcon(R.drawable.ic_logout).setPositiveButton(
                    R.string.logout, logoutOnClickListener
                ).setNegativeButton(R.string.cancel, null).create()

        binding.btnLogout.setOnClickListener {
            logoutAlertDialog.show()
        }
    }

    private val logoutOnClickListener by lazy {
        DialogInterface.OnClickListener { _, _ ->
            FirebaseAuth.getInstance().signOut()
            // Go to user intent.
            val intent = Intent(activity, UserActivity::class.java)
            startActivity(intent)
            activity?.finishAffinity()
        }
    }

    private fun setupRecyclerView() {
        exclusiveNewsAdapter = NewsRecyclerAdapter()
        binding.rvExclusiveNews.apply {
            adapter = exclusiveNewsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}