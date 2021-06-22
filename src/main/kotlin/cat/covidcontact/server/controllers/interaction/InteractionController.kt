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
