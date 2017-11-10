package de.holisticon.ranked.h2

import org.axonframework.eventhandling.saga.repository.jpa.AssociationValueEntry
import org.axonframework.eventhandling.saga.repository.jpa.SagaEntry
import org.axonframework.eventhandling.tokenstore.jpa.TokenEntry
import org.axonframework.eventsourcing.eventstore.AbstractSnapshotEventEntry
import org.axonframework.eventsourcing.eventstore.jpa.DomainEventEntry
import org.axonframework.eventsourcing.eventstore.jpa.SnapshotEventEntry
import org.h2.tools.Server
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.repository.PagingAndSortingRepository
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2


fun main(args: Array<String>) {
  SpringApplication.run(H2Application::class.java, *args)
}

@SpringBootApplication
@EnableSwagger2
//@Import({ springfox.documentation.spring.data.rest.configuration.SpringDataRestConfiguration.class})
class H2Application(
  @Value("\${ranked.h2.port:9092}") val port: String
) {

  @Bean(initMethod = "start", destroyMethod = "stop")
  fun h2Server(): Server = Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", port)


  @Bean
  fun commandApi() = Docket(DocumentationType.SWAGGER_2)
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
