fun summator(map: Map<Int, Int>): Int {
    var sum = 0
    for ((key, value) in map) {
        if (key % 2 == 0) {
            sum += value
        }
    }
    return sum
}
