package com.example.divulgeadmin.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.divulgeadmin.R
import com.example.divulgeadmin.Session
import com.example.divulgeadmin.models.User
import com.example.divulgeadmin.repository.UserRepositoryImpl
import com.example.divulgeadmin.util.Constants.Companion.IS_USER_DEFAULT_VALUE
import com.example.divulgeadmin.util.Constants.Companion.USER_DEFAULT_COUNTRY_VALUE
import com.example.divulgeadmin.util.UiState
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SplashScreenActivity : AppCompatActivity() {
    private val auth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler(Looper.getMainLooper()).postDelayed({
            //Open the next activity.
            val intent: Intent
            if (auth.currentUser == null) {
                startActivity(Intent(this@SplashScreenActivity, UserActivity::class.java))
                finish()
            } else {
                getUserDataToSessionAndGotoNews(auth.uid.toString())
            }
        }, 1000)
    }

    private fun getUserDataToSessionAndGotoNews(uid: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val repository = UserRepositoryImpl(application)
            repository.getUserFromFireStore(uid) {
                when (it) {
                    is UiState.Failure -> {
                        Session.user = User("", "", "", USER_DEFAULT_COUNTRY_VALUE, IS_USER_DEFAULT_VALUE)
                        startActivity(
                            Intent(this@SplashScreenActivity, UserActivity::class.java)
                        )
                        finish()
                    }
                    is UiState.Loading -> {}
                    is UiState.Success -> {
                        Session.user = it.data
                        // Check for admin role.
                        if (!Session.user.userRole) {
                            startActivity(
                                Intent(this@SplashScreenActivity, ExclusiveNewsActivity::class.java)
                            )
                            finish()
                        } else {
                            auth.signOut()
                            startActivity(
                                Intent(this@SplashScreenActivity, UserActivity::class.java)
                            )
                            finish()
                        }
                    }
                }
            }
        }
    }
}