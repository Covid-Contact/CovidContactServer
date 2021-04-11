package cat.covidcontact.server.services.user

import kotlin.random.Random

class NumberCalculatorServiceImpl : NumberCalculatorService {
    private val minNumber = 1000
    private val maxNumber = 9999

    override fun generateRandomNumber() = Random.nextInt(minNumber, maxNumber)
}
