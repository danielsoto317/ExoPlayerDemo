package com.dsv.basemvvm.framework.ui.main

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    private val _progressVisible = MutableLiveData<Boolean>()
    val progressVisible: LiveData<Boolean> get() = _progressVisible

    private val _showMessage = MutableLiveData<String>()
    val showMessage: LiveData<String> get() = _showMessage

    fun onCreate() {
        viewModelScope.launch {
            _progressVisible.value = true
            _progressVisible.value = false
        }
    }

}