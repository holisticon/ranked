package de.holisticon.ranked.frontend

import de.holisticon.ranked.extension.runApplicationExpr
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


/**
 * Main application -> run this!
 */
fun main(args: Array<String>) = runApplicationExpr<FrontendApplication>(*args)

@SpringBootApplication
class FrontendApplication