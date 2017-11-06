package de.holisticon.ranked.model

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import javax.validation.Validation


object ModelSpekSpec : Spek({

  val validator = Validation.buildDefaultValidatorFactory().validator
  val kermit = UserName("kermit")

  describe("a userName") {
    it("should have the given value") {
      assertThat(validator.validate(kermit)).isEmpty()
      assertThat(kermit.value).isEqualTo("kermit")
    }
    it("is not valid if the value is empty") {
      assertThat(validator.validate(UserName(""))).isNotEmpty
    }
    it("is not valid if the value is shorter than 4 chars") {
      assertThat(validator.validate(UserName("abc"))).isNotEmpty
    }
  }

  describe("a team") {
    it("is created with two different players") {
      val team = Team(kermit, UserName("piggy"))

      assertThat(validator.validate(team)).isEmpty()
    }
    it("must not have the same player twice") {
      val team = Team(kermit, kermit)

      assertThat(validator.validate(team)).isNotEmpty
    }
  }

})
