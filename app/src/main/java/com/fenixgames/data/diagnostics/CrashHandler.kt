package com.fenixgames.data.diagnostics

import com.fenixgames.domain.diagnostics.DiagnosticLevel

object CrashHandler {
    fun install(logger: BlackBoxLogger) {
        val previous = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            logger.logBlocking(
                BlackBoxLogger.event(
                    level = DiagnosticLevel.FATAL,
                    tag = "CrashHandler",
                    message = "Uncaught exception in ${thread.name}",
                    throwable = throwable
                )
            )
            previous?.uncaughtException(thread, throwable)
        }
    }
}

