package gt.uvg.pmproject.ui.components

import gt.uvg.pmproject.model.EventType

fun EventType.label(): String = when (this) {
    EventType.WEDDING -> "Boda"
    EventType.BIRTHDAY -> "Cumpleaños"
    EventType.CORPORATE -> "Corporativo"
    EventType.GRADUATION -> "Graduación"
    EventType.OTHER -> "Otro"
}
