package com.example.labo_android_semana_02.data.mapper

import com.example.labo_android_semana_02.data.local.entities.ActividadEntity
import com.example.labo_android_semana_02.data.local.entities.CategoriaEntity
import com.example.labo_android_semana_02.data.local.entities.ReporteEntity
import com.example.labo_android_semana_02.data.remote.dto.ActividadDto
import com.example.labo_android_semana_02.domain.ActividadFormativa
import com.example.labo_android_semana_02.domain.Categoria
import com.example.labo_android_semana_02.domain.Prioridad
import com.example.labo_android_semana_02.domain.Reporte

// Categoría Mappers
fun CategoriaEntity.toDomain() = Categoria(id, nombre, icono)
fun Categoria.toEntity() = CategoriaEntity(id, nombre, icono)

// Reporte Mappers
fun ReporteEntity.toDomain() = Reporte(
    id = id,
    titulo = titulo,
    descripcion = descripcion,
    fecha = fecha,
    categoriaId = categoriaId,
    resuelto = resuelto,
    evidenciaFotoUri = evidenciaFotoUri,
    evidenciaArchivoUri = evidenciaArchivoUri
)

fun Reporte.toEntity() = ReporteEntity(
    id = id,
    titulo = titulo,
    descripcion = descripcion,
    fecha = fecha,
    categoriaId = categoriaId,
    resuelto = resuelto,
    evidenciaFotoUri = evidenciaFotoUri,
    evidenciaArchivoUri = evidenciaArchivoUri
)

// Actividad Mappers
fun ActividadEntity.toDomain() = ActividadFormativa(
    id = id,
    titulo = titulo,
    descripcion = descripcion,
    progreso = progreso,
    fechaEntrega = fechaEntrega,
    prioridad = prioridad,
    evidenciaFotoUri = evidenciaFotoUri,
    evidenciaArchivoUri = evidenciaArchivoUri
)

fun ActividadFormativa.toEntity() = ActividadEntity(
    id = id,
    titulo = titulo,
    descripcion = descripcion,
    progreso = progreso,
    fechaEntrega = fechaEntrega,
    prioridad = prioridad,
    resuelto = progreso == 100,
    evidenciaFotoUri = evidenciaFotoUri,
    evidenciaArchivoUri = evidenciaArchivoUri
)

fun ActividadDto.toEntity() = ActividadEntity(
    id = id,
    titulo = titulo,
    descripcion = descripcion,
    progreso = progreso,
    fechaEntrega = fechaEntrega,
    prioridad = Prioridad.MEDIA, // Asumiendo default o calculable si no viene en API
    resuelto = resuelto
)
