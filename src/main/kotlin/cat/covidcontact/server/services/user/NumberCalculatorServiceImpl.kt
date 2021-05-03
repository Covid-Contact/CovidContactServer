package cat.covidcontact.server.services.user

import kotlin.random.Random

class NumberCalculatorServiceImpl : NumberCalculatorService {
    private val minNumber = 1000
    private val maxNumber = 9999
    private val accessCodeDigits = 6

    override fun generateRandomNumber() = Random.nextInt(minNumber, maxNumber)

    override fun generateAccessCode(): String {
        var accessCode = ""
        repeat(accessCodeDigits) {
            accessCode += Random.nextInt(0, 9)
        }

        return accessCode
    }
}
