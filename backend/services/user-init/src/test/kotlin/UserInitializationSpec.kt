package de.holisticon.ranked.service.user

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class UserInitializationSpec {

  @Test
  fun `read players from json file`() {
    assertThat(readUsersFromJson("/players.json")).hasSize(56)
  }

  @Test
  fun `load single user`() {
    val users = UserInitializationConfiguration().users()
    val user = users("jangalinski")
    assertThat(user.id).isEqualTo("jangalinski")
    assertThat(user.name).isEqualTo("Jan Galinski")
    assertThat(user.imageUrl).endsWith("/JanGalinski.png")
  }

}
