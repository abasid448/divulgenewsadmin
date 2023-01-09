package com.example.divulgeadmin.repository

import android.app.Application
import android.net.Uri
import android.widget.Toast
import com.example.divulgeadmin.models.ExclusiveNews
import com.example.divulgeadmin.util.Constants
import com.example.divulgeadmin.util.Constants.Companion.EXCLUSIVE_NEWS_IMAGES
import com.example.divulgeadmin.util.Resource
import com.example.divulgeadmin.util.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ExclusiveNewsRepositoryImpl(
    private val app: Application,
) : ExclusiveNewsRepository {
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val exclusiveCollectionRef = Firebase.firestore.collection("exclusive")
    private var storage: FirebaseStorage = FirebaseStorage.getInstance()

    suspend fun saveExclusiveNews(exclusiveNews: ExclusiveNews, result: (UiState<Boolean>) -> Unit) = suspendCoroutine { continuation ->
        exclusiveCollectionRef.add(exclusiveNews).addOnSuccessListener {
            continuation.resume(result.invoke(UiState.Success(true)))
        }.addOnFailureListener {
            continuation.resume(result.invoke(UiState.Failure(it.message)))
        }
    }

    suspend fun updateExclusiveNewsData(exclusiveNews: ExclusiveNews, result: (UiState<Boolean>) -> Unit) {
        suspendCancellableCoroutine { continuation ->
            val updates = mapOf(
                Constants.EX_NEWS_AUTHOR to exclusiveNews.author,
                Constants.EX_NEWS_TITLE to exclusiveNews.title,
                Constants.EX_NEWS_CONTENT to exclusiveNews.content,
                Constants.EX_NEWS_DESCRIPTION to exclusiveNews.description,
                Constants.EX_NEWS_ACTIVE_STATUS to exclusiveNews.activeStatus,
                Constants.EX_NEWS_COUNTRY to exclusiveNews.countryCode,
                Constants.EX_NEWS_SOURCE to exclusiveNews.source,
                Constants.EX_NEWS_URL to exclusiveNews.url,
                Constants.EX_NEWS_URL_TO_IMAGE to exclusiveNews.urlToImage,
            )
            exclusiveCollectionRef.document(exclusiveNews.id!!).update(updates).addOnSuccessListener {
                if (continuation.isActive) continuation.resume(result.invoke(UiState.Success(true)))
            }.addOnFailureListener {
                continuation.resume(result.invoke(UiState.Failure(it.message)))
            }
        }
    }

    fun saveExclusiveNewsImageFirebaseStorage(exclusiveNewsImageUri: Uri, result: (UiState<String>) -> Unit) {
        val reference = storage.reference.child(EXCLUSIVE_NEWS_IMAGES).child(auth.uid.toString())
        reference.putFile(exclusiveNewsImageUri).addOnCompleteListener { profileImageTask ->
            if (profileImageTask.isSuccessful) {
                reference.downloadUrl.addOnSuccessListener {
                    result.invoke(UiState.Success(it.toString()))
                }
            } else {
                Toast.makeText(app, profileImageTask.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            result.invoke(UiState.Failure("Something went wrong, couldn't update user."))
        }
    }

    suspend fun getExclusiveNews(): Resource<List<ExclusiveNews>> = suspendCancellableCoroutine { continuation ->
        exclusiveCollectionRef.whereEqualTo(Constants.EX_NEWS_CREATED_BY, auth.uid.toString()).get().addOnSuccessListener {
            try {
                val exclusiveNewsList = mutableListOf<ExclusiveNews>()
                it.documents.forEach { document ->
                    val exclusiveNews = document.toObject(ExclusiveNews::class.java)?.apply {
                        id = document.id
                    }
                    exclusiveNewsList.add(exclusiveNews!!)
                }
                continuation.resume(Resource.Success(exclusiveNewsList))
            } catch (e: Exception) {
                continuation.resume(Resource.Error(e.message!!, null))
            }
        }.addOnFailureListener {
            continuation.resume(Resource.Error(it.message!!, null))
        }
    }
}