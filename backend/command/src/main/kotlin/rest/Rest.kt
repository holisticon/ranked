@file:Suppress("UNUSED")

package de.holisticon.ranked.command.rest

import de.holisticon.ranked.command.api.CreateMatch
import de.holisticon.ranked.command.api.CreatePlayer
import de.holisticon.ranked.extension.send
import de.holisticon.ranked.model.UserName
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import mu.KLogging
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/command")
@Api(tags = ["Command"])
class CommandApi(val commandGateway: CommandGateway) {
  companion object : KLogging()

  @ApiOperation(value = "Creates a new match.")
  @ApiResponses(
    ApiResponse(code = 204, message = "If the create match request has been successfully received."),
    ApiResponse(code = 400, message = "If the create match request was not correct.")
  )
  @PostMapping(path = ["/match"])
  fun createMatch(@RequestBody match: CreateMatch): ResponseEntity<String> {

    var response: ResponseEntity<String> = ResponseEntity.noContent().build()
    commandGateway.send(
      command = match,
      success = { _, _: Any -> logger.debug { "Successfully submitted a match" } },
      failure = { _, cause: Throwable ->
        logger.error { "Failure by submitting a match: ${cause.localizedMessage}" }
        response = ResponseEntity.badRequest().build()
      }
    )

    return response
  }

  @ApiOperation(value = "Creates a new player.")
  @ApiResponses(
    ApiResponse(code = 204, message = "If the create player request has been successfully received."),
    ApiResponse(code = 400, message = "If the create player request was not correct.")
  )
  @PostMapping(path = ["/player"])
  fun createPlayer(@RequestBody playerInfo: PlayerInfo): ResponseEntity<String> {
    var response: ResponseEntity<String> = ResponseEntity.noContent().build()

    commandGateway.send(
      command = CreatePlayer(
        userName = playerInfo.userName(),
        displayName = playerInfo.displayName,
        imageUrl = playerInfo.imageUrl
      ),
      success = { _, _: Any -> logger.debug { "Successfully created a user ${playerInfo.displayName}" } },
      failure = { _, cause: Throwable ->
        logger.error { "Failure by submitting a user: ${cause.localizedMessage}" }
        response = ResponseEntity.badRequest().build()
      })

    return response
  }

  inner class PlayerInfo(
    val displayName: String,
    val imageUrl: String
  ) {
    fun userName() = UserName(displayName
      .replace(" ", "")
      .toLowerCase()
      .replace("ü", "ue")
      .replace("ä", "ae")
      .replace("ö", "oe")
      .replace("ß", "ss")
    )

  }
}
