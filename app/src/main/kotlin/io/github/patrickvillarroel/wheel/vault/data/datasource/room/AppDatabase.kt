package io.github.patrickvillarroel.wheel.vault.data.datasource.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.github.patrickvillarroel.wheel.vault.data.dao.BrandDao
import io.github.patrickvillarroel.wheel.vault.data.dao.CarDao
import io.github.patrickvillarroel.wheel.vault.data.dao.CarImageDao
import io.github.patrickvillarroel.wheel.vault.data.dao.NewsDao
import io.github.patrickvillarroel.wheel.vault.data.entity.BrandEntity
import io.github.patrickvillarroel.wheel.vault.data.entity.CarEntity
import io.github.patrickvillarroel.wheel.vault.data.entity.CarImageEntity
import io.github.patrickvillarroel.wheel.vault.data.entity.NewsEntity

@Database(
    entities = [
        BrandEntity::class,
        CarEntity::class,
        CarImageEntity::class,
        NewsEntity::class,
    ],
    version = 3,
    exportSchema = false,
)
@TypeConverters(RoomTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun brandDao(): BrandDao
    abstract fun carDao(): CarDao
    abstract fun carImageDao(): CarImageDao
    abstract fun newsDao(): NewsDao
}
