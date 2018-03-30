package de.holisticon.ranked.model.user

import de.holisticon.ranked.model.ImageUrl

/**
 * User, represented by [id] (id), [name] and [imageUrl].
 */
data class User(
  val id: String,
  val name: String,
  val imageUrl: ImageUrl
)

/**
 * A user supplier is a function that can return a user for a given string (userId).
 */
typealias UserSupplier = (String) -> User
