import android.app.Activity
import androidx.test.platform.app.InstrumentationRegistry

import org.koin.core.module.Module

//class KoinTestRule<T: Activity>(activityClass: Class<T>, private val modules: List<Module>): ActivityTestRule<T>(activityClass) {
//
//    override fun beforeActivityLaunched() {
//        super.beforeActivityLaunched()
//        val application: KoinTestApp =
//                InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as KoinTestApp
//        application.injectModules(modules)
//    }
//
//    override fun afterActivityLaunched() {
//        super.afterActivityLaunched()
//    }
//
//    override fun afterActivityFinished() {
//        super.afterActivityFinished()
//    }
//}
