package de.holisticon.ranked.command

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class CommandServiceApplication

fun main(args: Array<String>) {
    SpringApplication.run(CommandServiceApplication::class.java, *args)
}