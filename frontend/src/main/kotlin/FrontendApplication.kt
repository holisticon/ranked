package de.holisticon.ranked.frontend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


/**
 * Main application -> run this!
 */
fun main(args: Array<String>) {
  runApplication<FrontendApplication>(*args)
}

@SpringBootApplication
class FrontendApplication
