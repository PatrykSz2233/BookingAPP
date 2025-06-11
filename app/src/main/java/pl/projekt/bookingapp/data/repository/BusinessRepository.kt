package pl.projekt.bookingapp.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import pl.projekt.bookingapp.data.model.Business // <-- TA LINIA MUSI TU BYĆ
import javax.inject.Inject

interface BusinessRepository {
    suspend fun getNearbyBusinesses(): Result<List<Business>>
}

class BusinessRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : BusinessRepository {
    override suspend fun getNearbyBusinesses(): Result<List<Business>> {
        return try {
            val businesses = firestore.collection("businesses")
                .get().await().toObjects(Business::class.java)
            Result.success(businesses)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}