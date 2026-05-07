package com.fenixgames.data.diagnostics

import android.content.Context
import android.net.Uri
import com.fenixgames.domain.diagnostics.DiagnosticLevel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DiagnosticRepository(
    private val context: Context,
    private val logger: BlackBoxLogger
) {
    suspend fun logInfo(tag: String, message: String) {
        logger.log(BlackBoxLogger.event(DiagnosticLevel.INFO, tag, message))
    }

    suspend fun logError(tag: String, message: String, throwable: Throwable? = null) {
        logger.log(BlackBoxLogger.event(DiagnosticLevel.ERROR, tag, message, throwable))
    }

    suspend fun readReport(): String = logger.readSanitized()

    suspend fun exportReport(uri: Uri) = withContext(Dispatchers.IO) {
        val report = logger.readSanitized()
        context.contentResolver.openOutputStream(uri)?.use { output ->
            output.write(report.toByteArray(Charsets.UTF_8))
        } ?: error("No se pudo abrir destino SAF")
    }
}

