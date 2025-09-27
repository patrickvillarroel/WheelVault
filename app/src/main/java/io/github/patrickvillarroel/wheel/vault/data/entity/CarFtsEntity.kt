package io.github.patrickvillarroel.wheel.vault.data.entity

import androidx.room.Entity
import androidx.room.Fts4

@Fts4(contentEntity = CarEntity::class)
@Entity(tableName = "cars_fts")
class CarFtsEntity(
    val model: String,
    val brand: String,
    val manufacturer: String,
    val category: String?,
    val description: String?,
)
