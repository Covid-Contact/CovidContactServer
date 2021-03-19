package cat.covidcontact.server.controllers

import cat.covidcontact.server.data.applicationuser.ApplicationUser
import cat.covidcontact.server.services.user.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(UserControllerUrls.BASE)
class UserController(
    private val userService: UserService
) {

    @PostMapping(UserControllerUrls.SIGN_UP)
    fun signUp(@RequestBody user: ApplicationUser) = try {
        userService.createUser(user)
        ResponseEntity<String>(HttpStatus.CREATED)
    } catch (e: UserException.UserExisting) {
        ResponseEntity<String>("The user already exists", HttpStatus.BAD_REQUEST)
    }

    @PostMapping(UserControllerUrls.VALIDATE)
    fun validate(@PathVariable validateId: String) = try {
        userService.validateUser(validateId)
        ResponseEntity<String>(HttpStatus.OK)
    } catch (e: UserException.InvalidId) {
        ResponseEntity<String>("The id is invalid", HttpStatus.BAD_REQUEST)
    }
}