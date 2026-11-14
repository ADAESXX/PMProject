package gt.uvg.pmproject.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val crashlytics: FirebaseCrashlytics = FirebaseCrashlytics.getInstance()
) : AuthRepository {

    private val signInMutex = Mutex()

    override suspend fun ensureSignedIn() {
        if (auth.currentUser != null) return
        signInMutex.withLock {
            // Otra corrutina pudo iniciar sesión mientras esperábamos el candado.
            if (auth.currentUser != null) return
            try {
                val user = auth.signInAnonymously().await().user
                user?.uid?.let { crashlytics.setUserId(it) }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (exception: Exception) {
                crashlytics.log("Falló el inicio de sesión anónimo")
                crashlytics.recordException(exception)
                throw exception
            }
        }
    }
}
