package de.holisticon.ranked.command.rest

import de.holisticon.ranked.command.cmd.CreateMatch
import mu.KLogging
import org.axonframework.commandhandling.CommandExecutionException
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
class CommandApi(val commandGateway: CommandGateway) {

  @GetMapping("/hello")
  fun hello() = "hello simon!"

  companion object: KLogging()

  @PostMapping(path = arrayOf("/command/createMatch"))
  fun createMatch(@RequestBody @Valid match: CreateMatch): ResponseEntity<String> {
    try {
      val result: Any = commandGateway.sendAndWait(match) ?: return ResponseEntity.badRequest().body("Interrupted")
      // TODO how to react to that?
      return ResponseEntity.noContent().build()
    } catch (e: CommandExecutionException) {
      logger.error { "Command execution error $e" }
      return ResponseEntity.badRequest().body(e.message)
    }
  }
}
