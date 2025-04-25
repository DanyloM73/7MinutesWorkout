package com.danylom73.a7minutesworkout

data class ExerciseModel(
    var id: Int,
    var name: String,
    var image: Int,
    var isSelected: Boolean = false,
    var isCompleted: Boolean = false
)