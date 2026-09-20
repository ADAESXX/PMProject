package gt.uvg.pmproject.data

import gt.uvg.pmproject.model.Quotation
import gt.uvg.pmproject.model.Service

object MockData {
    val quotations = listOf(
        Quotation(
            id = "1",
            client = "Familia Martínez",
            eventType = "Boda",
            date = "Actualizada hoy",
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
            id = "2",
            client = "TechCorp Inc.",
            eventType = "Corporativo",
            date = "Ayer",
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
            eventType = "Cumpleaños",
            date = "Hace 2 días",
            services = listOf(
                Service(
                    id = "s3",
                    name = "Decoración",
                    provider = "Florería El Encanto",
                    quantity = 1,
                    unitPrice = 300.0
                )
            ),
            status = "Borrador",
            amount = 800.0
        )
    )
}