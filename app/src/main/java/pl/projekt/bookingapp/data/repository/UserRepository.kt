package pl.projekt.bookingapp.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import pl.projekt.bookingapp.data.model.User
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    suspend fun getCurrentUser(): User? {
        val uid = auth.currentUser?.uid ?: return null
        val snapshot = firestore.collection("users").document(uid).get().await()
        return snapshot.toObject(User::class.java)?.copy(uid = uid)
    }

    suspend fun updateUserProfile(user: User) {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("users").document(uid).set(user).await()
    }

    fun logout() {
        auth.signOut()
    }
}
