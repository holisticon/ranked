package de.holisticon.ranked.frontend

import de.holisticon.ranked.extension.runApplicationExpr
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.netflix.zuul.EnableZuulProxy


/**
 * Main application -> run this!
 */
fun main(args: Array<String>) = runApplicationExpr<FrontendApplication>(*args)

@SpringBootApplication
@EnableZuulProxy
class FrontendApplication
