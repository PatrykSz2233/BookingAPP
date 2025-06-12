// app/src/main/java/pl/projekt/bookingapp/data/repository/BusinessRepository.kt
package pl.projekt.bookingapp.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import pl.projekt.bookingapp.data.model.Business
import javax.inject.Inject

interface BusinessRepository {
    suspend fun getNearbyBusinesses(): Result<List<Business>>
}

class BusinessRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : BusinessRepository {
    override suspend fun getNearbyBusinesses(): Result<List<Business>> {
        return try {
            val snapshot = firestore
                .collection("businesses")
                .get()
                .await()
            // mapujemy każdy dokument na Business, kopiując its id → uid
            val businesses = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Business::class.java)
                    ?.copy(uid = doc.id)
            }
            Result.success(businesses)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
