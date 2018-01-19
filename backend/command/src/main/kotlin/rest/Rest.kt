@file:Suppress("UNUSED")

package de.holisticon.ranked.command.rest

import de.holisticon.ranked.command.api.CreateMatch
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
  fun createMatch(@RequestBody @Valid match: CreateMatch): ResponseEntity<String> {
    try {
      commandGateway.sendAndWait<CreateMatch>(match)
      return ResponseEntity.noContent().build()
    } catch (e: Throwable) {
      return ResponseEntity.badRequest().body(e.message)
    }
  }
}

