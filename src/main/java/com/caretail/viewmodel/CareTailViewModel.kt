package com.caretail.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caretail.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class DataState<out T> {
    object Idle : DataState<Nothing>()
    object Loading : DataState<Nothing>()
    data class Success<T>(val data: T) : DataState<T>()
    data class Error(val message: String) : DataState<Nothing>()
}

/**
 * Main ViewModel for CareTail app - manages all pet care operations
 */
class CareTailViewModel(
    private val repository: FirebaseRepository = FirebaseRepository()
) : ViewModel() {

    // ==================== PETS ====================

    private val _petsState = MutableStateFlow<DataState<List<Pet>>>(DataState.Idle)
    val petsState: StateFlow<DataState<List<Pet>>> = _petsState.asStateFlow()

    private val _pets = MutableStateFlow<List<Pet>>(emptyList())
    val pets: StateFlow<List<Pet>> = _pets.asStateFlow()

    fun loadPets(ownerId: String) {
        viewModelScope.launch {
            _petsState.value = DataState.Loading

            val result = repository.getPetsByOwnerId(ownerId)
            result.onSuccess { petList ->
                _pets.value = petList
                _petsState.value = DataState.Success(petList)
            }.onFailure { exception ->
                _petsState.value = DataState.Error(
                    exception.message ?: "Failed to load pets"
                )
            }
        }
    }

    fun addPet(
        ownerId: String,
        name: String,
        type: PetType,
        breed: String,
        age: Int,
        weight: Double,
        color: String,
        medicalInfo: String,
        specialNeeds: String,
        feedingSchedule: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val pet = Pet(
                ownerId = ownerId,
                name = name,
                type = type.name,
                breed = breed,
                age = age,
                weight = weight,
                color = color,
                medicalInfo = medicalInfo,
                specialNeeds = specialNeeds,
                feedingSchedule = feedingSchedule
            )

            val result = repository.addPet(pet)
            result.onSuccess {
                loadPets(ownerId)
                onResult(true, "Pet added successfully!")
            }.onFailure { exception ->
                onResult(false, exception.message ?: "Failed to add pet")
            }
        }
    }

    fun updatePet(
        petId: String,
        ownerId: String,
        updates: Map<String, Any>,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.updatePet(petId, updates)
            result.onSuccess {
                loadPets(ownerId)
                onResult(true, "Pet updated successfully!")
            }.onFailure { exception ->
                onResult(false, exception.message ?: "Failed to update pet")
            }
        }
    }

    fun deletePet(
        petId: String,
        ownerId: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.deletePet(petId)
            result.onSuccess {
                loadPets(ownerId)
                onResult(true, "Pet removed successfully")
            }.onFailure { exception ->
                onResult(false, exception.message ?: "Failed to remove pet")
            }
        }
    }

    // ==================== SITTERS ====================

    private val _sitters = MutableStateFlow<List<User>>(emptyList())
    val sitters: StateFlow<List<User>> = _sitters.asStateFlow()

    private val _verifiedSitters = MutableStateFlow<List<User>>(emptyList())
    val verifiedSitters: StateFlow<List<User>> = _verifiedSitters.asStateFlow()

    fun loadSitters(location: String? = null) {
        viewModelScope.launch {
            val result = repository.getAllSitters(location)
            result.onSuccess { sitterList ->
                _sitters.value = sitterList
            }
        }
    }

    fun loadVerifiedSitters() {
        viewModelScope.launch {
            val result = repository.getVerifiedSitters()
            result.onSuccess { sitterList ->
                _verifiedSitters.value = sitterList
            }
        }
    }

    // ==================== BOOKINGS ====================

    private val _bookingsState = MutableStateFlow<DataState<List<Booking>>>(DataState.Idle)
    val bookingsState: StateFlow<DataState<List<Booking>>> = _bookingsState.asStateFlow()

    private val _bookings = MutableStateFlow<List<Booking>>(emptyList())
    val bookings: StateFlow<List<Booking>> = _bookings.asStateFlow()

    fun loadOwnerBookings(ownerId: String) {
        viewModelScope.launch {
            _bookingsState.value = DataState.Loading

            val result = repository.getBookingsByOwnerId(ownerId)
            result.onSuccess { bookingList ->
                _bookings.value = bookingList
                _bookingsState.value = DataState.Success(bookingList)
            }.onFailure { exception ->
                _bookingsState.value = DataState.Error(
                    exception.message ?: "Failed to load bookings"
                )
            }
        }
    }

    fun loadSitterBookings(sitterId: String) {
        viewModelScope.launch {
            _bookingsState.value = DataState.Loading

            val result = repository.getBookingsBySitterId(sitterId)
            result.onSuccess { bookingList ->
                _bookings.value = bookingList
                _bookingsState.value = DataState.Success(bookingList)
            }.onFailure { exception ->
                _bookingsState.value = DataState.Error(
                    exception.message ?: "Failed to load bookings"
                )
            }
        }
    }

    fun createBooking(
        ownerId: String,
        sitterId: String,
        petId: String,
        startDate: Long,
        endDate: Long,
        totalDays: Int,
        pricePerDay: Double,
        specialInstructions: String,
        pickupAddress: String,
        isOnSite: Boolean,
        isPremium: Boolean,
        additionalServices: List<String>,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val booking = Booking(
                ownerId = ownerId,
                sitterId = sitterId,
                petId = petId,
                startDate = startDate,
                endDate = endDate,
                totalDays = totalDays,
                pricePerDay = pricePerDay,
                totalPrice = totalDays * pricePerDay,
                specialInstructions = specialInstructions,
                pickupAddress = pickupAddress,
                isOnSite = isOnSite,
                isPremium = isPremium,
                additionalServices = additionalServices,
                status = BookingStatus.PENDING.name
            )

            val result = repository.createBooking(booking)
            result.onSuccess {
                loadOwnerBookings(ownerId)
                onResult(true, "Booking created successfully!")
            }.onFailure { exception ->
                onResult(false, exception.message ?: "Failed to create booking")
            }
        }
    }

    fun updateBookingStatus(
        bookingId: String,
        status: BookingStatus,
        userId: String,
        isOwner: Boolean,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.updateBookingStatus(bookingId, status)
            result.onSuccess {
                if (isOwner) {
                    loadOwnerBookings(userId)
                } else {
                    loadSitterBookings(userId)
                }
                onResult(true, "Booking status updated to ${status.name}")
            }.onFailure { exception ->
                onResult(false, exception.message ?: "Failed to update booking")
            }
        }
    }

    fun acceptBooking(bookingId: String, sitterId: String, onResult: (Boolean, String) -> Unit) {
        updateBookingStatus(bookingId, BookingStatus.CONFIRMED, sitterId, false, onResult)
    }

    fun rejectBooking(bookingId: String, sitterId: String, onResult: (Boolean, String) -> Unit) {
        updateBookingStatus(bookingId, BookingStatus.CANCELLED, sitterId, false, onResult)
    }

    fun startBooking(bookingId: String, sitterId: String, onResult: (Boolean, String) -> Unit) {
        updateBookingStatus(bookingId, BookingStatus.IN_PROGRESS, sitterId, false, onResult)
    }

    fun completeBooking(bookingId: String, sitterId: String, onResult: (Boolean, String) -> Unit) {
        updateBookingStatus(bookingId, BookingStatus.COMPLETED, sitterId, false, onResult)
    }

    fun getPendingBookings(): List<Booking> {
        return _bookings.value.filter {
            it.getStatusEnum() == BookingStatus.PENDING
        }
    }

    fun getActiveBookings(): List<Booking> {
        return _bookings.value.filter {
            it.getStatusEnum() == BookingStatus.CONFIRMED ||
                    it.getStatusEnum() == BookingStatus.IN_PROGRESS
        }
    }

    // ==================== DAILY REPORTS ====================

    private val _reports = MutableStateFlow<List<DailyReport>>(emptyList())
    val reports: StateFlow<List<DailyReport>> = _reports.asStateFlow()

    fun loadReports(bookingId: String) {
        viewModelScope.launch {
            val result = repository.getReportsByBookingId(bookingId)
            result.onSuccess { reportList ->
                _reports.value = reportList
            }
        }
    }

    fun submitDailyReport(
        bookingId: String,
        petId: String,
        sitterId: String,
        mealsTaken: Int,
        walksCompleted: Int,
        playTimeMinutes: Int,
        bathroomBreaks: Int,
        mood: String,
        healthStatus: String,
        notes: String,
        photoUrls: List<String>,
        location: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val report = DailyReport(
                bookingId = bookingId,
                petId = petId,
                sitterId = sitterId,
                date = System.currentTimeMillis(),
                mealsTaken = mealsTaken,
                walksCompleted = walksCompleted,
                playTimeMinutes = playTimeMinutes,
                bathroomBreaks = bathroomBreaks,
                mood = mood,
                healthStatus = healthStatus,
                notes = notes,
                photoUrls = photoUrls,
                photoTimestamps = List(photoUrls.size) { System.currentTimeMillis() },
                location = location
            )

            val result = repository.addDailyReport(report)
            result.onSuccess {
                loadReports(bookingId)
                onResult(true, "Daily report submitted successfully!")
            }.onFailure { exception ->
                onResult(false, exception.message ?: "Failed to submit report")
            }
        }
    }

    // ==================== CHECKLIST ====================

    private val _checklist = MutableStateFlow<List<ChecklistItem>>(emptyList())
    val checklist: StateFlow<List<ChecklistItem>> = _checklist.asStateFlow()

    fun loadChecklist(bookingId: String) {
        viewModelScope.launch {
            val result = repository.getChecklistByBookingId(bookingId)
            result.onSuccess { items ->
                _checklist.value = items
            }
        }
    }

    fun addChecklistItem(
        bookingId: String,
        label: String,
        description: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val item = ChecklistItem(
                bookingId = bookingId,
                label = label,
                description = description
            )

            val result = repository.addChecklistItem(item)
            result.onSuccess {
                loadChecklist(bookingId)
                onResult(true, "Checklist item added!")
            }.onFailure { exception ->
                onResult(false, exception.message ?: "Failed to add item")
            }
        }
    }

    fun toggleChecklistItem(
        itemId: String,
        bookingId: String,
        isCompleted: Boolean,
        userId: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.toggleChecklistItem(itemId, isCompleted, userId)
            result.onSuccess {
                loadChecklist(bookingId)
                onResult(true, if (isCompleted) "Task completed!" else "Task unmarked")
            }.onFailure { exception ->
                onResult(false, exception.message ?: "Failed to update task")
            }
        }
    }

    // ==================== CHAT ====================

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    fun loadMessages(bookingId: String) {
        viewModelScope.launch {
            val result = repository.getChatMessages(bookingId)
            result.onSuccess { messageList ->
                _messages.value = messageList
            }
        }
    }

    fun sendMessage(
        bookingId: String,
        senderId: String,
        receiverId: String,
        message: String,
        isEmergency: Boolean = false,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val chatMessage = ChatMessage(
                bookingId = bookingId,
                senderId = senderId,
                receiverId = receiverId,
                message = message,
                isEmergency = isEmergency
            )

            val result = repository.sendMessage(chatMessage)
            result.onSuccess {
                loadMessages(bookingId)
                onResult(true, "Message sent")
            }.onFailure { exception ->
                onResult(false, exception.message ?: "Failed to send message")
            }
        }
    }

    // ==================== REVIEWS ====================

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()

    fun loadReviews(userId: String) {
        viewModelScope.launch {
            val result = repository.getReviewsForUser(userId)
            result.onSuccess { reviewList ->
                _reviews.value = reviewList
            }
        }
    }

    fun submitReview(
        bookingId: String,
        fromUserId: String,
        toUserId: String,
        rating: Int,
        comment: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val review = Review(
                bookingId = bookingId,
                fromUserId = fromUserId,
                toUserId = toUserId,
                rating = rating,
                comment = comment
            )

            val result = repository.addReview(review)
            result.onSuccess {
                onResult(true, "Review submitted!")
            }.onFailure { exception ->
                onResult(false, exception.message ?: "Failed to submit review")
            }
        }
    }

    // ==================== PAYMENTS ====================

    fun createPayment(
        bookingId: String,
        fromUserId: String,
        toUserId: String,
        amount: Double,
        paymentMethod: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val payment = Payment(
                bookingId = bookingId,
                fromUserId = fromUserId,
                toUserId = toUserId,
                amount = amount,
                paymentMethod = paymentMethod,
                status = "PENDING"
            )

            val result = repository.createPayment(payment)
            result.onSuccess {
                onResult(true, "Payment initiated")
            }.onFailure { exception ->
                onResult(false, exception.message ?: "Payment failed")
            }
        }
    }

    fun getPetById(petId: String): Pet? {
        return _pets.value.firstOrNull { it.id == petId }
    }

}