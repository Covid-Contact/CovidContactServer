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

package cat.covidcontact.server.services.user

import cat.covidcontact.server.controllers.user.UserExceptions
import cat.covidcontact.server.model.nodes.device.DeviceRepository
import cat.covidcontact.server.model.nodes.location.*
import cat.covidcontact.server.model.nodes.user.Marriage
import cat.covidcontact.server.model.nodes.user.Occupation
import cat.covidcontact.server.model.nodes.user.User
import cat.covidcontact.server.model.nodes.user.UserRepository
import cat.covidcontact.server.model.post.PostUser
import cat.covidcontact.server.security.encrypt
import cat.covidcontact.server.services.location.LocationService
import cat.covidcontact.server.services.numbercalculator.NumberCalculatorService
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val deviceRepository: DeviceRepository,
    private val countryRepository: CountryRepository,
    private val numberCalculatorService: NumberCalculatorService,
    private val locationService: LocationService
) : UserService {

    @Synchronized
    override fun addUserData(user: PostUser) {
        if (userRepository.existsUserByEmail(user.email)) {
            throw UserExceptions.userDataFound
        }

        val usernameNumber = numberCalculatorService.generateRandomNumber()
        user.username = "${user.username}#$usernameNumber"

        val city = user.city?.let { cityName -> getCity(cityName) }

        val userNode = with(user) {
            User(
                email = email,
                username = username,
                gender = gender,
                birthDate = birthDate,
                studies = studies,
                occupation = occupation,
                marriage = marriage,
                children = children,
                hasBeenPositive = hasBeenPositive,
                isVaccinated = isVaccinated,
                city = city
            )
        }
        userRepository.save(userNode)
    }

    @Synchronized
    override fun getUserData(email: String): User {
        return userRepository.findByEmail(email) ?: throw UserExceptions.userDataNotFound
    }

    @Synchronized
    override fun registerMessagingToken(email: String, token: String) {
        userRepository.findByEmail(email)?.let { user ->
            user.messagingToken = token.encrypt()
            userRepository.save(user)
        }
    }

    @Synchronized
    override fun updateUser(
        newEmail: String,
        newCity: String?,
        newStudies: String?,
        newOccupation: String?,
        newMarriage: String?,
        newChildren: Int?,
        newPositive: Boolean?,
        newVaccinated: Boolean?
    ) {
        userRepository.findByEmail(newEmail)?.let { user ->
            val newUser = user.apply {
                city = newCity?.let { cityName -> getCity(cityName) }
                studies = newStudies ?: studies
                occupation = newOccupation?.let { occupation -> Occupation.valueOf(occupation) }
                    ?: occupation
                marriage = newMarriage?.let { marriage -> Marriage.valueOf(marriage) } ?: marriage
                children = newChildren ?: children
                hasBeenPositive = newPositive ?: hasBeenPositive
                isVaccinated = newVaccinated ?: isVaccinated
            }

            userRepository.save(newUser)
        } ?: UserExceptions.userDataNotFound
    }

    @Synchronized
    override fun makeLogOut(email: String, deviceId: String) {
        userRepository.findByEmail(email)?.let { user ->
            deviceRepository.findDeviceById(deviceId)?.let { device ->
                device.users.find { userDevice -> userDevice.user.email == email }?.isLogged = false
                deviceRepository.save(device)
            } ?: throw UserExceptions.deviceNotFound
        } ?: throw UserExceptions.userDataNotFound
    }

    @Synchronized
    override fun deleteAccount(email: String) {
        userRepository.findByEmail(email)?.let { user ->
            userRepository.delete(user)
        } ?: throw UserExceptions.userDataNotFound
    }

    override fun getAllNonOwnerMembers(contactNetworkName: String): List<User> {
        return userRepository.getAllNonOwnerMembersFromContactNetwork(contactNetworkName)
    }

    private fun getCity(name: String): City? {
        val locationResponse = locationService.getLocationFromName(name)

        val countryName = locationResponse.country
        val regionName = locationResponse.region
        val provinceName = locationResponse.province
        val cityName = locationResponse.city

        return if (!countryName.isNullOrEmpty() && !regionName.isNullOrEmpty()
            && !provinceName.isNullOrEmpty() && !cityName.isNullOrEmpty()
        ) {
            val country = countryRepository.findCountryByName(countryName) ?: Country(countryName)

            val region = country.regions.find { region -> region.name == regionName }
                ?: Region(regionName).also { region ->
                    country.regions.add(region)
                }

            val province = region.provinces.find { province -> province.name == provinceName }
                ?: Province(provinceName).also { province ->
                    region.provinces.add(province)
                }

            val city = province.cities.find { city -> city.name == cityName }
                ?: City(cityName).also { city ->
                    province.cities.add(city)
                }

            countryRepository.save(country)
            city
        } else {
            null
        }
    }
}
