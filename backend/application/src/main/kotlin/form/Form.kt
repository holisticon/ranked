@file:Suppress("PackageDirectoryMismatch", "unused")

package de.holisticon.ranked.form

import de.holisticon.ranked.command.api.CreateMatch
import de.holisticon.ranked.model.MatchSet
import de.holisticon.ranked.model.Player
import de.holisticon.ranked.model.Team
import de.holisticon.ranked.model.UserName
import de.holisticon.ranked.view.player.PlayerViewService
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.stereotype.Controller
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import javax.validation.Valid

@Controller
class WebController(val commandGateway: CommandGateway, val playerViewService: PlayerViewService) : WebMvcConfigurer {

  @ModelAttribute("players")
  fun allPlayers(): List<Player> {
    return playerViewService.findAllPlayers()
  }

  @GetMapping("/")
  fun showForm(createMatchForm: CreateMatchForm): String = "create-match"

  @PostMapping("/")
  fun checkPersonInfo(@Valid form: CreateMatchForm, bindingResult: BindingResult): String {

    val sets = mutableListOf<MatchSet>()

    if (form.goalsBlue1 == 6 || form.goalsRed1 == 6) {
      sets.add(MatchSet(
        goalsRed = form.goalsRed1,
        goalsBlue = form.goalsBlue1,
        offenseRed = UserName(form.offenseRed1),
        offenseBlue = UserName(form.offenseBlue1)
      ))
    }
    if (form.goalsBlue2 == 6 || form.goalsRed2 == 6) {
      sets.add(MatchSet(
        goalsRed = form.goalsRed2,
        goalsBlue = form.goalsBlue2,
        offenseRed = UserName(form.offenseRed2),
        offenseBlue = UserName(form.offenseBlue2)
      ))
    }
    if (form.goalsBlue3 == 6 || form.goalsRed3 == 6) {
      sets.add(MatchSet(
        goalsRed = form.goalsRed3,
        goalsBlue = form.goalsBlue3,
        offenseRed = UserName(form.offenseRed3),
        offenseBlue = UserName(form.offenseBlue3)
      ))
    }

    val cmd = CreateMatch(
      teamRed = Team(UserName(form.red1), UserName(form.red2)),
      teamBlue = Team(UserName(form.blue1), UserName(form.blue2)),
      matchSets = sets
    )

    commandGateway.send<CreateMatch>(cmd)

    return "redirect:/view/wall/matches"
  }
}

data class CreateMatchForm(
  var blue1: String = "",
  var blue2: String = "",
  var red1: String = "",
  var red2: String = "",

  // set 1
  var goalsBlue1: Int = 0,
  var goalsRed1: Int = 0,
  var offenseBlue1: String = "",
  var offenseRed1: String = "",

  // set 2
  var goalsBlue2: Int = 0,
  var goalsRed2: Int = 0,
  var offenseBlue2: String = "",
  var offenseRed2: String = "",

  // set 3
  var goalsBlue3: Int = 0,
  var goalsRed3: Int = 0,
  var offenseBlue3: String = "",
  var offenseRed3: String = ""
)
