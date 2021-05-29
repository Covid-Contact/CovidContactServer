package cat.covidcontact.server.controllers.user

import cat.covidcontact.server.controllers.runGet
import cat.covidcontact.server.controllers.runPost
import cat.covidcontact.server.controllers.runPut
import cat.covidcontact.server.model.authentication.applicationuser.ApplicationUser
import cat.covidcontact.server.model.post.PostDevice
import cat.covidcontact.server.model.post.PostToken
import cat.covidcontact.server.model.post.PostUser
import cat.covidcontact.server.services.applicationuser.ApplicationUserService
import cat.covidcontact.server.services.device.DeviceService
import cat.covidcontact.server.services.user.UserService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(UserControllerUrls.BASE)
class UserController(
    private val applicationUserService: ApplicationUserService,
    private val userService: UserService,
    private val deviceService: DeviceService
) {

    @PostMapping(UserControllerUrls.SIGN_UP)
    fun signUp(@RequestBody user: ApplicationUser) = runPost {
        applicationUserService.createUser(user)
    }

    @GetMapping(UserControllerUrls.VALIDATE)
    fun validate(@RequestParam(required = true) code: String) = runGet {
        applicationUserService.validateUser(code)
        "You can now log in"
    }

    @GetMapping(UserControllerUrls.VALIDATED)
    fun isValid(@RequestParam(required = true) email: String) = runGet {
        applicationUserService.isValidated(email)
    }

    @PostMapping(UserControllerUrls.USER_INFO)
    fun addUserInfo(@RequestBody user: PostUser) = runPost {
        userService.addUserData(user)
    }

    @GetMapping(UserControllerUrls.USER_INFO)
    fun getUserInfo(@RequestParam(required = true) email: String) = runGet {
        val user = userService.getUserData(email)
        with(user) {
            PostUser(
                email,
                username,
                gender,
                birthDate,
                city,
                studies,
                occupation,
                marriage,
                children,
                hasBeenPositive,
                isVaccinated,
                state
            )
        }
    }

    @PostMapping(UserControllerUrls.USER_DEVICE)
    fun registerUserDevice(
        @RequestParam(required = true) email: String,
        @RequestBody device: PostDevice
    ) = runPost {
        val userNode = userService.getUserData(email)
        deviceService.registerUserDevice(userNode, device)
    }

    @PostMapping(UserControllerUrls.MESSAGE_TOKEN)
    fun registerMessageToken(@RequestBody postToken: PostToken) = runPost {
        userService.registerMessagingToken(postToken.email, postToken.token)
    }

    @PutMapping(UserControllerUrls.UPDATE)
    fun updateUser(
        @RequestParam(required = true) email: String,
        @RequestParam(required = false) city: String?,
        @RequestParam(required = false) studies: String?,
        @RequestParam(required = false) occupation: String?,
        @RequestParam(required = false) marriage: String?,
        @RequestParam(required = false) children: Int?,
        @RequestParam(required = false) positive: Boolean?,
        @RequestParam(required = false) vaccinated: Boolean?
    ) = runPut {
        println(email)
        userService.updateUser(
            email,
            city,
            studies,
            occupation,
            marriage,
            children,
            positive,
            vaccinated
        )
    }
}
