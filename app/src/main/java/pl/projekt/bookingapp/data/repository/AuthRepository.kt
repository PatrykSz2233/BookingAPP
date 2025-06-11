package pl.projekt.bookingapp.data.repository

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import pl.projekt.bookingapp.data.model.User
import javax.inject.Inject

interface AuthRepository {
    suspend fun signIn(email: String, pass: String): Result<AuthResult>
    suspend fun signUp(email: String, pass: String, name: String, userType: String): Result<Unit>
    fun getCurrentUserId(): String?
    fun isUserLoggedIn(): Boolean // <-- TA METODA BYŁA POTRZEBNA
    fun logout()
}

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {
    override suspend fun signIn(email: String, pass: String): Result<AuthResult> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, pass).await()
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override fun logout() {
        auth.signOut()
    }

    override suspend fun signUp(email: String, pass: String, name: String, userType: String): Result<Unit> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, pass).await()
            val uid = authResult.user?.uid ?: throw Exception("User creation failed")
            val user = User(uid, email, name, userType)
            firestore.collection("users").document(uid).set(user).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCurrentUserId(): String? = auth.currentUser?.uid

    override fun isUserLoggedIn(): Boolean = auth.currentUser != null // <-- ORAZ JEJ IMPLEMENTACJA
}