package com.example.dicodingevent.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dicodingevent.data.response.ListEventsItem
import androidx.lifecycle.viewModelScope
import com.example.dicodingevent.data.response.EventResponse
import com.example.dicodingevent.data.retrofit.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Response

class HomeViewModel : ViewModel() {
    private val _events = MutableLiveData<List<ListEventsItem>>()
    val events: LiveData<List<ListEventsItem>> = _events

    private val _finishedEvents = MutableLiveData<List<ListEventsItem>?>()
    val finishedEvents: MutableLiveData<List<ListEventsItem>?> = _finishedEvents

    private val _isLoadingUpcoming = MutableLiveData<Boolean>()
    val isLoadingUpcoming: LiveData<Boolean> = _isLoadingUpcoming

    private val _isLoadingFinished = MutableLiveData<Boolean>()
    val isLoadingFinished: LiveData<Boolean> = _isLoadingFinished

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: MutableLiveData<String?> = _errorMessage

    fun getActiveEvents() {
        if (_isLoadingUpcoming.value == true || !_events.value.isNullOrEmpty()) {
            return // Prevent reloading if already loading or data is available
        }

        _isLoadingUpcoming.value = true
        viewModelScope.launch {
            try {
                val response: Response<EventResponse> = ApiConfig.getApiServices().getHomeActiveEvent()
                if (response.isSuccessful) {
                    _events.value = response.body()?.listEvents ?: listOf()
                } else {
                    _errorMessage.value = "Failed to fetch events: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoadingUpcoming.value = false
            }
        }
    }

    fun getFinishedEvents() {
        if (_isLoadingFinished.value == true || !_finishedEvents.value.isNullOrEmpty()) {
            return // Prevent reloading if already loading or data is available
        }

        _isLoadingFinished.value = true
        viewModelScope.launch {
            try {
                val response: Response<EventResponse> = ApiConfig.getApiServices().getFinishedEvent()
                if (response.isSuccessful) {
                    val finishedEventList = response.body()?.listEvents
                    if (!finishedEventList.isNullOrEmpty()) {
                        _finishedEvents.value = finishedEventList
                    } else {
                        _errorMessage.value = "No available."
                    }
                } else {
                    _errorMessage.value = "Failed finished events: ${response.code()} ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                Log.e("HomeViewModel", "getFinishedEvents: ${e.localizedMessage}", e)
            } finally {
                _isLoadingFinished.value = false
            }
        }
    }
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}

