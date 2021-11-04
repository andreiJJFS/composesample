package com.jjfs.android.composetestapp

import android.app.Application
import com.jjfs.android.composetestapp.config.AppConfig.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

open class AndroidApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@AndroidApplication)
            modules(appModules)
        }

    }
}