@file:Suppress("UNUSED")

package de.holisticon.ranked.command.rest

import de.holisticon.ranked.command.api.CreateMatch
import de.holisticon.ranked.command.api.CreatePlayerAndUser
import de.holisticon.ranked.model.UserName
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import mu.KLogging
import org.axonframework.commandhandling.CommandCallback
import org.axonframework.commandhandling.CommandMessage
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.function.BiConsumer
import javax.validation.Valid

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
      commandGateway.send<CreateMatch, Any>(match, object: CommandCallback<CreateMatch, Any> {
        override fun onSuccess(commandMessage: CommandMessage<out CreateMatch>?, result: Any?) {
          logger.debug { "Successfully submitted a match" }
        }

        override fun onFailure(commandMessage: CommandMessage<out CreateMatch>?, cause: Throwable) {
          logger.error { "Failure by submitting a match: ${cause.localizedMessage}" }
          response = ResponseEntity.badRequest().build()
        }
      })
      return response
  }

  @ApiOperation(value = "Creates a new user.")
  @ApiResponses(
    ApiResponse(code = 204, message = "If the create user request has been successfully received."),
    ApiResponse(code = 400, message = "If the create user request was not correct.")
  )
  @PostMapping(path = ["/user"])
  fun createPlayer(@RequestBody userInfo: UserInfo): ResponseEntity<String> {
    var response: ResponseEntity<String> = ResponseEntity.noContent().build()
    commandGateway.send<CreatePlayerAndUser, Any>(CreatePlayerAndUser(
      userName = UserName(userInfo.username()),
      displayName = userInfo.displayName,
      imageUrl = userInfo.imageUrl
    ), object: CommandCallback<CreatePlayerAndUser, Any> {
      override fun onSuccess(commandMessage: CommandMessage<out CreatePlayerAndUser>?, result: Any?) {
        logger.debug { "Successfully created a user" }
      }

      override fun onFailure(commandMessage: CommandMessage<out CreatePlayerAndUser>?, cause: Throwable) {
        logger.error { "Failure by submitting a user: ${cause.localizedMessage}" }
        response = ResponseEntity.badRequest().build()
      }
    })
    return response
  }

  data class UserInfo(
    val displayName: String,
    val imageUrl: String
  ) {
    fun username() = displayName
      .replace(" ", "")
      .toLowerCase()
      .replace("ü", "ue")
      .replace("ä", "ae")
      .replace("ö", "oe")
      .replace("ß", "ss")
  }
}
