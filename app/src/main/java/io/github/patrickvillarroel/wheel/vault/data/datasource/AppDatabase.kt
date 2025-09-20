package io.github.patrickvillarroel.wheel.vault.data.datasource

import androidx.room.Database
import androidx.room.RoomDatabase
import io.github.patrickvillarroel.wheel.vault.data.dao.BrandDao
import io.github.patrickvillarroel.wheel.vault.data.dao.CarDao
import io.github.patrickvillarroel.wheel.vault.data.entity.BrandEntity
import io.github.patrickvillarroel.wheel.vault.data.entity.CarEntity

@Database(entities = [BrandEntity::class, CarEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun brandDao(): BrandDao
    abstract fun carDao(): CarDao
}
