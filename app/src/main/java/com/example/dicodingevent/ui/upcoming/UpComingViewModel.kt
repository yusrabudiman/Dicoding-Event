package com.example.dicodingevent.ui.upcoming

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingevent.data.response.EventResponse
import com.example.dicodingevent.data.response.ListEventsItem
import com.example.dicodingevent.data.retrofit.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Response

class UpComingViewModel : ViewModel() {

    private val _events = MutableLiveData<List<ListEventsItem>>()
    val events: LiveData<List<ListEventsItem>> = _events

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun getActiveEvents() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response: Response<EventResponse> = ApiConfig.getApiServices().getHomeActiveEvent()
                if (response.isSuccessful) {
                    _events.value = response.body()?.listEvents ?: listOf()
                    clearErrorMessage() // Clear error on successful fetch
                } else {
                    _errorMessage.value = "Failed to fetch events: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}
