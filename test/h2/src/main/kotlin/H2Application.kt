@file:Suppress("PackageDirectoryMismatch", "unused")

package de.holisticon.ranked.h2

import de.holisticon.ranked.extension.runApplicationExpr
import org.axonframework.eventhandling.saga.repository.jpa.AssociationValueEntry
import org.axonframework.eventhandling.saga.repository.jpa.SagaEntry
import org.axonframework.eventhandling.tokenstore.jpa.TokenEntry
import org.axonframework.eventsourcing.eventstore.AbstractSnapshotEventEntry
import org.axonframework.eventsourcing.eventstore.jpa.DomainEventEntry
import org.axonframework.eventsourcing.eventstore.jpa.SnapshotEventEntry
import org.h2.tools.Server
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

fun main(args: Array<String>) = runApplicationExpr<H2Application>(*args)

@SpringBootApplication
@EnableSwagger2
@RestController // should be data-rest, but see https://github.com/springfox/springfox/issues/1957
class H2Application(
  @Value("\${ranked.h2.port:9092}") val port: String,
  val domainEventRepository: DomainEventRepository,
  val tokenRepository: TokenRepository
) {

  @Bean(initMethod = "start", destroyMethod = "stop")
  fun h2Server(): Server = Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", port)

  @GetMapping("/domain")
  fun domainEvents() = domainEventRepository.findAll()

  @GetMapping("/token")
  fun tokens() = tokenRepository.findAll()

  @Bean
  fun swagger(): Docket = Docket(DocumentationType.SWAGGER_2)
    .groupName("H2 Repositories")
    .select()
    .apis(RequestHandlerSelectors.any())
    .paths(PathSelectors.any())
    .build()
}

interface AssociationValueRepository : PagingAndSortingRepository<AssociationValueEntry, Long>
interface DomainEventRepository : PagingAndSortingRepository<DomainEventEntry, Long>
interface SagaRepository : PagingAndSortingRepository<SagaEntry<Any>,String>
interface TokenRepository : PagingAndSortingRepository<TokenEntry, TokenEntry.PK>
interface SnapshotEventRepository : PagingAndSortingRepository<SnapshotEventEntry, AbstractSnapshotEventEntry.PK>
