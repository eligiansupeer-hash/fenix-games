package com.fenixgames.data.diagnostics

import android.content.Context
import com.fenixgames.domain.diagnostics.DiagnosticEvent
import com.fenixgames.domain.diagnostics.DiagnosticLevel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BlackBoxLogger(
    context: Context,
    private val maxBytes: Long = 5L * 1024L * 1024L
) {
    private val logFile = File(context.filesDir, "fenix-blackbox.log")
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US)

    suspend fun log(event: DiagnosticEvent) = withContext(Dispatchers.IO) {
        rotateIfNeeded()
        logFile.appendText(format(event))
    }

    fun logBlocking(event: DiagnosticEvent) {
        rotateIfNeeded()
        logFile.appendText(format(event))
    }

    suspend fun readSanitized(): String = withContext(Dispatchers.IO) {
        if (!logFile.exists()) return@withContext ""
        sanitize(logFile.readText())
    }

    private fun rotateIfNeeded() {
        if (!logFile.exists() || logFile.length() <= maxBytes) return
        val bytes = logFile.readBytes()
        val keepFrom = (bytes.size - (maxBytes / 2).toInt()).coerceAtLeast(0)
        logFile.writeBytes(bytes.copyOfRange(keepFrom, bytes.size))
        logFile.appendText("\n${timestamp()} WARNING BlackBox Log rotator activated\n")
    }

    private fun format(event: DiagnosticEvent): String {
        val trace = event.throwable?.stackTraceToString().orEmpty()
        val body = buildString {
            append(timestamp(event.timestampMillis))
            append(' ')
            append(event.level.name)
            append(' ')
            append(event.tag)
            append(" - ")
            append(event.message)
            if (trace.isNotBlank()) {
                append('\n')
                append(trace)
            }
            append('\n')
        }
        return sanitize(body)
    }

    private fun timestamp(value: Long = System.currentTimeMillis()): String =
        dateFormat.format(Date(value))

    private fun sanitize(raw: String): String = raw
        .replace(Regex("""\b(?:\d{1,3}\.){3}\d{1,3}\b"""), "[ip-redacted]")
        .replace(Regex("""[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}"""), "[email-redacted]")
        .replace(Regex("""(?i)(player\s+)[A-Za-z0-9_-]+"""), "$1[player-redacted]")

    companion object {
        fun event(
            level: DiagnosticLevel,
            tag: String,
            message: String,
            throwable: Throwable? = null
        ): DiagnosticEvent = DiagnosticEvent(level, tag, message, throwable)
    }
}

