package org.example.project

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.example.project.data.Horas
import org.example.project.data.repository.ControlRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.*
import kotlin.time.Duration.Companion.milliseconds

private val clock: Clock get() = Clock.System
fun getCurrentTime(): Instant = clock.now()

class HomeViewModel(
    private val controlRepository: ControlRepository = ControlRepository()) : ViewModel() {


    private val _pcState = MutableStateFlow(false)
    val pcState: StateFlow<Boolean> = _pcState.asStateFlow()

    private val _isPcToggling = MutableStateFlow(false)
    val isPcToggling: StateFlow<Boolean> = _isPcToggling.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _horasUiState = MutableStateFlow<HorasUiState>(HorasUiState.Initial)
    val horasUiState: StateFlow<HorasUiState> = _horasUiState.asStateFlow()

    private val _currentUserHours = MutableStateFlow<Horas?>(null)
    val currentUserHours: StateFlow<Horas?> = _currentUserHours.asStateFlow()

    private val _uniqueWorkingDaysCount = MutableStateFlow(0)
    val uniqueWorkingDaysCount: StateFlow<Int> = _uniqueWorkingDaysCount.asStateFlow()

    private val _weeklyUsageSummary = MutableStateFlow<Map<String, Long>>(emptyMap())
    val weeklyUsageSummary: StateFlow<Map<String, Long>> = _weeklyUsageSummary.asStateFlow()

    fun formatMillisToTime(millis: Long): String {
        if (millis < 0) return "00:00:00"
        val duration = millis.milliseconds
        val hours = duration.inWholeHours
        val minutes = duration.inWholeMinutes % 60
        val seconds = duration.inWholeSeconds % 60
        return "${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
    }

    fun timeStringToMinutes(timeString: String): Int {
        val parts = timeString.split(':')
        return if (parts.size >= 2) {
            val hours = parts[0].toIntOrNull() ?: 0
            val minutes = parts[1].toIntOrNull() ?: 0
            (hours * 60) + minutes
        } else {
            0
        }
    }

    private fun LocalDate.startOfWeek(firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY): LocalDate {
        val daysFromStart = (dayOfWeek.isoDayNumber - firstDayOfWeek.isoDayNumber + 7) % 7
        return this.minus(daysFromStart, DateTimeUnit.DAY)
    }

    fun isSameWeek(date1: LocalDate, date2: LocalDate): Boolean {
        return date1.startOfWeek() == date2.startOfWeek()
    }

    private fun parseDateTime(dateTimeString: String?): Instant? {
        if (dateTimeString == null) return null
        return try {
            Instant.parse(dateTimeString)
        } catch (e: Exception) {
            try {
                Instant.parse(dateTimeString.replace(" ", "T") + "Z")
            } catch (e2: Exception) {
                null
            }
        }
    }

    private fun isSameDay(instant1: Instant?, instant2: Instant?): Boolean {
        if (instant1 == null || instant2 == null) return false
        val tz = TimeZone.currentSystemDefault()
        val d1 = instant1.toLocalDateTime(tz).date
        val d2 = instant2.toLocalDateTime(tz).date
        return d1 == d2
    }

    private fun calculateWeeklyWorkDayUsage(horasList: List<Horas>, userId: String) {
        val usageMap = mutableMapOf<String, Long>()
        val tz = TimeZone.currentSystemDefault()
        //val now = kotlinx.datetime.Clock.System.now()
        val now = getCurrentTime()
        val today = now.toLocalDateTime(tz).date
        val currentDayOfWeekValue = today.dayOfWeek.isoDayNumber

        horasList.filter { it.user == userId }
            .forEach { horas ->
                val startInstant = parseDateTime(horas.hora_encendido) ?: return@forEach
                val startDateTime = startInstant.toLocalDateTime(tz)
                val startDate = startDateTime.date
                val dayOfWeek = startDate.dayOfWeek

                if (dayOfWeek.isoDayNumber in 1..5) { // Monday to Friday
                    val dayName = when (dayOfWeek) {
                        DayOfWeek.MONDAY -> "Lunes"
                        DayOfWeek.TUESDAY -> "Martes"
                        DayOfWeek.WEDNESDAY -> "Miércoles"
                        DayOfWeek.THURSDAY -> "Jueves"
                        DayOfWeek.FRIDAY -> "Viernes"
                        else -> return@forEach
                    }

                    if (isSameWeek(today, startDate)) {
                        val endInstant = parseDateTime(horas.hora_apagado) ?: now
                        val duration = (endInstant.toEpochMilliseconds() - startInstant.toEpochMilliseconds()).coerceAtLeast(0L)
                        // Forma segura de actualizar el mapa para iOS
                        val currentDuration = usageMap[dayName] ?: 0L
                        usageMap[dayName] = currentDuration + duration
                    }
                }
            }

        val dayNameToValue = mapOf(
            "Lunes" to 1, "Martes" to 2, "Miércoles" to 3,
            "Jueves" to 4, "Viernes" to 5
        )

        val filteredUsageMap = usageMap.filter { (dayName, _) ->
            val dayValue = dayNameToValue[dayName] ?: Int.MAX_VALUE
            dayValue <= currentDayOfWeekValue
        }

        _weeklyUsageSummary.value = filteredUsageMap
    }

    fun fetchHoras(currentUserId: String) {
        if (_horasUiState.value is HorasUiState.Loading) return

        viewModelScope.launch {
            _horasUiState.value = HorasUiState.Loading
            _currentUserHours.value = null
            _weeklyUsageSummary.value = emptyMap()

            val result = controlRepository.listaHoras()

            result.fold(
                onSuccess = { horasList ->
                    //val now = kotlinx.datetime.Clock.System.now()
                    val now = getCurrentTime()
                    val matchingEntry = horasList.find { horas ->
                        val isUserMatch = horas.user == currentUserId
                        val startInstant = parseDateTime(horas.hora_encendido)
                        isUserMatch && isSameDay(startInstant, now)
                    }
                    _currentUserHours.value = matchingEntry
                    calculateWeeklyWorkDayUsage(horasList, currentUserId)
                    _horasUiState.value = HorasUiState.Success(horasList)
                },
                onFailure = { e ->
                    val error = "Error al cargar las horas: ${e.message}"
                    _horasUiState.value = HorasUiState.Error(error)
                    _currentUserHours.value = null
                    _weeklyUsageSummary.value = emptyMap()
                }
            )
        }
    }
}
