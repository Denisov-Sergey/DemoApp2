package com.example.demoapp2.gallery_module


data class Comment(var userId: String, var message: String, var image: String) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to userId,
            "message" to message,
            "image" to image
        )
    }
}