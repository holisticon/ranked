package de.holisticon.ranked

import de.holisticon.ranked.extension.runApplicationExpr
import de.holisticon.ranked.properties.RankedPropertiesAutoConfiguration
import de.holisticon.ranked.properties.createProperties
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import

/**
 * Main application -> run this!
 */
fun main(args: Array<String>) = runApplicationExpr<RankedApplication>(*args)

@SpringBootApplication
class RankedApplication
