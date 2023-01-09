package com.example.divulgeadmin.repository

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.example.divulgeadmin.models.User
import com.example.divulgeadmin.util.Constants
import com.example.divulgeadmin.util.Constants.Companion.UID
import com.example.divulgeadmin.util.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepositoryImpl(private val app: Application) {
    val firebaseUserMutableLiveData: MutableLiveData<FirebaseUser> = MutableLiveData()
    val userLoggedStatusMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var storage: FirebaseStorage = FirebaseStorage.getInstance()
    private val userCollectionRef = Firebase.firestore.collection(Constants.USERS)

    suspend fun getUserFromFireStore(uid: String, result: (UiState<User>) -> Unit) {
        withContext(Dispatchers.IO) {
            val query = userCollectionRef.whereEqualTo(UID, uid).limit(1)
            query.get().addOnCompleteListener {
                if (it.isSuccessful) {
                    try {
                        val document = it.result.documents.first().data
                        val countryCode = document?.get(Constants.COUNTRY_CODE).toString()
                        val email = document?.get(Constants.EMAIL).toString()
                        val fullName = document?.get(Constants.FULL_NAME).toString()
                        val isUser = document?.get(Constants.USER_ROLE) as Boolean
                        val user = User(uid, fullName, email, countryCode, isUser)
                        result.invoke(UiState.Success(user))
                    } catch (e: NoSuchElementException) {
                        result.invoke(UiState.Failure(e.message))
                    }
                }
            }.addOnFailureListener {
                result.invoke(UiState.Failure(it.message))
            }
        }
    }
}
