package gt.uvg.pmproject.model

data class Service (
    val id: String,
    val name: String,
    val provider: String,
    val quantity: Int,
    val unitPrice:Double
){
    val subTotal: Double get() = quantity * unitPrice
}