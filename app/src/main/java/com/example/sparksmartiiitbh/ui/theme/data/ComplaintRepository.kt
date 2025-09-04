package com.example.sparksmartiiitbh.ui.theme.data

import com.example.sparksmartiiitbh.ui.theme.Model.Complaint
import com.example.sparksmartiiitbh.ui.theme.Model.ComplaintStatus
import com.google.firebase.Timestamp
import com.example.sparksmartiiitbh.ui.theme.Model.UserBrief
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ComplaintRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val complaints = firestore.collection("complaints")

    suspend fun createComplaint(complaint: Complaint): String {
        val doc = if (complaint.id.isBlank()) complaints.document() else complaints.document(complaint.id)
        val id = doc.id
        val now = Timestamp.now()
        val toSave = complaint.copy(id = id, createdAt = now, updatedAt = now, userId = complaint.userId.ifBlank { auth.currentUser?.uid ?: "" })
        doc.set(toSave).await()
        return id
    }

    suspend fun getComplaintsForCurrentUser(): List<Complaint> {
        val uid = auth.currentUser?.uid ?: return emptyList()
        // Avoid composite index requirement by fetching without orderBy and sorting client-side
        return complaints.whereEqualTo("userId", uid).get().await().toObjects(Complaint::class.java)
            .sortedByDescending { it.createdAt.toDate().time }
    }

    suspend fun getComplaintsAssignedToWorker(): List<Complaint> {
        val uid = auth.currentUser?.uid ?: return emptyList()
        return complaints.whereEqualTo("assignedWorkerId", uid).get().await().toObjects(Complaint::class.java)
    }

    suspend fun getAllComplaints(): List<Complaint> {
        return complaints.get().await().toObjects(Complaint::class.java)
            .sortedByDescending { it.createdAt.toDate().time }
    }

    suspend fun assignComplaint(complaintId: String, workerId: String) {
        complaints.document(complaintId).update(
            mapOf(
                "assignedWorkerId" to workerId,
                "updatedAt" to Timestamp.now(),
                "status" to ComplaintStatus.Accepted
            )
        ).await()
    }

    suspend fun updateStatus(complaintId: String, status: ComplaintStatus) {
        complaints.document(complaintId).update(
            mapOf(
                "status" to status,
                "updatedAt" to Timestamp.now()
            )
        ).await()
    }

    suspend fun fetchWorkers(): List<UserBrief> {
        val snapshot = firestore.collection("users").whereEqualTo("role", "worker").get().await()
        return snapshot.documents.map { doc ->
            UserBrief(
                id = doc.id,
                email = doc.getString("email"),
                role = doc.getString("role")
            )
        }
    }

    fun listenComplaintsForCurrentUser(onChanged: (List<Complaint>) -> Unit): ListenerRegistration? {
        val uid = auth.currentUser?.uid ?: return null
        return complaints.whereEqualTo("userId", uid)
            .addSnapshotListener { snaps, _ ->
                val list = snaps?.toObjects(Complaint::class.java)?.sortedByDescending { it.createdAt.toDate().time }
                    ?: emptyList()
                onChanged(list)
            }
    }

    fun listenComplaintsAssignedToWorker(onChanged: (List<Complaint>) -> Unit): ListenerRegistration? {
        val uid = auth.currentUser?.uid ?: return null
        return complaints.whereEqualTo("assignedWorkerId", uid)
            .addSnapshotListener { snaps, _ ->
                val list = snaps?.toObjects(Complaint::class.java)?.sortedByDescending { it.createdAt.toDate().time }
                    ?: emptyList()
                onChanged(list)
            }
    }

    fun listenAvailableComplaints(onChanged: (List<Complaint>) -> Unit): ListenerRegistration {
        return complaints
            .whereEqualTo("status", ComplaintStatus.Registered)
            .whereEqualTo("assignedWorkerId", null)
            .addSnapshotListener { snaps, _ ->
                val list = snaps?.toObjects(Complaint::class.java)?.sortedByDescending { it.createdAt.toDate().time }
                    ?: emptyList()
                onChanged(list)
            }
    }

    suspend fun acceptByCurrentWorker(complaintId: String) {
        val uid = auth.currentUser?.uid ?: return
        complaints.document(complaintId).update(
            mapOf(
                "assignedWorkerId" to uid,
                "status" to ComplaintStatus.Accepted,
                "updatedAt" to Timestamp.now()
            )
        ).await()
    }

    fun listenAllComplaints(onChanged: (List<Complaint>) -> Unit): ListenerRegistration {
        return complaints.addSnapshotListener { snaps, _ ->
            val list = snaps?.toObjects(Complaint::class.java)?.sortedByDescending { it.createdAt.toDate().time }
                ?: emptyList()
            onChanged(list)
        }
    }

    suspend fun getComplaintById(id: String): Complaint? {
        val snap = complaints.document(id).get().await()
        return snap.toObject(Complaint::class.java)
    }

    fun listenComplaintById(id: String, onChanged: (Complaint?) -> Unit): ListenerRegistration {
        return complaints.document(id).addSnapshotListener { snap, _ ->
            onChanged(snap?.toObject(Complaint::class.java))
        }
    }
}


