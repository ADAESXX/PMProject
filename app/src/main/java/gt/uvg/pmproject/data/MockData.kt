package gt.uvg.pmproject.data

import gt.uvg.pmproject.model.EventType
import gt.uvg.pmproject.model.Quotation
import gt.uvg.pmproject.model.Service
import java.time.LocalDate

object MockData {
    val quotations = listOf(
        Quotation(
            id = "1",
            client = "Familia Martínez",
            eventType = EventType.BODA,
            date = LocalDate.of(2026,12,12),
            services = listOf(
                Service(
                    id = "s1",
                    name = "Fotografía",
                    provider = "Estudio Luz Clara",
                    quantity = 1,
                    unitPrice = 4500.0
                )
            ),
            status = "Aprobada",
            amount = 4500.0
        ),
        Quotation(
            id = "4",
            client = "Carlos Gómez",
            eventType = EventType.ANIVERSARIO,
            date = LocalDate.of(2026, 8, 7),
            services = listOf(
                Service(
                    id = "s4",
                    name = "Música en Vivo",
                    provider = "Banda Armonía",
                    quantity = 1,
                    unitPrice = 1200.0
                )
            ),
            status = "Activa",
            amount = 1200.0
        ),
        Quotation(
            id = "5",
            client = "Colegio Interamericano",
            eventType = EventType.GRADUACION,
            date = LocalDate.of(2026,3,14),
            services = listOf(
                Service(
                    id = "s5",
                    name = "Audio e Iluminación",
                    provider = "ProSound Events",
                    quantity = 1,
                    unitPrice = 3500.0
                )
            ),
            status = "Aprobada",
            amount = 3500.0
        ),
        Quotation(
            id = "2",
            client = "TechCorp Inc.",
            eventType = EventType.CORPORATIVO,
            date = LocalDate.of(2026, 5, 7),
            services = listOf(
                Service(
                    id = "s2",
                    name = "Catering",
                    provider = "Gourmet Express",
                    quantity = 50,
                    unitPrice = 57.0
                )
            ),
            status = "Enviada",
            amount = 2850.0
        ),
        Quotation(
            id = "3",
            client = "Ana López",
            eventType = EventType.CUMPLEANOS,
            date = LocalDate.of(2026,10,11),
            services = listOf(
                Service(
                    id = "s3",
                    name = "Decoración",
                    provider = "Florería El Encanto",
                    quantity = 1,
                    unitPrice = 300.0
                )
            ),
            status = "Enviada",
            amount = 800.0
        )
    )
}