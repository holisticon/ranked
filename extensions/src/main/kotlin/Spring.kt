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

inline fun defaultSmartLifecycle(thePhase:Int, crossinline action:() ->Unit) = object: DefaultSmartLifecycle(thePhase) {
  override fun onStart() {
    action()
  }
}

/**
 * Opinionated best guess implementation of [SmartLifecycle], just override [onStart] and  [getPhase]
 * to get an auto-started, startable and stoppable lifecycle manager.
 *
 * object: DefaultSmartLifecycle(50) {
 *   override fun onStart() { ... }
 * }
 */
abstract class DefaultSmartLifecycle(private val thePhase : Int) : SmartLifecycle {

  private var running: Boolean = false

  override fun start() {
    onStart()
    this.running = true
  }

  abstract fun onStart()

  override fun isAutoStartup() = true

  override fun stop(callback: Runnable?) {
    callback?.run()
    this.running = false
  }

  override fun stop() = stop(null)

  override fun isRunning() = running

  override fun getPhase(): Int = thePhase
}
