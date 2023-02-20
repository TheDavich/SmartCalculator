import java.math.BigInteger

fun main() {
    val a = BigInteger(readln())
    val b = BigInteger(readln())
    val r = a % b
    val q = (a - r) / b
    println("$a = $b * $q + $r")
}