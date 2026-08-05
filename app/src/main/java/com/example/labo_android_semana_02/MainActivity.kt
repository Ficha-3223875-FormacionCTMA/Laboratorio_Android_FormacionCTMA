package com.example.labo_android_semana_02

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.labo_android_semana_02.domain.ActividadFormativa
import com.example.labo_android_semana_02.domain.Prioridad
import com.example.labo_android_semana_02.domain.actividadesUrgentes
import com.example.labo_android_semana_02.domain.promedioProgreso
import com.example.labo_android_semana_02.ui.theme.Labo_android_semana_02Theme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val actividades = listOf(
            ActividadFormativa(
                id = 1,
                titulo = "Kotlin básico",
                descripcion = "Variables y funciones",
                progreso = 100,
                diasRestantes = 5,
                prioridad = Prioridad.MEDIA
            ),
            ActividadFormativa(
                id = 2,
                titulo = "Semana 2 Android",
                descripcion = "Modelado y reglas",
                progreso = 60,
                diasRestantes = 1,
                prioridad = Prioridad.ALTA
            ),
            ActividadFormativa(
                id = 3,
                titulo = "Evidencias SENA",
                descripcion = "Subir al repositorio",
                progreso = 0,
                diasRestantes = 2,
                prioridad = Prioridad.ALTA
            )
        )

        val promedio = promedioProgreso(actividades)
        val urgentes = actividadesUrgentes(actividades)

        val resumen = "Promedio: %.1f%%\nUrgentes: %d"
            .format(promedio, urgentes.size)

        setContent {
            Labo_android_semana_02Theme {
                PantallaInicio(
                    nombre = "Juan Manuel",
                    resumen = resumen
                )
            }
        }
    }
}

@Composable
fun PantallaInicio(
    nombre: String,
    resumen: String
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),

        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Mi Formación CTMA",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(text = "Hola, $nombre")

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = resumen)
    }
}

@Preview(showBackground = true)
@Composable
fun PantallaInicioPreview() {

    Labo_android_semana_02Theme {
        PantallaInicio(
            nombre = "Juan Manuel",
            resumen = "Promedio: 53.3%\nUrgentes: 2"
        )
    }
}