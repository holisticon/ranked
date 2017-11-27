package de.holisticon.ranked.frontend

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping


@Controller
class UIController {

  @RequestMapping(value = "/")
  fun index(): String {
    return "index"
  }

}
