package de.holisticon.ranked

import de.holisticon.ranked.command.rest.CommandApi
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean


/**
 * Main application -> run this!
 */
fun main(args: Array<String>) {
  SpringApplication.run(RankedApplication::class.java, *args)
}

@SpringBootApplication
class RankedApplication {

}


