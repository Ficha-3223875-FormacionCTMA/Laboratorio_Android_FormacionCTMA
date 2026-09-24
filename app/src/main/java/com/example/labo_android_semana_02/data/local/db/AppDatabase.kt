package com.example.labo_android_semana_02.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.labo_android_semana_02.data.local.dao.ActividadDao
import com.example.labo_android_semana_02.data.local.dao.ReporteDao
import com.example.labo_android_semana_02.data.local.entities.ActividadEntity
import com.example.labo_android_semana_02.data.local.entities.CategoriaEntity
import com.example.labo_android_semana_02.data.local.entities.ReporteEntity

@Database(
    entities = [ReporteEntity::class, CategoriaEntity::class, ActividadEntity::class],
    version = 4,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun reporteDao(): ReporteDao
    abstract fun actividadDao(): ActividadDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Agregar columna resuelto a la tabla reportes
                db.execSQL("ALTER TABLE reportes ADD COLUMN resuelto INTEGER NOT NULL DEFAULT 0")
            }
        }
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Migración 2 a 3 (si aplica)
            }
        }
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE reportes ADD COLUMN evidenciaFotoUri TEXT")
                db.execSQL("ALTER TABLE reportes ADD COLUMN evidenciaArchivoUri TEXT")
                db.execSQL("ALTER TABLE actividades ADD COLUMN evidenciaFotoUri TEXT")
                db.execSQL("ALTER TABLE actividades ADD COLUMN evidenciaArchivoUri TEXT")
            }
        }
    }
}
