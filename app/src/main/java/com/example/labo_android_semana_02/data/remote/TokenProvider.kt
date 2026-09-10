package com.example.labo_android_semana_02.data.remote

interface TokenProvider {
    fun getToken(): String
}

class SessionTokenProvider : TokenProvider {
    override fun getToken(): String {
        // En un entorno real, esto vendría de un DataStore o Session Manager
        // PROHIBIDO: Literales reales o BuildConfig con secretos.
        return "Bearer sample_token_12345" 
    }
}
