package gt.uvg.pmproject.data
interface AuthRepository {
    suspend fun ensureSignedIn()
}