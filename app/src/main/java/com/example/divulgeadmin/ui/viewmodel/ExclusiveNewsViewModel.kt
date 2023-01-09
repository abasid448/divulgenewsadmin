package com.example.divulgeadmin.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.divulgeadmin.models.ExclusiveNews
import com.example.divulgeadmin.repository.ExclusiveNewsRepositoryImpl
import com.example.divulgeadmin.util.UiState
import kotlinx.coroutines.launch

class ExclusiveNewsViewModel(
    app: Application,
    private val repository: ExclusiveNewsRepositoryImpl
) : AndroidViewModel(app) {

    private val _exclusiveNewsFromFireStore = MutableLiveData<List<ExclusiveNews>>()
    val exclusiveNewsFromFireStore: LiveData<List<ExclusiveNews>>
        get() = _exclusiveNewsFromFireStore

    fun saveExclusiveNews(exclusiveNews: ExclusiveNews, result: (UiState<Boolean>) -> Unit) = viewModelScope.launch {
        repository.saveExclusiveNews(exclusiveNews, result)
    }

    fun saveExclusiveNewsImageFirebaseStorage(exclusiveNewsImageUri: Uri, result: (UiState<String>) -> Unit) =
        repository.saveExclusiveNewsImageFirebaseStorage(exclusiveNewsImageUri, result)

    fun updateExclusiveNewsData(exclusiveNews: ExclusiveNews, result: (UiState<Boolean>) -> Unit) = viewModelScope.launch {
        repository.updateExclusiveNewsData(exclusiveNews, result)
    }

    fun getExclusiveNews() = viewModelScope.launch {
        _exclusiveNewsFromFireStore.value = repository.getExclusiveNews().data!!
    }
}