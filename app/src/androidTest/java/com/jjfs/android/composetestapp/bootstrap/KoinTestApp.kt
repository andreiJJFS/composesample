package com.jjfs.android.composetestapp.bootstrap

import androidx.test.platform.app.InstrumentationRegistry
import com.jjfs.android.composetestapp.AndroidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.module.Module


//class KoinTestApp: AndroidApplication() {
//
//    override fun onCreate() {
//        super.onCreate()
//    }
//
//    internal fun injectModule(module: Module) {
//        loadKoinModules(module)
//    }
//
//    fun injectModules(modules: List<Module>) {
//        val context = InstrumentationRegistry.getInstrumentation().targetContext
//        try {
//            startKoin {
//                modules(emptyList())
//                androidContext(context)
//            }
//        } catch (ex: Throwable) {
//            // ignore
//        }
//        loadKoinModules(modules)
//    }
//}
