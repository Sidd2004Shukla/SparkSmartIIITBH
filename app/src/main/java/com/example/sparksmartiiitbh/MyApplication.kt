package com.example.sparksmartiiitbh

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            FirebaseApp.initializeApp(this)
            Log.d("FirebaseInit", "Firebase initialized successfully")
        } catch (e: Exception) {
            Log.e("FirebaseInit", "Failed to initialize Firebase", e)
        }
    }
}
