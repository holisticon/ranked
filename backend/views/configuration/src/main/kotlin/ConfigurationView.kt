package de.holisticon.ranked.view.configuration

import de.holisticon.ranked.properties.RankedProperties
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/view"])
class ConfigurationView(private val properties: RankedProperties){

  @ApiOperation(value = "Lists configuration")
  @GetMapping("/configuration")
  fun findAll() = properties

}
