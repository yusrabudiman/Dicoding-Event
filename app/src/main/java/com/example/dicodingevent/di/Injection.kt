package com.example.dicodingevent.di

import android.content.Context
import android.util.Log
import com.example.dicodingevent.data.local.SettingPreferences
import com.example.dicodingevent.data.local.database.AppDatabaseRoomEvent
import com.example.dicodingevent.data.repository.EventRepository
import com.example.dicodingevent.data.retrofit.ApiConfig

object Injection {
    fun provideEventRepository(context: Context): EventRepository {
        val apiServices = ApiConfig.getApiServices()
        val database = AppDatabaseRoomEvent.getDatabaseInstance(context)
        val favoriteEventDao = database.favoriteEventDao()
        return EventRepository(apiServices, favoriteEventDao).also {
            Log.d("Injection", "Repository Created Successfully")
        }
    }
    fun provideSettingPreferences(context: Context): SettingPreferences {
        return SettingPreferences.provideSettingPreferences(context)
    }
}
