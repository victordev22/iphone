package org.example.project

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

// Aquí iOS sí suele aceptar Clock.System si está aislado en su propio sourceSet
actual fun getSystemNow(): Instant = Clock.System.now()