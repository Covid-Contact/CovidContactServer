package cat.covidcontact.server.controllers.interaction

import cat.covidcontact.server.controllers.runPost
import cat.covidcontact.server.model.post.PostRead
import cat.covidcontact.server.services.interaction.InteractionService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(InteractionControllerUrls.BASE)
class InteractionController(
    private val interactionService: InteractionService
) {

    @PostMapping(InteractionControllerUrls.READ)
    fun addRead(@RequestBody read: PostRead) = runPost {
        interactionService.addRead(read)
    }
}
