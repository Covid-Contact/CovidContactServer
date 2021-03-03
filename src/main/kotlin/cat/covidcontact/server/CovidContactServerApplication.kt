package cat.covidcontact.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CovidContactServerApplication

fun main(args: Array<String>) {
    runApplication<CovidContactServerApplication>(*args)
}
