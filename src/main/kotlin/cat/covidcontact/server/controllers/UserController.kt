package cat.covidcontact.server.controllers

import cat.covidcontact.server.data.ApplicationUser
import cat.covidcontact.server.services.user.UserService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(UserControllerUrls.BASE)
class UserController(
    private val userService: UserService
) {

    @PostMapping(UserControllerUrls.SIGN_UP)
    fun signUp(@RequestBody user: ApplicationUser) {
        userService.createUser(user)
    }
}