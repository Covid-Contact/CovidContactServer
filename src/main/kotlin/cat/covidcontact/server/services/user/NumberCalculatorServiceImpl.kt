package cat.covidcontact.server.services.user

import kotlin.random.Random

class NumberCalculatorServiceImpl : NumberCalculatorService {
    private val minNumber = 1000
    private val maxNumber = 9999

    override fun generateUsernameNumber() = Random.nextInt(minNumber, maxNumber)
}
