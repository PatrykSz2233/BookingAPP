package pl.projekt.bookingapp.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import pl.projekt.bookingapp.data.model.Booking
import java.time.LocalDate
import java.time.ZoneOffset
import javax.inject.Inject

interface BookingRepository {
    suspend fun createBooking(booking: Booking): Result<Unit>
    suspend fun getBookingsForBusinessServiceDate(
        businessId: String,
        serviceId: String,
        date: LocalDate
    ): Result<List<Booking>>
    suspend fun getBookingsForUser(userId: String): Result<List<Booking>>
}

class BookingRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : BookingRepository {
    override suspend fun createBooking(booking: Booking) = try {
        val uid = auth.currentUser!!.uid
        val ref = firestore.collection("bookings").document()
        ref.set(booking.copy(id = ref.id, clientId = uid)).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getBookingsForBusinessServiceDate(
        businessId: String,
        serviceId: String,
        date: LocalDate
    ) = try {
        val start = date.atStartOfDay().toInstant(ZoneOffset.UTC)
        val end   = date.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC)
        val snap = firestore.collection("bookings")
            .whereEqualTo("businessId", businessId)
            .whereEqualTo("serviceId", serviceId)
            .whereGreaterThanOrEqualTo("bookingTime", start)
            .whereLessThan("bookingTime", end)
            .get()
            .await()
        Result.success(snap.toObjects(Booking::class.java))
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getBookingsForUser(userId: String) = try {
        val uid = userId.ifBlank { auth.currentUser!!.uid }
        val snap = firestore.collection("bookings")
            .whereEqualTo("clientId", uid)
            .get()
            .await()
        Result.success(snap.toObjects(Booking::class.java))
    } catch (e: Exception) {
        Result.failure(e)
    }
}
