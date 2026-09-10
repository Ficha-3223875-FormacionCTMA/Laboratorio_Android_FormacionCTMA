package com.example.labo_android_semana_02.data.remote

import com.example.labo_android_semana_02.data.remote.api.ActividadApi
import com.example.labo_android_semana_02.data.remote.dto.ActividadDto
import kotlinx.coroutines.CancellationException
import java.io.IOException
import java.net.SocketTimeoutException

class RemoteActividadDataSource(
    private val api: ActividadApi,
    private val tokenProvider: TokenProvider
) {
    suspend fun fetchActividades(): Result<List<ActividadDto>> {
        return try {
            val response = api.getActividades(tokenProvider.getToken())
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                val error = when (response.code()) {
                    401 -> "Sesión expirada. Por favor, inicie sesión de nuevo."
                    404 -> "El recurso solicitado no existe."
                    in 500..599 -> "Error en el servidor. Intente más tarde."
                    else -> "Error inesperado: ${response.code()}"
                }
                Result.failure(Exception(error))
            }
        } catch (e: SocketTimeoutException) {
            Result.failure(Exception("Tiempo de espera agotado. Verifique su conexión."))
        } catch (e: IOException) {
            Result.failure(Exception("No hay conexión a internet."))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(Exception("Error de serialización o procesamiento."))
        }
    }
}
