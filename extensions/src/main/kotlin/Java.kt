@file:Suppress("UNUSED")

package de.holisticon.ranked.extension

import kotlin.reflect.KClass

fun KClass<out Any>.inPackage(pkg: String): Boolean = this.java.inPackage(pkg)

fun Class<*>.inPackage(pkg: String): Boolean {
  // in spring-actuator is a bean that seems to have no package so this hack was required for not null!
  val p: Package? = this.`package`
  return if (p != null) p.name.startsWith(pkg) else false
}

fun String.toFirstUpper(): String = if (this.isEmpty()) { this } else { this.substring(0, 1).toUpperCase() + this.substring(1) }

fun String.snakeCaseToSpace(): String = this.replace("_", " ")

fun String.toFirstUpperSentence(text: String): String = text.split(" ").joinToString(separator = " ") { it.toFirstUpper() }



