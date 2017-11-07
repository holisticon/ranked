import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

/**
 * The spring boot main application.
 */
@SpringBootApplication
class AiApplication

fun main(args: Array<String>) {
  SpringApplication.run(AiApplication::class.java, *args)
}
