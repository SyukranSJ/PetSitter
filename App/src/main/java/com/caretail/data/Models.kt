package com.caretail.data

data class Pet(val id: String, val name: String, val type: String, val ownerId: String)
data class User(val id: String, val name: String, val role: Role)
enum class Role { OWNER, SITTER }

data class Booking(val id: String, val pet: Pet, val sitter: User, val days: Int, val status: String)
data class ChecklistItem(val id: String, val label: String, var done: Boolean = false)
data class DailyReport(val id: String, val bookingId: String, val notes: String)

object Repo {
    val ownerSarah = User("u1", "Sarah", Role.OWNER)
    val ownerJames = User("u2", "James", Role.OWNER)
    val sitterAisha = User("u3", "Aisha", Role.SITTER)
    val sitterDaniel = User("u4", "Daniel", Role.SITTER)

    val buddy = Pet("p1", "Buddy", "Dog", ownerSarah.id)
    val milo = Pet("p2", "Milo", "Cat", ownerSarah.id)

    val bookings = mutableListOf(
        Booking("b1", buddy, sitterAisha, 3, "Accepted"),
        Booking("b2", milo, sitterAisha, 1, "Pending")
    )

    val defaultChecklist = listOf(
        ChecklistItem("c1", "Feed"),
        ChecklistItem("c2", "Walk"),
        ChecklistItem("c3", "Play"),
        ChecklistItem("c4", "Clean"),
        ChecklistItem("c5", "Upload Photo")
    )

    // Registered users (test + registered)
    val users = mutableListOf(
        User("u1", "Sarah", Role.OWNER),
        User("u2", "James", Role.OWNER),
        User("u3", "Aisha", Role.SITTER),
        User("u4", "Daniel", Role.SITTER)
    )

    // Register new user locally
    fun registerUser(name: String, email: String, password: String, phone: String, dob: String, role: Role): User {
        val id = "u${users.size + 1}"
        val newUser = User(id, name, role)
        users.add(newUser)
        return newUser
    }

    // Authenticate local users or test credentials
    fun authenticate(email: String, password: String): Role? {
        if (email.equals("owner@caretail.test", true) && password == "owner123") return Role.OWNER
        if (email.equals("sitter@caretail.test", true) && password == "sitter123") return Role.SITTER
        // Match by name (for local demo registrations)
        return users.find { it.name.equals(email, true) }?.role
    }
}
