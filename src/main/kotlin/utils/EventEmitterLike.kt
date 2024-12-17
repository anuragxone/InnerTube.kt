package com.anuragxone.innertube.utils

class EventEmitter {
    private val events = mutableMapOf<String, MutableList<(Array<out Any>) -> Unit>>()

    // Registers a listener for a given event
    fun on(event: String, listener: (Array<out Any>) -> Unit) {
        events.getOrPut(event) { mutableListOf() }.add(listener)
    }

    // Registers a one-time listener for a given event
    fun once(event: String, listener: (Array<out Any>) -> Unit) {
        val wrapper: (Array<out Any>) -> Unit = { args ->
            listener(args)
            off(event, wrapper)
        }
        on(event, wrapper)
    }

    // Emits an event, invoking all associated listeners
    fun emit(event: String, vararg args: Any) {
        events[event]?.toList()?.forEach { it(args) }
    }

    // Removes a specific listener for a given event
    fun off(event: String, listener: (Array<out Any>) -> Unit) {
        events[event]?.remove(listener)
        if (events[event]?.isEmpty() == true) {
            events.remove(event)
        }
    }

    // Removes all listeners for a given event
    fun removeAllListeners(event: String) {
        events.remove(event)
    }
}

fun main() {
    val emitter = EventEmitter()

    // Example usage
    val listener = { args: Array<out Any> -> println("Listener called with: ${args.joinToString()}") }

    emitter.on("test", listener)
    emitter.emit("test", 1, 2, 3) // Output: Listener called with: 1, 2, 3

    emitter.once("testOnce") { args ->
        println("Once listener called with: ${args.joinToString()}")
    }
    emitter.emit("testOnce", "first") // Output: Once listener called with: first
    emitter.emit("testOnce", "second") // No output

    emitter.off("test", listener)
    emitter.emit("test", 4, 5, 6) // No output
}
