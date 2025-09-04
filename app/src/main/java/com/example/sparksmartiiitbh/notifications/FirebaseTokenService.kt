package com.example.sparksmartiiitbh.notifications

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseTokenService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance().collection("users").document(uid)
            .update(mapOf("fcmToken" to token))
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        // TODO: Show notification if needed
    }
}


