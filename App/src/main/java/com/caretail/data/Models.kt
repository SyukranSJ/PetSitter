package com.caretail.data

import com.google.firebase.database.IgnoreExtraProperties
import java.util.UUID

// Enums
enum class Role { OWNER, SITTER }
enum class BookingStatus { PENDING, CONFIRMED, IN_PROGRESS, COMPLETED, CANCELLED }
enum class PetType { DOG, CAT, BIRD, RABBIT, HAMSTER, FISH, OTHER }

// User Model - Based on requirements: pet owners and caretakers
@IgnoreExtraProperties
data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val dob: String = "",
    val role: String = Role.OWNER.name,
    val profileImageUrl: String = "",
    val address: String = "",
    val bio: String = "",
    val rating: Double = 0.0,
    val totalReviews: Int = 0,
    // For sitters: work hours, expertise, location
    val workHours: String = "", // e.g., "9am-5pm" or "Flexible"
    val expertise: String = "", // e.g., "Dogs, Cats"
    val location: String = "", // For browsing local sitters
    val isVerified: Boolean = false, // For licensed/official sitters
    val isPremium: Boolean = false, // Premium service flag
    val createdAt: Long = System.currentTimeMillis()
) {
    constructor() : this("", "", "", "", "", Role.OWNER.name, "", "", "", 0.0, 0, "", "", "", false, false, 0L)
    
    fun getRoleEnum(): Role = try {
        Role.valueOf(role)
    } catch (e: Exception) {
        Role.OWNER
    }
}

// Pet Model - For pet owner's pets
@IgnoreExtraProperties
data class Pet(
    val id: String = "",
    val ownerId: String = "",
    val name: String = "",
    val type: String = PetType.DOG.name,
    val breed: String = "",
    val age: Int = 0,
    val weight: Double = 0.0,
    val color: String = "",
    val imageUrl: String = "",
    val medicalInfo: String = "", // Medical conditions, allergies
    val specialNeeds: String = "", // Special care instructions
    val feedingSchedule: String = "", // When and what to feed
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
) {
    constructor() : this("", "", "", PetType.DOG.name, "", 0, 0.0, "", "", "", "", "", true, 0L)
    
    fun getTypeEnum(): PetType = try {
        PetType.valueOf(type)
    } catch (e: Exception) {
        PetType.DOG
    }
}

// Booking Model - Schedule and book caretakers
@IgnoreExtraProperties
data class Booking(
    val id: String = "",
    val ownerId: String = "",
    val sitterId: String = "",
    val petId: String = "",
    val startDate: Long = 0L,
    val endDate: Long = 0L,
    val status: String = BookingStatus.PENDING.name,
    val totalDays: Int = 0,
    val pricePerDay: Double = 0.0,
    val totalPrice: Double = 0.0,
    val specialInstructions: String = "", // Care checklist instructions
    val pickupAddress: String = "", // Where to pick up/care for pet
    val dropoffAddress: String = "",
    val isOnSite: Boolean = true, // On-site (sitter comes to owner) or off-site
    val isPremium: Boolean = false, // Premium service booking
    val additionalServices: List<String> = emptyList(), // grooming, vet, food delivery
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    constructor() : this("", "", "", "", 0L, 0L, BookingStatus.PENDING.name, 0, 0.0, 0.0, "", "", "", true, false, emptyList(), 0L, 0L)
    
    fun getStatusEnum(): BookingStatus = try {
        BookingStatus.valueOf(status)
    } catch (e: Exception) {
        BookingStatus.PENDING
    }
}

// Daily Report Model - Verifiable activity logs with photos
@IgnoreExtraProperties
data class DailyReport(
    val id: String = "",
    val bookingId: String = "",
    val petId: String = "",
    val sitterId: String = "",
    val date: Long = System.currentTimeMillis(),
    val mealsTaken: Int = 0,
    val walksCompleted: Int = 0,
    val playTimeMinutes: Int = 0,
    val bathroomBreaks: Int = 0,
    val mood: String = "Happy",
    val healthStatus: String = "Normal",
    val notes: String = "",
    val photoUrls: List<String> = emptyList(), // In-app camera photos
    val photoTimestamps: List<Long> = emptyList(), // When photos were taken
    val location: String = "", // GPS location when photo taken
    val createdAt: Long = System.currentTimeMillis()
) {
    constructor() : this("", "", "", "", 0L, 0, 0, 0, 0, "Happy", "Normal", "", emptyList(), emptyList(), "", 0L)
}

// Checklist Item Model - Owner-created or auto checklist
@IgnoreExtraProperties
data class ChecklistItem(
    val id: String = "",
    val bookingId: String = "",
    val label: String = "",
    val description: String = "",
    val isCompleted: Boolean = false,
    val completedAt: Long = 0L,
    val completedBy: String = "",
    val photoUrl: String = "", // Proof photo
    val createdAt: Long = System.currentTimeMillis()
) {
    constructor() : this("", "", "", "", false, 0L, "", "", 0L)
}

// Review Model - Pet owner reviews sitter
@IgnoreExtraProperties
data class Review(
    val id: String = "",
    val bookingId: String = "",
    val fromUserId: String = "",
    val toUserId: String = "",
    val rating: Int = 5,
    val comment: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    constructor() : this("", "", "", "", 5, "", 0L)
}

// Chat Message Model - Communication between owner and sitter
@IgnoreExtraProperties
data class ChatMessage(
    val id: String = "",
    val bookingId: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val message: String = "",
    val imageUrl: String = "",
    val isRead: Boolean = false,
    val isEmergency: Boolean = false, // Flag for emergency messages
    val createdAt: Long = System.currentTimeMillis()
) {
    constructor() : this("", "", "", "", "", "", false, false, 0L)
}

// Notification Model - Push notifications for updates
@IgnoreExtraProperties
data class AppNotification(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val message: String = "",
    val type: String = "INFO", // BOOKING, REPORT, EMERGENCY, REMINDER, MAINTENANCE
    val relatedId: String = "",
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    constructor() : this("", "", "", "", "INFO", "", false, 0L)
}

// Payment Model - Track payments for services
@IgnoreExtraProperties
data class Payment(
    val id: String = "",
    val bookingId: String = "",
    val fromUserId: String = "",
    val toUserId: String = "",
    val amount: Double = 0.0,
    val currency: String = "USD",
    val status: String = "PENDING", // PENDING, COMPLETED, FAILED, REFUNDED
    val paymentMethod: String = "", // CARD, WALLET, etc.
    val transactionId: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long = 0L
) {
    constructor() : this("", "", "", "", 0.0, "USD", "PENDING", "", "", 0L, 0L)
}

// Helper function to generate IDs
fun generateId(): String = UUID.randomUUID().toString()

