package de.holisticon.ranked.ai

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import ai.api.AIConfiguration
import ai.api.AIDataService
import ai.api.model.AIRequest
import ai.api.model.AIResponse
import org.springframework.stereotype.Component

@ComponentScan
class AiConfiguration {

  @Value(value="apikey")
  private lateinit var apiKey: String;


  @Bean
  fun aiDataService(): AIDataService {
    val configuration = AIConfiguration(apiKey)
    return AIDataService(configuration)
  }
}

@Component
class AiService(val dataService: AIDataService) {

  fun resolveIntent(intent: String) : String {

    try {
      val request = AIRequest(intent)
      val response = dataService.request(request)

      if (response.status.code == 200) {
        return (response.result.fulfillment.speech)
      } else {
        throw RuntimeException(response.status.errorDetails)
      }
    } catch (ex: Exception) {
      throw RuntimeException(ex)
    }

  }
}
