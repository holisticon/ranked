package de.holisticon.ranked.extension

import org.springframework.aop.support.AopUtils
import org.springframework.boot.runApplication
import kotlin.reflect.KClass

/**
 * @return the targetClass of a Spring Bean as KClass
 */
fun Any.getTargetClass(): KClass<out Any> {
  return AopUtils.getTargetClass(this).kotlin
}

inline fun <reified T : kotlin.Any> runApplicationExpr(vararg args: kotlin.String): Unit {
  runApplication<T>(*args)
}
