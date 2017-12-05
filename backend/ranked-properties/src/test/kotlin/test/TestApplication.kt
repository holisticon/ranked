package de.holisticon.ranked.properties.test

import de.holisticon.ranked.extension.runApplicationExpr
import org.springframework.boot.autoconfigure.SpringBootApplication


@SpringBootApplication
class PropertiesSpecApplication

fun main(args: Array<String>) = runApplicationExpr<PropertiesSpecApplication>(*args)

