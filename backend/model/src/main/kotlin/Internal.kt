package de.holisticon.ranked.model.event.internal

import de.holisticon.ranked.model.user.User

/**
 * Spring Event to trigger replay for given trackingProcessor.
 */
typealias ReplayTrackingProcessor = String

/**
 * Published via spring eventing on userInitialization
 */
typealias InitUser = User
