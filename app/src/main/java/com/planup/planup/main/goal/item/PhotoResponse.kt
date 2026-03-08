package com.planup.planup.main.goal.item

data class DeletePhotoResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: Unit
)

data class DailyPhotoResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: List<DailyPhotoResult>
)

data class DailyPhotoResult(
    val date: String,
    val photos: List<PhotosDTO>
)

data class PhotosDTO(
    val id: Int,
    val photoUrl: String,
    val createdAt: String
)

data class PostPhotoRequest(
    val files: List<String>
)

data class PostPhotoResponse(
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    val result: PostPhotoResult
)

data class PostPhotoResult(
    val date: String,
    val uploadedPhotos: PhotosDTO
)