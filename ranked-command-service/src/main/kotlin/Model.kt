package de.holisticon.ranked.model

data class UserName(
  val value: String
)

data class Team(
  val player1: UserName,
  val player2: UserName
)

data class MatchSet(
  val goalsRed: Int,
  val goalsBlue: Int,
  val offenseRed: UserName,
  val offenseBlue: UserName
)
