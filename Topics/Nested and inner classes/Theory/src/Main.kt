class Pumpkin(val isForHalloween: Boolean) {
    fun addCandle() {
        if (isForHalloween) {
            val candle = Candle()
            candle.burning()
        } else {
            println("We don't need a candle.")
        }
    }
    class Candle() {
        fun burning() {
            println("The candle is burning")
        }
    }
}

fun main(args: Array<String>) {
    val pumpkin1 = Pumpkin(true)
    val pumpkin2 = Pumpkin(false)

    pumpkin1.addCandle() // Output: The candle is burning.
    pumpkin2.addCandle() // Output: We don't need a candle.
}
