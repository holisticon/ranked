package de.holisticon.ranked.command.saga

import de.holisticon.ranked.model.event.MatchCreated
import de.holisticon.ranked.model.event.TeamWonMatch
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.saga.EndSaga
import org.axonframework.eventhandling.saga.SagaEventHandler
import org.axonframework.eventhandling.saga.StartSaga
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired


@Saga
class EloMatchSaga {

  @Autowired
  @Transient private val commandGateway: CommandGateway? = null

  @StartSaga
  @SagaEventHandler(associationProperty = "matchId")
  fun handle(e: MatchCreated) {

  }

  @EndSaga
  @SagaEventHandler(associationProperty = "matchId")
  fun handle(e: TeamWonMatch) {

  }

}

