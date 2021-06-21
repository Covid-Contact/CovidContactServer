package cat.covidcontact.server.controllers.interaction

import cat.covidcontact.server.controllers.runPost
import cat.covidcontact.server.controllers.runPut
import cat.covidcontact.server.model.post.PostRead
import cat.covidcontact.server.services.interaction.InteractionService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(InteractionControllerUrls.BASE)
class InteractionController(
    private val interactionService: InteractionService
) {

    @PostMapping(InteractionControllerUrls.READ)
    fun addRead(@RequestBody read: PostRead) = runPost {
        interactionService.addRead(read)
        Unit
    }

    @PutMapping(InteractionControllerUrls.POSITIVE)
    fun notifyPositive(@RequestParam(required = true) email: String) = runPut {
        interactionService.notifyPositive(email)
    }
}
