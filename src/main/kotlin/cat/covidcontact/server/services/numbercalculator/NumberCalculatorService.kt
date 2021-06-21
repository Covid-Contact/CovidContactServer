package cat.covidcontact.server.services.numbercalculator

interface NumberCalculatorService {
    fun generateRandomNumber(): Int

    fun generateAccessCode(): String
}
