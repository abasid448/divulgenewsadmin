package com.example.divulgeadmin.ui.fragments

import android.content.Context
import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.divulgeadmin.R
import com.example.divulgeadmin.Session
import com.example.divulgeadmin.databinding.FragmentVerifyOtpBinding
import com.example.divulgeadmin.models.User
import com.example.divulgeadmin.repository.UserRepositoryImpl
import com.example.divulgeadmin.ui.ExclusiveNewsActivity
import com.example.divulgeadmin.ui.UserActivity
import com.example.divulgeadmin.ui.viewmodel.UserViewModel
import com.example.divulgeadmin.util.Constants
import com.example.divulgeadmin.util.Constants.Companion.IS_DEBUG
import com.example.divulgeadmin.util.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VerifyOtpFragment : Fragment(R.layout.fragment_verify_otp) {
    private lateinit var binding: FragmentVerifyOtpBinding
    private lateinit var viewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVerifyOtpBinding.inflate(layoutInflater)
        initializeComponents()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val inputMethodManager =
            (activity as UserActivity).getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(
            InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY
        )
        binding.otpView.requestFocus()
    }

    private fun initializeComponents() {
        // Initialize view model from activity.
        viewModel = (activity as UserActivity).viewModel

        // Set Mobile Number on the text view.
        binding.tvPhoneNumber.text = viewModel.phoneNumber

        binding.tvChangePhoneNumber.setOnClickListener {
            Toast.makeText(activity, "OTP sent successfully", Toast.LENGTH_SHORT).show()
        }

        binding.tvChangePhoneNumber.setOnClickListener {
            // Go back to the mobile number fragment.
            findNavController().navigateUp()
        }

        binding.btnVerifyOtp.setOnClickListener(btnVerifyOtpOnClickListener)
    }

    private val auth = FirebaseAuth.getInstance()

    private val btnVerifyOtpOnClickListener = View.OnClickListener {
        val code = binding.otpView.text.toString().trim()

        // Set progress bar visible.
        binding.progressBar.visibility = View.VISIBLE
        binding.btnVerifyOtp.visibility = View.INVISIBLE

        if (code.length >= 6) {
            if (viewModel.verificationId != "") {
                val credential = PhoneAuthProvider.getCredential(viewModel.verificationId, code)
                auth.signInWithCredential(credential).addOnCompleteListener {
                    if (it.isSuccessful) {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.btnVerifyOtp.visibility = View.INVISIBLE
                        // Hide the keyboard after verifying otp.
                        hideKeyboard(activity as UserActivity, binding.otpView)
                        getUserDataToSessionAndGotoNews(auth.uid.toString())
                    } else {
                        binding.progressBar.visibility = View.GONE
                        binding.btnVerifyOtp.visibility = View.VISIBLE
                        Toast.makeText(activity, "Invalid OTP", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            Toast.makeText(activity, "Invalid OTP", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getUserDataToSessionAndGotoNews(uid: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val repository = UserRepositoryImpl(requireActivity().application)
            repository.getUserFromFireStore(uid) {
                when (it) {
                    is UiState.Failure -> {
                        Session.user = User(
                            "",
                            "",
                            "",
                            Constants.USER_DEFAULT_COUNTRY_VALUE,
                            Constants.IS_USER_DEFAULT_VALUE
                        )
                        binding.progressBar.visibility = View.GONE
                        binding.btnVerifyOtp.visibility = View.VISIBLE

                        if (IS_DEBUG) {
                            Toast.makeText(activity, "Failure", Toast.LENGTH_SHORT).show()
                        }
                    }
                    is UiState.Loading -> {}
                    is UiState.Success -> {
                        Session.user = it.data
                        // Check for admin role.
                        if (!Session.user.userRole) {
                            startActivity(
                                Intent(activity, ExclusiveNewsActivity::class.java)
                            )
                        } else {
                            binding.progressBar.visibility = View.GONE
                            binding.btnVerifyOtp.visibility = View.VISIBLE
                            Toast.makeText(
                                activity,
                                "You don't have permission to access",
                                Toast.LENGTH_SHORT
                            ).show()
                            // Go back to the mobile number fragment.
                            findNavController().navigateUp()
                        }
                    }
                }
            }
        }
    }

    private fun hideKeyboard(context: Context, view: View) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}