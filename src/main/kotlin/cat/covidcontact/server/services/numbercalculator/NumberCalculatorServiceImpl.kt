/*
 * Copyright (C) 2021  Albert Pinto
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package cat.covidcontact.server.services.numbercalculator

import org.springframework.stereotype.Service
import kotlin.random.Random

@Service
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
