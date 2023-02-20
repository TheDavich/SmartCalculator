package calculator

import java.math.BigInteger

class Calculator {
    private val commandRegex = Regex("""/[a-z]+""")
    private val expressionRegex = Regex("""-?(?:-?[0-9a-zA-Z()]+([-+]+|[*/]))+-?[0-9a-zA-Z()]+""")

    private val variableRegex = Regex("""\w+(=[-+]?\w+)*""")
    private val globalVariables = mutableMapOf<String, BigInteger>()
    private val errors = mapOf(
        "InvalidAssignment" to "Invalid assignment",
        "InvalidExpression" to "Invalid expression",
        "InvalidIdentifier" to "Invalid identifier",
        "UnknownCommand" to "Unknown command",
        "UnknownVariable" to "Unknown variable"
    )

    fun run() {
        while (true) {
            val inputString = readln()
            val inputForMatch = inputString.replace("\\s*".toRegex(), "")
            try {
                when {
                    inputForMatch.isBlank() -> continue
                    inputForMatch.matches(variableRegex)  -> processVariable(inputForMatch)
                    inputForMatch.matches(expressionRegex) -> processExpression(inputForMatch)
                    inputForMatch.matches(commandRegex) -> {
                        when (inputString) {
                            "/exit" -> break
                            "/help" -> showHelp()
                            else -> printError("UnknownCommand")
                        }
                    }
                    else -> printError("InvalidExpression")
                }
            } catch(e: Exception) {
                continue
            }
        }
        println("Bye!")
    }

    private fun processVariable(expression: String) {
        val variables = expression.split("=")
        when (variables.size) {
            1 -> printVariable(variables.first())
            2 -> setVariable(variables)
            else -> printError("InvalidAssignment")
        }
    }

    private fun printVariable(variable: String) {
        if (!variable.matches("""[a-zA-Z]+""".toRegex())) {
            printError("InvalidIdentifier")
        } else if (globalVariables.containsKey(variable)) {
            println(globalVariables[variable])
        } else {
            printError("UnknownVariable")
        }
    }

    private fun setVariable(variables: List<String>) {
        val leftValue = variables.first()
        val rightValue = variables.last()

        when {
            !leftValue.matches("""[a-zA-Z]+""".toRegex()) -> printError("InvalidIdentifier")
            rightValue.matches("""-?\d+""".toRegex()) -> globalVariables[leftValue] = rightValue.toBigInteger()
            rightValue.matches("""-?[a-zA-Z]+""".toRegex()) -> {
                if (globalVariables.containsKey(rightValue)) {
                    globalVariables[leftValue] = BigInteger(globalVariables[rightValue]!!.toString())
                } else {
                    printError("UnknownVariable")
                }
            }
            else -> printError("InvalidAssignment")
        }
    }

    private fun processExpression(expression: String) {
        if (expression.count { it == '(' } != expression.count { it == ')' }) {
            printError("InvalidExpression")
            return
        }

        val members = expression.split("(?<=[\\d|a-zA-Z)(])(?=\\D)|(?<=\\D)(?=[\\d|a-zA-Z)(])".toRegex()).toMutableList()

        simplifyOperators(members)
        val postfix = infixToPostfix(members)
        calculate(postfix)
    }

    private fun simplifyOperators(members: MutableList<String>) {
        for(index in 0..members.lastIndex) {
            if (members[index].matches("\\++".toRegex())) {
                members[index] = "+"
            }

            if (members[index].matches("-+".toRegex())) {
                members[index] = if (members[index].length % 2 == 0) "+" else "-"
            }
        }
        members.replaceAll { it.replace("\\s*", "") }
    }

    fun getValue(inputValue: String): BigInteger {
        return try {
            inputValue.toBigInteger()
        } catch (e: NumberFormatException) {
            val result: BigInteger
            if(globalVariables.containsKey(inputValue)) {
                result = globalVariables[inputValue]!!
            } else {
                printError("UnknownVariable")
                throw Exception("Unknown variable")
            }
            result
        }
    }

    private fun infixToPostfix(infix: MutableList<String>): List<String> {
        val postfixStack = mutableListOf<String>()
        val tempStack = mutableListOf<String>()
        for (s in infix) {
            if (s.matches("[\\d|a-zA-Z]+".toRegex())) {
                postfixStack += s
            } else if (s == "(") {
                tempStack.add(s)
            } else if (s == ")") {
                while (tempStack.isNotEmpty() && tempStack.last() != "(") {
                    postfixStack += tempStack.last()
                    tempStack.removeAt(tempStack.lastIndex)
                }
                if (tempStack.isNotEmpty() && tempStack.last() != "(") {
                    printError("InvalidExpression")
                    return emptyList()
                } else {
                    tempStack.removeAt(tempStack.lastIndex)
                }
            } else {
                while (tempStack.isNotEmpty() && precedence(s) <= precedence(tempStack.last())) {
                    postfixStack += tempStack.last()
                    tempStack.removeAt(tempStack.lastIndex)
                }
                tempStack.add(s)
            }
        }

        while (tempStack.isNotEmpty()) {
            postfixStack += tempStack.last()
            tempStack.removeAt(tempStack.lastIndex)
        }

        return postfixStack.toList()
    }

    private fun precedence(operand: String): Int {
        return when (operand) {
            "+","-" -> 1
            "*", "/" -> 2
            else -> -1
        }
    }

    private fun calculate(postfixStack: List<String>) {
        val calculationStack = mutableListOf<BigInteger>()
        for (el in postfixStack) {
            when {
                el.matches("[\\d|a-zA-Z]+".toRegex()) -> {
                    calculationStack.add(getValue(el))
                }
                el == "*" -> {
                    val op1 = calculationStack.last()
                    calculationStack.removeAt(calculationStack.lastIndex)
                    val op2 = calculationStack.last()
                    calculationStack.removeAt(calculationStack.lastIndex)
                    calculationStack.add(op2 * op1)
                }
                el == "/" -> {
                    val op1 = calculationStack.last()
                    calculationStack.removeAt(calculationStack.lastIndex)
                    val op2 = calculationStack.last()
                    calculationStack.removeAt(calculationStack.lastIndex)
                    calculationStack.add(op2 / op1)
                }
                el == "+" -> {
                    val op1 = calculationStack.last()
                    calculationStack.removeAt(calculationStack.lastIndex)
                    val op2 = calculationStack.last()
                    calculationStack.removeAt(calculationStack.lastIndex)
                    calculationStack.add(op2 + op1)
                }
                el == "-" -> {
                    val op1 = calculationStack.last()
                    calculationStack.removeAt(calculationStack.lastIndex)
                    val op2 = calculationStack.last()
                    calculationStack.removeAt(calculationStack.lastIndex)
                    calculationStack.add(op2 - op1)
                }
            }
        }

        val result = calculationStack.last()
        println(result)
    }

    private fun showHelp() {
        println("The program calculates arithmetic operation.")
        println("Your can enter several same operators following each other, e.g.: 9 +++ 10 -- 8.")
        println("The even number of minuses gives a plus, and the odd number of minuses gives a minus!")
        println("Look at it this way: 2 -- 2 equals 2 - (-2) equals 2 + 2.")
        println("You cannot specify several * or / operation as it is possible for + and -.")
        println("It is possible to declare a variable: a=5 and use it in an expression.")
        println("Valid name of a variable contains only letters.")
    }

    private fun printError(errorCode: String) {
        if(errors.containsKey(errorCode)) println(errors.getValue(errorCode))
        else println("UNKNOWN ERROR")
    }
}



