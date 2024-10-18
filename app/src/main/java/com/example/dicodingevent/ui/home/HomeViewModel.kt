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

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: MutableLiveData<String?> = _errorMessage

    fun getActiveEvents() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response: Response<EventResponse> = ApiConfig.getApiServices().getHomeActiveEvent()
                when {
                    response.isSuccessful -> {
                        _events.value = response.body()?.listEvents ?: listOf()
                    }
                    else -> {
                        _errorMessage.value = "Failed to fetch events: ${response.message()}"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getFinishedEvents() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response: Response<EventResponse> = ApiConfig.getApiServices().getFinishedEvent()
                when {
                    response.isSuccessful -> {
                        val finishedEventList = response.body()?.listEvents
                        when {
                            !finishedEventList.isNullOrEmpty() -> {
                                _finishedEvents.value = finishedEventList
                            }
                            else -> {
                                _errorMessage.value = "No available."
                            }
                        }
                    }
                    else -> {
                        _errorMessage.value = "Failed finished events: ${response.code()} ${response.message()}"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                Log.e("HomeViewModel", "getFinishedEvents: ${e.localizedMessage}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}

