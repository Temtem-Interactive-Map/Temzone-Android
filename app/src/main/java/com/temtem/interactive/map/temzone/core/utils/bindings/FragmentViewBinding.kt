package com.temtem.interactive.map.temzone.core.utils.bindings

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Returns a property delegate to access [ViewBinding] by **default** scoped to this [Fragment]:
 * ```
 * class MyFragment : Fragment() {
 *     val viewBinding: MyViewBinding by viewBindings()
 * }
 * ```
 */
inline fun <reified T : ViewBinding> Fragment.viewBindings(): ReadOnlyProperty<Fragment, T> {
    return object : ReadOnlyProperty<Fragment, T>, DefaultLifecycleObserver {
        private var binding: T? = null

        override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
            val bindMethod = T::class.java.getMethod("bind", View::class.java)
            val invoke = bindMethod.invoke(null, thisRef.requireView()) as T

            return invoke.also {
                if (viewLifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
                    viewLifecycleOwner.lifecycle.addObserver(this)
                    binding = it
                }
            }
        }

        override fun onDestroy(owner: LifecycleOwner) {
            binding = null
        }
    }
}
