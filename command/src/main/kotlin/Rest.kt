package de.holisticon.ranked.command.rest

import de.holisticon.ranked.command.api.CreateMatch
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import mu.KLogging
import org.axonframework.commandhandling.CommandExecutionException
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/command")
@Api(tags = arrayOf("Command"))
class CommandApi(val commandGateway: CommandGateway) {

  companion object : KLogging()

  @ApiOperation(value = "Creates a new match.")
  @ApiResponses(
    ApiResponse(code=204, message = "If the create match request has been successfully received."),
    ApiResponse(code=400, message = "If the create match request was not correct.")
  )
  @PostMapping(path = arrayOf("/createMatch"))
  fun createMatch(@RequestBody @Valid match: CreateMatch): ResponseEntity<String> {
    try {
      val result: Any = commandGateway.sendAndWait(match) ?: return ResponseEntity.badRequest().body("Sending thread interrupted")
      // TODO how to react to that?
      return ResponseEntity.noContent().build()
    } catch (e: CommandExecutionException) {
      logger.error { "Command execution error $e" }
      return ResponseEntity.badRequest().body(e.message)
    }
  }
}
