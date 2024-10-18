package com.example.dicodingevent.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingevent.data.response.EventDetailResponse
import com.example.dicodingevent.data.retrofit.ApiConfig
import kotlinx.coroutines.launch

class DetailViewModel : ViewModel() {

    private val _eventDetail = MutableLiveData<EventDetailResponse?>()
    val eventDetail: LiveData<EventDetailResponse?> = _eventDetail

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getEventDetail(eventId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = ApiConfig.getApiServices().getDetailEvent(eventId)
                if (response.isSuccessful) {
                    _eventDetail.value = response.body()
                } else {
                    _errorMessage.value = "Failed: ${response.message()}"
                    Log.e("DetailViewModel", "Error: ${response.message()}")
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
                Log.e("DetailViewModel", "Exception: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

}