// app/src/main/java/pl/projekt/bookingapp/data/repository/ServiceRepository.kt
package pl.projekt.bookingapp.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import pl.projekt.bookingapp.data.model.Service
import javax.inject.Inject

interface ServiceRepository {
    suspend fun getServicesForBusiness(businessId: String): Result<List<Service>>
}

class ServiceRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ServiceRepository {
    override suspend fun getServicesForBusiness(businessId: String): Result<List<Service>> {
        return try {
            // we now point at the sub-collection under the business document:
            val snapshot = firestore
                .collection("businesses")
                .document(businessId)
                .collection("services")
                .get()
                .await()
            // map each DocumentSnapshot to Service (and copy its ID if you like)
            val list = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Service::class.java)
                    ?.copy(id = doc.id)
            }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
