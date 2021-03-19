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

    @GetMapping(UserControllerUrls.VALIDATE)
    fun validate(@RequestParam(required = true) code: String) = try {
        userService.validateUser(code)
        ResponseEntity<String>("You can now log in", HttpStatus.OK)
    } catch (e: UserException.InvalidId) {
        ResponseEntity<String>("The id is invalid", HttpStatus.BAD_REQUEST)
    }
}