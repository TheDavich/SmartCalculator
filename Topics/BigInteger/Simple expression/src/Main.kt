import java.math.BigInteger

fun main() {
    val a = BigInteger(readln())
    val b = BigInteger(readln())
    val c = BigInteger(readln())
    val d = BigInteger(readln())
    val result = (-a) * b + c - d
    println(result)
}