package de.holisticon.ranked.extension

import org.springframework.aop.support.AopUtils
import kotlin.reflect.KClass

/**
 * @return the targetClass of a Spring Bean as KClass
 */
fun Any.getTargetClass(): KClass<out Any> {
  return AopUtils.getTargetClass(this).kotlin
}
