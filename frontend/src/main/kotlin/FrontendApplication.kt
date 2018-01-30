package de.holisticon.ranked.frontend

import de.holisticon.ranked.extension.runApplicationExpr
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.netflix.zuul.EnableZuulProxy
import org.springframework.context.annotation.Bean
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter




/**
 * Main application -> run this!
 */
fun main(args: Array<String>) = runApplicationExpr<FrontendApplication>(*args)

@SpringBootApplication
@EnableZuulProxy
class FrontendApplication
