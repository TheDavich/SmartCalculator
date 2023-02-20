fun removing(currentMap: MutableMap<Int, String>, value: String): MutableMap<Int, String> {
    val result = mutableMapOf<Int, String>()
    for ((key, v) in currentMap) {
        if (v != value) {
            result[key] = v
        }
    }
    return result
}
