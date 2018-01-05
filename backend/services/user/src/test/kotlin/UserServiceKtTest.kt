package de.holisticon.ranked.service.user

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class UserServiceKtTest() {


  @Test
  fun `read players from json file`() {
    val players = readUsersFromJson()

    assertThat(players).hasSize(51)
  }
}
