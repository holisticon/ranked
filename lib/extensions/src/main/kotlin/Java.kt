package de.holisticon.ranked.extension

import kotlin.reflect.KClass

fun KClass<out Any>.inPackage(pkg:String) :Boolean = this.java.inPackage(pkg)

fun Class<*>.inPackage(pkg : String ): Boolean {
  // in spring-actuator is a bean that seems to have no package so this hack was required for not null!
  val p : Package? = this?.`package`
  return if (p != null) p.name.startsWith(pkg) else false
}
