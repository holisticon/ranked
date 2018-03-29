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

typealias UserSupplier = (String) -> User
