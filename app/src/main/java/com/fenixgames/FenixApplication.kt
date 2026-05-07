package com.fenixgames

import android.app.Application
import com.fenixgames.core.di.AppContainer
import com.fenixgames.data.diagnostics.CrashHandler

class FenixApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        CrashHandler.install(container.blackBoxLogger)
    }
}

