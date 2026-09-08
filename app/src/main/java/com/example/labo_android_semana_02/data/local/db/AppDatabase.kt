package com.example.labo_android_semana_02.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.labo_android_semana_02.data.local.dao.ReporteDao
import com.example.labo_android_semana_02.data.local.entities.CategoriaEntity
import com.example.labo_android_semana_02.data.local.entities.ReporteEntity

@Database(
    entities = [ReporteEntity::class, CategoriaEntity::class],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun reporteDao(): ReporteDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Agregar columna resuelto a la tabla reportes
                db.execSQL("ALTER TABLE reportes ADD COLUMN resuelto INTEGER NOT NULL DEFAULT 0")
            }
        }
    }
}
