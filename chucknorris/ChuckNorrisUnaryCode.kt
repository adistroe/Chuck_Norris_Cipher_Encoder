package chucknorris

/*  Chuck Norris Unary Code
    ... because Chuck Norris encrypts only with zeros   */
class ChuckNorrisUnaryCode {

    val inputOperationMsg: String
        get() = MSG.INPUT_OPERATION.txt

    val exitCmd: String
        get() = MSG.EXIT.txt

    val byeMsg: String
        get() = MSG.BYE.txt

    val encodeCmd: String
        get() = MSG.ENCODE.txt

    val decodeCmd: String
        get() = MSG.DECODE.txt

    val noOperationMsg: String
        get() = MSG.NO_OPERATION.txt

    val inputStringMsg: String
        get() = MSG.INPUT_STRING.txt

    val inputEncodedStringMsg: String
        get() = MSG.INPUT_ENCODED_STRING.txt

    private enum class MSG(val txt: String) {
        INPUT_OPERATION("Please input operation (encode/decode/exit):"),
        INPUT_STRING("Input string:"),
        INPUT_ENCODED_STRING("Input encoded string:"),
        ENCODED_STRING("Encoded string:"),
        DECODED_STRING("Decoded string:"),
        NO_OPERATION("There is no '%s' operation"),
        NOT_VALID("Encoded string is not valid. %s"),
        INVALID_CHAR("Only '0' and 'space' characters are allowed."),
        INVALID_BLOCK_HEADER("Bad block header."),
        ODD_BLOCK_NUMBER("Odd block number."),
        NOT_MULTIPLE_OF_SEVEN("Must be a multiple of 7."),
        ENCODE("encode"),
        DECODE("decode"),
        EXIT("exit"),
        BYE("Bye!"),
    }

    private enum class CONST(val value: String) {
        DELIMITER(" "),
        ZEROES_BLOCK_HEADER("00"),
        ONES_BLOCK_HEADER("0"),
    }

    private fun hasValidChars(input: String): Boolean {
        val delimiter = CONST.DELIMITER.value
        val allowedCharSet = "0$delimiter"

        for (ch in input) {
            if (ch !in allowedCharSet) {
                return false
            }
        }
        return true
    }

    private fun hasEvenNumberOfBlocks(input: String): Boolean {
        val delimiter = CONST.DELIMITER.value.first()
        //  We count the number of spaces. Even number of blocks will have an odd number of spaces (and vice-versa)
        val count = input.count { it == delimiter }
        return count % 2 == 1
    }

    private fun hasValidBlockHeader(input: String): Boolean {
        val zeroesHeader = CONST.ZEROES_BLOCK_HEADER.value
        val onesHeader = CONST.ONES_BLOCK_HEADER.value
        return input == zeroesHeader || input == onesHeader
    }

    private fun isValidBinaryString(input: String): Boolean = input.length % 7 == 0

    fun encode(input: String): String {
        val zeroesHeader = CONST.ZEROES_BLOCK_HEADER.value
        val onesHeader = CONST.ONES_BLOCK_HEADER.value
        val delimiter = CONST.DELIMITER.value
        val encStrMsg = MSG.ENCODED_STRING.txt

        // matches blocks of 1's or 0's
        val regex = "(?<=([01]))(?!\\1)".toRegex()

        // split into blocks of 1's and 0's
        val binaryBlocks = to7BitBinaryString(input).split(regex).toMutableList()
        //  because last block is empty
        if (binaryBlocks.last().isEmpty()) binaryBlocks.removeLast()
        val encodedString = StringBuilder()

        for (block in binaryBlocks) {
            val header = if (block.first() == '0') zeroesHeader else onesHeader
            encodedString.append(
                "$header$delimiter",
                block.replace('1', '0'),
                delimiter
            )
        }
        println(encStrMsg)
        return encodedString.toString()
    }

    fun decode(input: String): String {
        val invalidCharSet = MSG.INVALID_CHAR.txt
        val invalidString = MSG.NOT_VALID.txt
        val oddBlockNumber = MSG.ODD_BLOCK_NUMBER.txt
        val badBlockHeader = MSG.INVALID_BLOCK_HEADER.txt

        if (!hasValidChars(input))
            return String.format(invalidString, invalidCharSet)

        if (!hasEvenNumberOfBlocks(input))
            return String.format(invalidString, oddBlockNumber)

        val zeroesHeader = CONST.ZEROES_BLOCK_HEADER.value
        val delimiter = CONST.DELIMITER.value
        //  matches groups of two blocks
        val regex = "0+\\s0+".toRegex()
        //  split into groups of two blocks
        val blockGroups = regex.findAll(input).map { it.value }.toList()
        val decodedString = StringBuilder()

        for (group in blockGroups) {
            val header = group.split(delimiter).first()
            if (!hasValidBlockHeader(header))
                return String.format(invalidString, badBlockHeader)

            val isZeroesBlockHeader = header == zeroesHeader
            val payload = group.split(delimiter).last()
            val data = if (isZeroesBlockHeader) payload else payload.replace('0', '1')
            decodedString.append(data)
        }
        return from7BitBinaryString(decodedString.toString())
    }

    private fun from7BitBinaryString(input: String): String {
        val invalidString = MSG.NOT_VALID.txt
        val notMultipleOfSeven = MSG.NOT_MULTIPLE_OF_SEVEN.txt
        val decStrMsg = MSG.DECODED_STRING.txt

        if (!isValidBinaryString(input))
            return String.format(invalidString, notMultipleOfSeven)

        val requiredLength = 7
        val binaryGroups = input.chunked(requiredLength)
        val result = StringBuilder()

        for (group in binaryGroups) {
            result.append(
                Integer.parseInt(group, 2).toChar()
            )
        }
        println(decStrMsg)
        return result.toString()
    }

    private fun to7BitBinaryString(input: String): String {
        //  keep only the last 7 bits
        val hexMask = 0x7F
        val requiredLength = 7
        // left-padding for strings with less than 7 characters
        val padWithChar = '0'

        val byteArray = input.toByteArray(Charsets.US_ASCII) // 7-bit encoding
        val binaryString = StringBuilder()

        for (byte in byteArray) {
            binaryString.append(
                Integer
                    .toBinaryString(byte.toInt() and hexMask)   // convert each byte to binary string
                    .padStart(requiredLength, padWithChar) // left-padding
            )
        }
        return binaryString.toString()
    }
}