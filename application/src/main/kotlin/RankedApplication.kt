package de.holisticon.ranked

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * Main application -> run this!
 */
fun main(args: Array<String>) {
  runApplication<RankedApplication>(*args)
}

@SpringBootApplication
class RankedApplication
