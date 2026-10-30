package gt.uvg.pmproject.model

enum class EventType {
    WEDDING,
    BIRTHDAY,
    CORPORATE,
    GRADUATION,
    OTHER;

    companion object {
        fun fromName(name: String?): EventType =
            entries.firstOrNull { it.name == name } ?: OTHER
    }
}
