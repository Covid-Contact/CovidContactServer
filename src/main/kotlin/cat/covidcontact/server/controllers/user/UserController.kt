package cat.covidcontact.server.controllers.user

import cat.covidcontact.server.controllers.runGet
import cat.covidcontact.server.controllers.runPost
import cat.covidcontact.server.data.applicationuser.ApplicationUser
import cat.covidcontact.server.data.device.Device
import cat.covidcontact.server.data.user.User
import cat.covidcontact.server.services.applicationuser.ApplicationUserService
import cat.covidcontact.server.services.device.DeviceService
import cat.covidcontact.server.services.user.UserService
import cat.covidcontact.server.services.userdevice.UserDeviceService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(UserControllerUrls.BASE)
class UserController(
    private val applicationUserService: ApplicationUserService,
    private val userService: UserService,
    private val deviceService: DeviceService,
    private val userDeviceService: UserDeviceService
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
    fun addUserInfo(@RequestBody user: User) = runPost {
        userService.addUserData(user)
    }

    @GetMapping(UserControllerUrls.USER_INFO)
    fun getUserInfo(@RequestParam(required = true) email: String) = runGet {
        userService.getUserData(email)
    }

    @PostMapping(UserControllerUrls.USER_DEVICE)
    fun registerUserDevice(
        @RequestParam(required = true) email: String,
        @RequestBody device: Device
    ) = runPost {
        val userNode = userService.getUserData(email)
        deviceService.addDeviceIfNotExists(device)
        userDeviceService.registerUserDevice(userNode, device)
    }
}
