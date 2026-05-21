package com.caretail.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Firebase Repository - Complete backend integration
 * Handles all authentication and database operations for CareTail app
 */
class FirebaseRepository {
    
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: DatabaseReference = FirebaseDatabase.getInstance(
        ""
    ).reference


    companion object {
        private const val TAG = "FirebaseRepository"
        
        // Database paths
        private const val USERS_PATH = "users"
        private const val PETS_PATH = "pets"
        private const val BOOKINGS_PATH = "bookings"
        private const val REPORTS_PATH = "daily_reports"
        private const val CHECKLISTS_PATH = "checklists"
        private const val REVIEWS_PATH = "reviews"
        private const val CHATS_PATH = "chats"
        private const val NOTIFICATIONS_PATH = "notifications"
        private const val PAYMENTS_PATH = "payments"
    }
    
    // ==================== AUTHENTICATION ====================
    
    /**
     * Register a new user with email and password
     */
    suspend fun registerUser(
        email: String,
        password: String,
        name: String,
        phone: String,
        dob: String,
        role: Role,
        address: String = "",
        workHours: String = "",
        expertise: String = "",
        location: String = ""
    ): Result<User> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: throw Exception("User ID is null")
            
            val user = User(
                id = userId,
                name = name,
                email = email,
                phone = phone,
                dob = dob,
                role = role.name,
                address = address,
                workHours = workHours,
                expertise = expertise,
                location = location,
                createdAt = System.currentTimeMillis()
            )
            
            database.child(USERS_PATH).child(userId).setValue(user).await()
            
            Log.d(TAG, "User registered successfully: $userId")
            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "Registration failed", e)
            Result.failure(e)
        }
    }
    
    /**
     * Login with email and password
     */
    suspend fun loginUser(email: String, password: String): Result<User> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: throw Exception("User ID is null")
            
            val user = getUserById(userId).getOrThrow()
            
            Log.d(TAG, "User logged in successfully: $userId")
            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "Login failed", e)
            Result.failure(e)
        }
    }
    
    /**
     * Logout current user
     */
    fun logout() {
        auth.signOut()
        Log.d(TAG, "User logged out")
    }
    
    fun getCurrentFirebaseUser(): FirebaseUser? = auth.currentUser
    fun getCurrentUserId(): String? = auth.currentUser?.uid
    fun isUserLoggedIn(): Boolean = auth.currentUser != null
    
    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Password reset failed", e)
            Result.failure(e)
        }
    }
    
    // ==================== USER OPERATIONS ====================
    
    suspend fun getUserById(userId: String): Result<User> {
        return try {
            val snapshot = database.child(USERS_PATH).child(userId).get().await()
            val user = snapshot.getValue(User::class.java) 
                ?: throw Exception("User not found")
            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get user", e)
            Result.failure(e)
        }
    }
    
    suspend fun getCurrentUser(): Result<User> {
        val userId = getCurrentUserId() ?: return Result.failure(Exception("No user logged in"))
        return getUserById(userId)
    }
    
    suspend fun updateUser(userId: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            database.child(USERS_PATH).child(userId).updateChildren(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update user", e)
            Result.failure(e)
        }
    }
    
    /**
     * Get all sitters for browsing - Support requirement: browse local available pet-takers
     */
    suspend fun getAllSitters(location: String? = null): Result<List<User>> {
        return try {
            val snapshot = database.child(USERS_PATH)
                .orderByChild("role")
                .equalTo(Role.SITTER.name)
                .get()
                .await()
            
            var sitters = snapshot.children.mapNotNull { 
                it.getValue(User::class.java) 
            }
            
            // Filter by location if provided
            if (!location.isNullOrBlank()) {
                sitters = sitters.filter { 
                    it.location.contains(location, ignoreCase = true) 
                }
            }
            
            // Sort by rating
            sitters = sitters.sortedByDescending { it.rating }
            
            Result.success(sitters)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get sitters", e)
            Result.failure(e)
        }
    }
    
    /**
     * Get verified/premium sitters - Support requirement: official licensed sitters
     */
    suspend fun getVerifiedSitters(): Result<List<User>> {
        return try {
            val allSitters = getAllSitters().getOrThrow()
            val verified = allSitters.filter { it.isVerified }
            Result.success(verified)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ==================== PET OPERATIONS ====================
    
    suspend fun addPet(pet: Pet): Result<Pet> {
        return try {
            val petId = generateId()
            val newPet = pet.copy(id = petId)
            database.child(PETS_PATH).child(petId).setValue(newPet).await()
            Result.success(newPet)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to add pet", e)
            Result.failure(e)
        }
    }
    
    suspend fun getPetById(petId: String): Result<Pet> {
        return try {
            val snapshot = database.child(PETS_PATH).child(petId).get().await()
            val pet = snapshot.getValue(Pet::class.java) 
                ?: throw Exception("Pet not found")
            Result.success(pet)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get pet", e)
            Result.failure(e)
        }
    }
    
    suspend fun getPetsByOwnerId(ownerId: String): Result<List<Pet>> {
        return try {
            val snapshot = database.child(PETS_PATH)
                .orderByChild("ownerId")
                .equalTo(ownerId)
                .get()
                .await()
            
            val pets = snapshot.children.mapNotNull { 
                it.getValue(Pet::class.java) 
            }.filter { it.isActive }
            
            Result.success(pets)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get pets", e)
            Result.failure(e)
        }
    }
    
    suspend fun updatePet(petId: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            database.child(PETS_PATH).child(petId).updateChildren(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update pet", e)
            Result.failure(e)
        }
    }

    suspend fun deletePet(petId: String): Result<Unit> {
        return try {
            database.child(PETS_PATH).child(petId).removeValue().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    // ==================== BOOKING OPERATIONS ====================
    
    /**
     * Create booking - Support requirement: book date and time for pet-taker
     */
    suspend fun createBooking(booking: Booking): Result<Booking> {
        return try {
            val bookingId = generateId()
            val newBooking = booking.copy(
                id = bookingId,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            database.child(BOOKINGS_PATH).child(bookingId).setValue(newBooking).await()
            
            // Create notification for sitter
            val notification = AppNotification(
                id = generateId(),
                userId = booking.sitterId,
                title = "New Booking Request",
                message = "You have a new pet care booking request",
                type = "BOOKING",
                relatedId = bookingId
            )
            database.child(NOTIFICATIONS_PATH).child(notification.id).setValue(notification).await()
            
            Result.success(newBooking)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create booking", e)
            Result.failure(e)
        }
    }
    
    suspend fun getBookingById(bookingId: String): Result<Booking> {
        return try {
            val snapshot = database.child(BOOKINGS_PATH).child(bookingId).get().await()
            val booking = snapshot.getValue(Booking::class.java) 
                ?: throw Exception("Booking not found")
            Result.success(booking)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get booking", e)
            Result.failure(e)
        }
    }
    
    suspend fun getBookingsByOwnerId(ownerId: String): Result<List<Booking>> {
        return try {
            val snapshot = database.child(BOOKINGS_PATH)
                .orderByChild("ownerId")
                .equalTo(ownerId)
                .get()
                .await()
            
            val bookings = snapshot.children.mapNotNull { 
                it.getValue(Booking::class.java) 
            }.sortedByDescending { it.createdAt }
            
            Result.success(bookings)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get owner bookings", e)
            Result.failure(e)
        }
    }
    
    suspend fun getBookingsBySitterId(sitterId: String): Result<List<Booking>> {
        return try {
            val snapshot = database.child(BOOKINGS_PATH)
                .orderByChild("sitterId")
                .equalTo(sitterId)
                .get()
                .await()
            
            val bookings = snapshot.children.mapNotNull { 
                it.getValue(Booking::class.java) 
            }.sortedByDescending { it.createdAt }
            
            Result.success(bookings)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get sitter bookings", e)
            Result.failure(e)
        }
    }
    
    suspend fun updateBookingStatus(bookingId: String, status: BookingStatus): Result<Unit> {
        return try {
            val updates = mapOf(
                "status" to status.name,
                "updatedAt" to System.currentTimeMillis()
            )
            database.child(BOOKINGS_PATH).child(bookingId).updateChildren(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update booking status", e)
            Result.failure(e)
        }
    }
    
    suspend fun updateBooking(bookingId: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            val finalUpdates = updates.toMutableMap()
            finalUpdates["updatedAt"] = System.currentTimeMillis()
            database.child(BOOKINGS_PATH).child(bookingId).updateChildren(finalUpdates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update booking", e)
            Result.failure(e)
        }
    }
    
    // ==================== DAILY REPORT OPERATIONS ====================
    
    /**
     * Add daily report - Support requirement: daily summary report showing pet caretaker's activities
     */
    suspend fun addDailyReport(report: DailyReport): Result<DailyReport> {
        return try {
            val reportId = generateId()
            val newReport = report.copy(id = reportId)
            database.child(REPORTS_PATH).child(reportId).setValue(newReport).await()
            
            // Notify owner
            val booking = getBookingById(report.bookingId).getOrNull()
            if (booking != null) {
                val notification = AppNotification(
                    id = generateId(),
                    userId = booking.ownerId,
                    title = "New Daily Report",
                    message = "Your pet's daily care report is ready",
                    type = "REPORT",
                    relatedId = reportId
                )
                database.child(NOTIFICATIONS_PATH).child(notification.id).setValue(notification).await()
            }
            
            Result.success(newReport)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to add daily report", e)
            Result.failure(e)
        }
    }
    
    suspend fun getReportsByBookingId(bookingId: String): Result<List<DailyReport>> {
        return try {
            val snapshot = database.child(REPORTS_PATH)
                .orderByChild("bookingId")
                .equalTo(bookingId)
                .get()
                .await()
            
            val reports = snapshot.children.mapNotNull { 
                it.getValue(DailyReport::class.java) 
            }.sortedByDescending { it.date }
            
            Result.success(reports)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get reports", e)
            Result.failure(e)
        }
    }
    
    suspend fun updateDailyReport(reportId: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            database.child(REPORTS_PATH).child(reportId).updateChildren(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update report", e)
            Result.failure(e)
        }
    }
    
    // ==================== CHECKLIST OPERATIONS ====================
    
    /**
     * Create checklist - Support requirement: create care checklist for pet caretaker
     */
    suspend fun addChecklistItem(item: ChecklistItem): Result<ChecklistItem> {
        return try {
            val itemId = generateId()
            val newItem = item.copy(id = itemId)
            database.child(CHECKLISTS_PATH).child(itemId).setValue(newItem).await()
            Result.success(newItem)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to add checklist item", e)
            Result.failure(e)
        }
    }
    
    suspend fun getChecklistByBookingId(bookingId: String): Result<List<ChecklistItem>> {
        return try {
            val snapshot = database.child(CHECKLISTS_PATH)
                .orderByChild("bookingId")
                .equalTo(bookingId)
                .get()
                .await()
            
            val items = snapshot.children.mapNotNull { 
                it.getValue(ChecklistItem::class.java) 
            }.sortedBy { it.createdAt }
            
            Result.success(items)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get checklist", e)
            Result.failure(e)
        }
    }
    
    suspend fun updateChecklistItem(itemId: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            database.child(CHECKLISTS_PATH).child(itemId).updateChildren(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update checklist item", e)
            Result.failure(e)
        }
    }
    
    suspend fun toggleChecklistItem(itemId: String, isCompleted: Boolean, userId: String): Result<Unit> {
        return try {
            val updates = mapOf(
                "isCompleted" to isCompleted,
                "completedAt" to if (isCompleted) System.currentTimeMillis() else 0L,
                "completedBy" to if (isCompleted) userId else ""
            )
            updateChecklistItem(itemId, updates)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to toggle checklist item", e)
            Result.failure(e)
        }
    }
    
    // ==================== CHAT OPERATIONS ====================
    
    /**
     * Send message - Support requirement: communicate with pet-taker
     */
    suspend fun sendMessage(message: ChatMessage): Result<ChatMessage> {
        return try {
            val messageId = generateId()
            val newMessage = message.copy(id = messageId)
            database.child(CHATS_PATH).child(messageId).setValue(newMessage).await()
            
            // Notify receiver
            val notification = AppNotification(
                id = generateId(),
                userId = message.receiverId,
                title = if (message.isEmergency) "EMERGENCY MESSAGE" else "New Message",
                message = if (message.isEmergency) "Urgent: ${message.message}" else message.message,
                type = if (message.isEmergency) "EMERGENCY" else "CHAT",
                relatedId = messageId
            )
            database.child(NOTIFICATIONS_PATH).child(notification.id).setValue(notification).await()
            
            Result.success(newMessage)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send message", e)
            Result.failure(e)
        }
    }
    
    suspend fun getChatMessages(bookingId: String): Result<List<ChatMessage>> {
        return try {
            val snapshot = database.child(CHATS_PATH)
                .orderByChild("bookingId")
                .equalTo(bookingId)
                .get()
                .await()
            
            val messages = snapshot.children.mapNotNull { 
                it.getValue(ChatMessage::class.java) 
            }.sortedBy { it.createdAt }
            
            Result.success(messages)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get messages", e)
            Result.failure(e)
        }
    }
    
    // ==================== REVIEW OPERATIONS ====================
    
    /**
     * Add review - Support requirement: view reviews of pet-takers
     */
    suspend fun addReview(review: Review): Result<Review> {
        return try {
            val reviewId = generateId()
            val newReview = review.copy(id = reviewId)
            database.child(REVIEWS_PATH).child(reviewId).setValue(newReview).await()
            
            // Update sitter's rating
            updateSitterRating(review.toUserId)
            
            Result.success(newReview)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to add review", e)
            Result.failure(e)
        }
    }
    
    suspend fun getReviewsForUser(userId: String): Result<List<Review>> {
        return try {
            val snapshot = database.child(REVIEWS_PATH)
                .orderByChild("toUserId")
                .equalTo(userId)
                .get()
                .await()
            
            val reviews = snapshot.children.mapNotNull { 
                it.getValue(Review::class.java) 
            }.sortedByDescending { it.createdAt }
            
            Result.success(reviews)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get reviews", e)
            Result.failure(e)
        }
    }
    
    private suspend fun updateSitterRating(sitterId: String) {
        try {
            val reviews = getReviewsForUser(sitterId).getOrNull() ?: return
            if (reviews.isEmpty()) return
            
            val avgRating = reviews.map { it.rating }.average()
            val updates = mapOf(
                "rating" to avgRating,
                "totalReviews" to reviews.size
            )
            updateUser(sitterId, updates)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update sitter rating", e)
        }
    }
    
    // ==================== NOTIFICATION OPERATIONS ====================
    
    suspend fun getNotifications(userId: String): Result<List<AppNotification>> {
        return try {
            val snapshot = database.child(NOTIFICATIONS_PATH)
                .orderByChild("userId")
                .equalTo(userId)
                .get()
                .await()
            
            val notifications = snapshot.children.mapNotNull { 
                it.getValue(AppNotification::class.java) 
            }.sortedByDescending { it.createdAt }
            
            Result.success(notifications)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get notifications", e)
            Result.failure(e)
        }
    }
    
    suspend fun markNotificationAsRead(notificationId: String): Result<Unit> {
        return try {
            database.child(NOTIFICATIONS_PATH).child(notificationId)
                .updateChildren(mapOf("isRead" to true)).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ==================== PAYMENT OPERATIONS ====================
    
    /**
     * Create payment - Support requirement: pay for services in app
     */
    suspend fun createPayment(payment: Payment): Result<Payment> {
        return try {
            val paymentId = generateId()
            val newPayment = payment.copy(id = paymentId)
            database.child(PAYMENTS_PATH).child(paymentId).setValue(newPayment).await()
            Result.success(newPayment)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create payment", e)
            Result.failure(e)
        }
    }
    
    suspend fun updatePaymentStatus(paymentId: String, status: String): Result<Unit> {
        return try {
            val updates = mapOf(
                "status" to status,
                "completedAt" to System.currentTimeMillis()
            )
            database.child(PAYMENTS_PATH).child(paymentId).updateChildren(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update payment status", e)
            Result.failure(e)
        }
    }
    
    suspend fun getPaymentsByBookingId(bookingId: String): Result<List<Payment>> {
        return try {
            val snapshot = database.child(PAYMENTS_PATH)
                .orderByChild("bookingId")
                .equalTo(bookingId)
                .get()
                .await()
            
            val payments = snapshot.children.mapNotNull { 
                it.getValue(Payment::class.java) 
            }
            
            Result.success(payments)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get payments", e)
            Result.failure(e)
        }
    }
    
    // ==================== REAL-TIME LISTENERS ====================
    
    fun observeUser(userId: String): Flow<User?> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                trySend(user)
            }
            
            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "User observation cancelled", error.toException())
                close(error.toException())
            }
        }
        
        database.child(USERS_PATH).child(userId).addValueEventListener(listener)
        
        awaitClose {
            database.child(USERS_PATH).child(userId).removeEventListener(listener)
        }
    }
    
    fun observeBookings(userId: String, isOwner: Boolean): Flow<List<Booking>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val bookings = snapshot.children.mapNotNull { 
                    it.getValue(Booking::class.java) 
                }.sortedByDescending { it.createdAt }
                trySend(bookings)
            }
            
            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Bookings observation cancelled", error.toException())
                close(error.toException())
            }
        }
        
        val query = if (isOwner) {
            database.child(BOOKINGS_PATH).orderByChild("ownerId").equalTo(userId)
        } else {
            database.child(BOOKINGS_PATH).orderByChild("sitterId").equalTo(userId)
        }
        
        query.addValueEventListener(listener)
        
        awaitClose {
            query.removeEventListener(listener)
        }
    }
    
    fun observeChats(bookingId: String): Flow<List<ChatMessage>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = snapshot.children.mapNotNull { 
                    it.getValue(ChatMessage::class.java) 
                }.sortedBy { it.createdAt }
                trySend(messages)
            }
            
            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Chat observation cancelled", error.toException())
                close(error.toException())
            }
        }
        
        database.child(CHATS_PATH).orderByChild("bookingId").equalTo(bookingId)
            .addValueEventListener(listener)
        
        awaitClose {
            database.child(CHATS_PATH).orderByChild("bookingId").equalTo(bookingId)
                .removeEventListener(listener)
        }
    }
}
