package com.example.demoapp2.data

data class Post(var userId: String, var body: String) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to userId,
            "message" to body
        )
    }
}
