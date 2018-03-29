package de.holisticon.ranked.command

import de.holisticon.ranked.service.user.UserService
import org.dom4j.Document
import org.dom4j.DocumentHelper

internal fun addImageUrl(userService: UserService): (Document) -> Document = {
  val userId = it.rootElement.element("userName").element("value").text

  val entry = DocumentHelper.createElement("imageUrl")
  entry.text = userService.loadUser(userId).imageUrl
  it.rootElement.add(entry)
  it
}
