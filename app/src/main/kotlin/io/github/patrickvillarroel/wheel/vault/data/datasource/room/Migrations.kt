package io.github.patrickvillarroel.wheel.vault.data.datasource.room

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Database migrations for AppDatabase.
 *
 * Each migration handles schema changes between versions.
 */
object Migrations {
    /**
     * Migration from version 2 to 3.
     *
     * Changes:
     * 1. Adds sync metadata columns to cars, brands, and news tables
     * 2. Creates car_images table with foreign key to cars
     * 3. Adds indices for sync_status and last_synced_at columns
     * 4. Fixes idRemote in cars table to not generate random UUIDs
     */
    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Step 1: Add sync metadata columns to cars table
            db.execSQL("ALTER TABLE cars ADD COLUMN updated_at INTEGER DEFAULT (strftime('%s','now') * 1000)")
            db.execSQL("ALTER TABLE cars ADD COLUMN sync_status TEXT NOT NULL DEFAULT 'PENDING'")
            db.execSQL("ALTER TABLE cars ADD COLUMN last_synced_at INTEGER")
            db.execSQL("ALTER TABLE cars ADD COLUMN is_deleted INTEGER NOT NULL DEFAULT 0")

            // Step 2: Create indices for cars sync columns
            db.execSQL("CREATE INDEX IF NOT EXISTS index_cars_sync_status ON cars(sync_status)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_cars_last_synced_at ON cars(last_synced_at)")

            // Step 3: Add sync metadata columns to brands table
            db.execSQL("ALTER TABLE brands ADD COLUMN updated_at INTEGER DEFAULT (strftime('%s','now') * 1000)")
            db.execSQL("ALTER TABLE brands ADD COLUMN sync_status TEXT NOT NULL DEFAULT 'SYNCED'")
            db.execSQL("ALTER TABLE brands ADD COLUMN last_synced_at INTEGER")
            db.execSQL("ALTER TABLE brands ADD COLUMN is_deleted INTEGER NOT NULL DEFAULT 0")

            // Step 4: Create indices for brands sync columns
            db.execSQL("CREATE INDEX IF NOT EXISTS index_brands_sync_status ON brands(sync_status)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_brands_last_synced_at ON brands(last_synced_at)")

            // Step 5: Add sync metadata columns to news table
            db.execSQL("ALTER TABLE news ADD COLUMN updated_at INTEGER DEFAULT (strftime('%s','now') * 1000)")
            db.execSQL("ALTER TABLE news ADD COLUMN sync_status TEXT NOT NULL DEFAULT 'SYNCED'")
            db.execSQL("ALTER TABLE news ADD COLUMN last_synced_at INTEGER")
            db.execSQL("ALTER TABLE news ADD COLUMN is_deleted INTEGER NOT NULL DEFAULT 0")

            // Step 6: Create indices for news sync columns
            db.execSQL("CREATE INDEX IF NOT EXISTS index_news_sync_status ON news(sync_status)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_news_last_synced_at ON news(last_synced_at)")

            // Step 7: Create car_images table
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS car_images (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    car_id_remote TEXT NOT NULL,
                    id_remote TEXT NOT NULL,
                    storage_path TEXT NOT NULL,
                    mime_type TEXT NOT NULL,
                    uploaded_by TEXT NOT NULL,
                    is_primary INTEGER NOT NULL DEFAULT 0,
                    local_path TEXT,
                    created_at INTEGER DEFAULT (strftime('%s','now') * 1000),
                    updated_at INTEGER DEFAULT (strftime('%s','now') * 1000),
                    sync_status TEXT NOT NULL DEFAULT 'PENDING',
                    last_synced_at INTEGER,
                    is_deleted INTEGER NOT NULL DEFAULT 0,
                    FOREIGN KEY(car_id_remote) REFERENCES cars(id_remote) ON DELETE CASCADE
                )
                """.trimIndent(),
            )

            // Step 8: Create indices for car_images table
            db.execSQL("CREATE INDEX IF NOT EXISTS index_car_images_car_id_remote ON car_images(car_id_remote)")
            db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_car_images_id_remote ON car_images(id_remote)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_car_images_is_primary ON car_images(is_primary)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_car_images_sync_status ON car_images(sync_status)")
        }
    }

    /**
     * List of all migrations to apply.
     */
    val ALL = arrayOf(
        MIGRATION_2_3,
    )
}
