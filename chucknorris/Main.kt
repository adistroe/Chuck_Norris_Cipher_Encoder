package chucknorris

fun main() {
    val cnuc = ChuckNorrisUnaryCode()

    do {
        println(cnuc.inputOperationMsg)
        val input = readln()
        when (input) {
            cnuc.encodeCmd -> {
                println(cnuc.inputStringMsg)
                val output = cnuc.encode(readln())
                println("$output\n")
            }

            cnuc.decodeCmd -> {
                println(cnuc.inputEncodedStringMsg)
                val output = cnuc.decode(readln())
                println("$output\n")
            }

            cnuc.exitCmd -> println(cnuc.byeMsg)

            else -> println("${String.format(cnuc.noOperationMsg, input)}\n")
        }
    } while (input != cnuc.exitCmd)
}