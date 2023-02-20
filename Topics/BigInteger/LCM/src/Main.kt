import java.math.BigInteger

fun main() {
    val a = BigInteger(readln())
    val b = BigInteger(readln())
    val lcm = a / a.gcd(b) * b
    println(lcm)
}