package cat.covidcontact.server.controllers.user

import cat.covidcontact.server.controllers.runGet
import cat.covidcontact.server.controllers.runPost
import cat.covidcontact.server.data.applicationuser.ApplicationUser
import cat.covidcontact.server.data.user.User
import cat.covidcontact.server.services.user.UserService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(UserControllerUrls.BASE)
class UserController(
    private val userService: UserService
) {

    @PostMapping(UserControllerUrls.SIGN_UP)
    fun signUp(@RequestBody user: ApplicationUser) = runPost {
        userService.createUser(user)
    }

    @GetMapping(UserControllerUrls.VALIDATE)
    fun validate(@RequestParam(required = true) code: String) = runGet {
        userService.validateUser(code)
        "You can now log in"
    }

    @GetMapping(UserControllerUrls.VALIDATED)
    fun isValid(@RequestParam email: String) = runGet {
        userService.isValidated(email)
    }

    @PostMapping(UserControllerUrls.USER_INFO)
    fun addUserInfo(@RequestBody user: User) = runPost {
        userService.addUserInfo(user)
    }
}
