package de.holisticon.ranked.service.user

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class PlayerImportServiceSpec() {


  @Test
  fun `read players from json file`() {
    val players = readUsersFromJson("/players.json")
    assertThat(players).hasSize(54)
  }

  @Test
  fun `load all users`() {
    assertThat(PlayerImportService().loadAll()).hasSize(54)
  }

  @Test
  fun `load single user`() {
    val user = PlayerImportService().loadAll().find { it.id == "jangalinski" }!!
    assertThat(user.id).isEqualTo("jangalinski")
    assertThat(user.name).isEqualTo("Jan Galinski")
    assertThat(user.imageUrl).endsWith("/JanGalinski.png")
  }
}
