package de.holisticon.ranked.command.rest

import de.holisticon.ranked.command.cmd.CreateMatch
import org.axonframework.commandhandling.CommandExecutionException
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@Component
@RestController
class CommandApi(val commandGateway: CommandGateway) {

  @PostMapping(path = arrayOf("/command/createMatch"))
  fun createMatch(@RequestBody @Valid match: CreateMatch): ResponseEntity<String> {
    try {
      val result: Any? = commandGateway.sendAndWait(match)
      // TODO how to react to that?
      if (result == null) {
        return ResponseEntity.badRequest().build()
      }
      return ResponseEntity.noContent().build()
    } catch (e: CommandExecutionException) {
      return ResponseEntity.badRequest().build()
    }
  }
}
