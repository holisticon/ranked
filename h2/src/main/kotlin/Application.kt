package de.holisticon.ranked.h2

import org.h2.tools.Server
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.SmartLifecycle


@SpringBootApplication
class H2Application(@Value("\${h2.port:9092}") val port : Int) : SmartLifecycle {

    val server = Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", port.toString())

    override fun isAutoStartup(): Boolean = true
    override fun getPhase(): Int = 0

    override fun isRunning(): Boolean = server.isRunning(false)

    override fun start() {
        server.start()
    }

    override fun stop(callback: Runnable) {
        server.stop();

        callback.run()
    }

    override fun stop() = stop({})
}

fun main(args: Array<String>) {
    SpringApplication.run(H2Application::class.java, *args)
}
