package com.example.dicodingevent.ui.eventfinished

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingevent.data.response.ListEventsItem
import com.example.dicodingevent.data.response.EventResponse
import com.example.dicodingevent.data.retrofit.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Response

class FinishedViewModel : ViewModel() {
    private val _finishedEvents = MutableLiveData<List<ListEventsItem>?>()
    val finishedEvents: LiveData<List<ListEventsItem>?> = _finishedEvents

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: MutableLiveData<String?> = _errorMessage

    fun getFinishedEvent() {
        if (_isLoading.value == true || !_finishedEvents.value.isNullOrEmpty()) {
            return
        }
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response: Response<EventResponse> =
                    ApiConfig.getApiServices().getFinishedEvent()
                if (response.isSuccessful) {
                    val finishedEventList = response.body()?.listEvents
                    if (!finishedEventList.isNullOrEmpty()) {
                        _finishedEvents.value = finishedEventList
                    } else {
                        _errorMessage.value = "No available."
                    }
                } else {
                    _errorMessage.value =
                        "Failed finished events: ${response.code()} ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                Log.e("FinishedViewModel", "getFinishedEvents: ${e.localizedMessage}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun searchFinishedEvents(query: String): LiveData<List<ListEventsItem>?> {
        val filteredEvents = MutableLiveData<List<ListEventsItem>?>()
        _finishedEvents.value?.let { events ->
            filteredEvents.value = events.filter { event ->
                event.name?.contains(query, ignoreCase = true) ?: false
            }
        }
        return filteredEvents
    }
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}
