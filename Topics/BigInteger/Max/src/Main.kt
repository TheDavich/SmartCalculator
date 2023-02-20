import java.math.BigInteger

fun main() {
    val a = BigInteger(readln())
    val b = BigInteger(readln())
    if (a > b) {
        println(a)
    } else {
        println(b)
    }
}