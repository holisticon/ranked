package de.holisticon.ranked.properties.test

import de.holisticon.ranked.extension.runApplicationExpr
import de.holisticon.ranked.properties.RankedProperties
import de.holisticon.ranked.properties.RankedPropertiesAutoConfiguration
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties


@SpringBootApplication
class PropertiesSpecApplication

fun main(args: Array<String>) = runApplicationExpr<PropertiesSpecApplication>(*args)

