package com.fenixgames.domain.diagnostics

data class DiagnosticEvent(
    val level: DiagnosticLevel,
    val tag: String,
    val message: String,
    val throwable: Throwable? = null,
    val timestampMillis: Long = System.currentTimeMillis()
)

