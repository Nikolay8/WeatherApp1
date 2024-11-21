package com.test.weatherapp1.util

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

/**
 * A lifecycle-aware observable that sends updates only once, preventing multiple emissions
 * when a `LiveData` is observed multiple times (e.g., after configuration changes).
 *
 * This is useful for handling events like navigation or showing a `Snackbar`, where
 * you want to respond only to the first emission of a value.
 *
 * @param T The type of data being observed.
 */
class SingleLiveEvent<T> : MutableLiveData<T>() {

    private val pending = AtomicBoolean(false)

    /**
     * Observes the `SingleLiveEvent`. The observer will only be notified if
     * the value is explicitly set using `setValue()` or `call()`, and only
     * once for each set action.
     *
     * @param owner The `LifecycleOwner` which controls the observer.
     * @param observer The observer that receives the updates.
     */
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner) { t ->
            if (pending.compareAndSet(true, false)) {
                observer.onChanged(t)
            }
        }
    }

    /**
     * Sets the value of the `SingleLiveEvent`. This also marks the event
     * as pending, so it will trigger the observer when observed.
     *
     * @param value The new value to be set.
     */
    override fun setValue(value: T?) {
        pending.set(true)
        super.setValue(value)
    }

    /**
     * Triggers the event without setting a specific value. This is typically used
     * when the event itself is more important than the value (e.g., a navigation event).
     */
    fun call() {
        value = null
    }
}