package com.example.divulgeadmin.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.divulgeadmin.repository.UserRepositoryImpl
import com.google.firebase.auth.FirebaseUser

class UserViewModel(
    app: Application,
) : AndroidViewModel(app) {

    private var repository: UserRepositoryImpl
    var userMutableData: MutableLiveData<FirebaseUser>
    var loggedMutableStatus: MutableLiveData<Boolean>
    var verificationStatus = MutableLiveData<Boolean>()
    var phoneNumber = ""
    var verificationId: String = ""

    init {
        repository = UserRepositoryImpl(app)
        userMutableData = repository.firebaseUserMutableLiveData
        loggedMutableStatus = repository.userLoggedStatusMutableLiveData
    }

}