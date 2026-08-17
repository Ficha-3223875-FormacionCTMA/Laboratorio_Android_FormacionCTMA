package com.example.labo_android_semana_02

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.labo_android_semana_02.domain.ActividadFormativa
import com.example.labo_android_semana_02.domain.Prioridad
import com.example.labo_android_semana_02.domain.actividadesUrgentes
import com.example.labo_android_semana_02.domain.promedioProgreso
import com.example.labo_android_semana_02.ui.theme.Labo_android_semana_02Theme

// Colores sugeridos
private val AgilePrimary = Color(0xFF000652)
private val AgileSecondary = Color(0xFF00BFA5)
private val AgileBackground = Color(0xFFF8F9FE)
private val AgileSurface = Color(0xFFFFFFFF)
private val AgileTextPrimary = Color(0xFF121420)
private val AgileTextSecondary = Color(0xFF636981)

private val AgileColorScheme = lightColorScheme(
    primary = AgilePrimary,
    secondary = AgileSecondary,
    background = AgileBackground,
    surface = AgileSurface,
    onPrimary = Color.White,
    onSurface = AgileTextPrimary
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = AgileColorScheme) {
                PantallaPrincipal("Juan Manuel")
            }
        }
    }
}

private data class Item(val titulo: String, val descripcion: String)

enum class AgileTab(val title: String) {
    ACTIVIDADES("Actividades"),
    MANIFESTO("Manifiesto"),
    SCRUM("Scrum"),
    TESTING("Pruebas")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPrincipal(nombre: String = "Aprendiz") {
    var selectedTab by remember { mutableStateOf(AgileTab.ACTIVIDADES) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Formación CTMA",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AgilePrimary,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = AgileSurface,
                tonalElevation = 8.dp
            ) {
                AgileTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = { 
                            // Representación simple sin usar la librería de iconos pesada
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (selectedTab == tab) AgilePrimary else AgileTextSecondary.copy(alpha = 0.3f))
                            )
                        },
                        label = { 
                            Text(
                                text = tab.title,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal
                            ) 
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedTextColor = AgilePrimary,
                            unselectedTextColor = AgileTextSecondary,
                            indicatorColor = AgilePrimary.copy(alpha = 0.1f)
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(AgileBackground)
        ) {
            Header(nombre)
            
            Box(modifier = Modifier.weight(1f)) {
                when (selectedTab) {
                    AgileTab.ACTIVIDADES -> SeccionActividades()
                    AgileTab.MANIFESTO -> SeccionManifiesto()
                    AgileTab.SCRUM -> SeccionScrum()
                    AgileTab.TESTING -> SeccionPruebas()
                }
            }
        }
    }
}

@Composable
private fun SeccionActividades() {
    val actividades = remember {
        listOf(
            ActividadFormativa(1, "Kotlin básico", "Variables y funciones", 100, 5, Prioridad.MEDIA),
            ActividadFormativa(2, "Semana 2 Android", "Modelado y reglas", 60, 1, Prioridad.ALTA),
            ActividadFormativa(3, "Evidencias SENA", "Subir al repositorio", 0, 2, Prioridad.ALTA)
        )
    }

    val promedio = promedioProgreso(actividades)
    val urgentes = actividadesUrgentes(actividades)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { SeccionTitulo("Resumen de Progreso", AgilePrimary) }
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AgileSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Promedio General: %.1f%%".format(promedio),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = AgilePrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Actividades Urgentes: ${urgentes.size}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (urgentes.isNotEmpty()) Color.Red else AgileSecondary
                    )
                }
            }
        }
        item { SeccionTitulo("Detalle de Actividades", AgilePrimary) }
        items(actividades) { actividad ->
            ItemCard(
                Item(actividad.titulo, "${actividad.descripcion} - ${actividad.progreso}%"),
                color = when(actividad.prioridad) {
                    Prioridad.ALTA -> Color.Red
                    Prioridad.MEDIA -> AgileSecondary
                    Prioridad.BAJA -> AgileTextSecondary
                }
            )
        }
    }
}

@Composable
private fun SeccionManifiesto() {
    val valores = listOf(
        "Individuos e interacciones sobre procesos y herramientas.",
        "Software funcionando sobre documentación exhaustiva.",
        "Colaboración con el cliente sobre negociación de contratos.",
        "Respuesta ante el cambio sobre seguir un plan estricto."
    )
    val principios = listOf(
        "Satisfacer al cliente mediante entregas continuas.",
        "Aceptar cambios incluso en etapas avanzadas.",
        "Trabajar juntos diariamente.",
        "Confiar en el equipo."
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { SeccionTitulo("Valores principales", AgilePrimary) }
        items(valores) { BulletCard(it, AgilePrimary) }
        item { SeccionTitulo("Principios clave", AgileSecondary) }
        items(principios) { BulletCard(it, AgileSecondary) }
    }
}

@Composable
private fun SeccionScrum() {
    val roles = listOf(
        Item("Product Owner", "Es la voz del cliente."),
        Item("Scrum Master", "Facilitador del proceso."),
        Item("Developers", "Equipo de construcción.")
    )
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { SeccionTitulo("Roles en Scrum", AgilePrimary) }
        items(roles) { ItemCard(it, AgilePrimary) }
    }
}

@Composable
private fun SeccionPruebas() {
    val pruebas = listOf(
        Item("Unitarias", "Componentes aislados."),
        Item("Sistema", "Funcionamiento global.")
    )
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { SeccionTitulo("Tipos de Pruebas", AgileSecondary) }
        items(pruebas) { ItemCard(it, AgileSecondary) }
    }
}

@Composable
private fun Header(nombre: String) {
    Surface(
        color = AgilePrimary,
        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape),
                color = Color.White.copy(alpha = 0.2f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = nombre.first().toString(), color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = "Hola, $nombre", color = Color.White, fontWeight = FontWeight.Bold)
                Text(text = "Anotaciones de clase", color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun SeccionTitulo(titulo: String, color: Color) {
    Text(
        text = titulo,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = color,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
private fun TextoCard(texto: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AgileSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Text(text = texto, modifier = Modifier.padding(16.dp), color = AgileTextSecondary)
    }
}

@Composable
private fun BulletCard(texto: String, color: Color) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = AgileSurface)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(color))
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = texto, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun ItemCard(item: Item, color: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AgileSurface)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(color = color.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp), modifier = Modifier.size(40.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = item.titulo.first().toString(), fontWeight = FontWeight.Bold, color = color)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = item.titulo, fontWeight = FontWeight.Bold)
                Text(text = item.descripcion, style = MaterialTheme.typography.bodySmall, color = AgileTextSecondary)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PantallaPrincipalPreview() {
    MaterialTheme(colorScheme = AgileColorScheme) {
        PantallaPrincipal("Juan Manuel")
    }
}