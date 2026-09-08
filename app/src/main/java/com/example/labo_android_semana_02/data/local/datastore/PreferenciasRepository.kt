package com.example.labo_android_semana_02.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_preferences")

class PreferenciasRepository(private val context: Context) {

    companion object {
        val ORDEN_FILTRO = stringPreferencesKey("orden_filtro")
    }

    val ordenFiltro: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[ORDEN_FILTRO] ?: "FECHA_DESC"
    }

    suspend fun guardarOrdenFiltro(orden: String) {
        context.dataStore.edit { preferences ->
            preferences[ORDEN_FILTRO] = orden
        }
    }
}
