package org.example.project.view

import kotlinx.datetime.Instant
import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

actual fun getSystemNow(): Instant {
    // Obtenemos los segundos desde 1970 usando la API nativa de Apple (iOS)
    val seconds = NSDate().timeIntervalSince1970
    // Convertimos a milisegundos y creamos el Instant de Kotlin
    return Instant.fromEpochMilliseconds((seconds * 1000).toLong())
}