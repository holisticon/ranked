package de.holisticon.ranked

import de.holisticon.ranked.extension.runApplicationExpr
import org.springframework.boot.autoconfigure.SpringBootApplication

/**
 * Main application -> run this!
 */
fun main(args: Array<String>) = runApplicationExpr<RankedApplication>(*args)

@SpringBootApplication
class RankedApplication