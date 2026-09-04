package com.example.labo_android_semana_02.data.mapper

import com.example.labo_android_semana_02.data.local.entities.CategoriaEntity
import com.example.labo_android_semana_02.data.local.entities.ReporteEntity
import com.example.labo_android_semana_02.domain.Categoria
import com.example.labo_android_semana_02.domain.Reporte

fun CategoriaEntity.toDomain() = Categoria(id, nombre, icono)
fun Categoria.toEntity() = CategoriaEntity(id, nombre, icono)

fun ReporteEntity.toDomain() = Reporte(id, titulo, descripcion, fecha, categoriaId, resuelto)
fun Reporte.toEntity() = ReporteEntity(id, titulo, descripcion, fecha, categoriaId, resuelto)
