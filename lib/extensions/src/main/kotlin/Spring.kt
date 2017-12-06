package de.holisticon.ranked.extension

import org.springframework.aop.support.AopUtils
import org.springframework.boot.runApplication
import org.springframework.context.SmartLifecycle
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


abstract class DefaultSmartLifecycle : SmartLifecycle {

  private var running: Boolean = false

  override fun start() {
    this.running = true
  }

  override fun isAutoStartup(): Boolean {
    return true
  }

  override fun stop(callback: Runnable?) {
    callback?.run()
    this.running = false
  }

  override fun stop() {
    this.running = false
  }

  override fun getPhase(): Int {
    return Int.MAX_VALUE
  }

  override fun isRunning(): Boolean {
    return running
  }
}
