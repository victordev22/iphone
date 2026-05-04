package org.example.project

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

actual fun getSystemNow(): Instant = Clock.System.now()