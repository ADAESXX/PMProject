package gt.uvg.pmproject

import android.app.Application
import gt.uvg.pmproject.data.AppContainer

class PMProjectApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
