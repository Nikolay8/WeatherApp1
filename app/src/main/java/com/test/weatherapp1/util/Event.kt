package com.test.weatherapp1.util

/**
 * A wrapper class used to handle events in a way that ensures they are consumed only once.
 * @param T The type of the content of the event.
 */
open class Event<out T>(private val content: T) {
    private var hasBeenHandled = false

    /**
     * Returns the content and marks the event as handled.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Returns the content even if the event has already been handled..
     */
    fun peekContent(): T = content
}