/*
 * Copyright 2020 Florian Spieß
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package bot.discord

import kotlinx.coroutines.suspendCancellableCoroutine
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import net.dv8tion.jda.api.events.interaction.GenericComponentInteractionCreateEvent
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent
import net.dv8tion.jda.api.hooks.EventListener
import kotlin.coroutines.resume

/**
 * Requires [CoroutineEventManager] to be used!
 *
 * Opens an event listener scope for simple hooking.
 *
 * ## Example
 *
 * ```kotlin
 * jda.listener<MessageReceivedEvent> { event ->
 *     println(event.message.contentRaw)
 * }
 * ```
 *
 * @param [consumer] The event consumer function
 *
 * @return [CoroutineEventListener] The created event listener instance (can be used to remove later)
 */
inline fun <reified T : GenericEvent> JDA.listener(crossinline consumer: suspend CoroutineEventListener.(T) -> Unit): CoroutineEventListener {
    return object : CoroutineEventListener {
        override fun cancel() {
            return removeEventListener(this)
        }

        override suspend fun onEvent(event: GenericEvent) {
            if (event is T)
                consumer(event)
        }
    }.also { addEventListener(it) }
}

/**
 * Requires [CoroutineEventManager] to be used!
 *
 * Opens an event listener scope for simple hooking. This is a special listener which is used to listen for button presses!
 *
 * ## Example
 *
 * ```kotlin
 * jda.onComponent<ButtonClickEvent>("delete") { event ->
 *     event.deferEdit().queue()
 *     event.hook.deleteOriginal().queue()
 * }
 * ```
 *
 * @param [customId] The button id
 * @param [consumer] The event consumer function
 *
 * @return [CoroutineEventListener] The created event listener instance (can be used to remove later)
 */
inline fun <reified T : GenericComponentInteractionCreateEvent> JDA.onComponent(
    customId: String,
    crossinline consumer: suspend CoroutineEventListener.(T) -> Unit
) = listener<T> {
    if (it.componentId == customId)
        consumer(it)
}

/**
 * Requires [CoroutineEventManager] to be used!
 *
 * Opens an event listener scope for simple hooking. This is a special listener which is used to listen for button presses!
 *
 * ## Example
 *
 * ```kotlin
 * jda.onButton("delete") { event ->
 *     event.deferEdit().queue()
 *     event.hook.deleteOriginal().queue()
 * }
 * ```
 *
 * @param [id] The button id
 * @param [consumer] The event consumer function
 *
 * @return [CoroutineEventListener] The created event listener instance (can be used to remove later)
 */
inline fun JDA.onButton(id: String, crossinline consumer: suspend CoroutineEventListener.(ButtonClickEvent) -> Unit) =
    onComponent(id, consumer)

/**
 * Requires [CoroutineEventManager] to be used!
 *
 * Opens an event listener scope for simple hooking. This is a special listener which is used to listen for selection menu events!
 *
 * ## Example
 *
 * ```kotlin
 * jda.onSelection("menu:class") { event ->
 *     event.deferEdit().queue()
 *     println("User selected: ${event.values}")
 * }
 * ```
 *
 * @param [id] The selection menu id
 * @param [consumer] The event consumer function
 *
 * @return [CoroutineEventListener] The created event listener instance (can be used to remove later)
 */
inline fun JDA.onSelection(
    id: String,
    crossinline consumer: suspend CoroutineEventListener.(SelectionMenuEvent) -> Unit
) = onComponent(id, consumer)

/**
 * Requires an EventManager implementation that supports either [EventListener] or [SubscribeEvent].
 *
 * Awaits a single event and then returns it. You can use the filter function to skip unwanted events for a simpler
 * code structure.
 *
 * ## Example
 *
 * ```kotlin
 * fun onMessage(message: Message) {
 *   if (message.contentRaw == "Hello Bot") {
 *     // Send confirmation message
 *     message.channel.sendTyping().await()
 *     message.channel.sendMessage("Hello, how are you?").queue()
 *     // Wait for user's response
 *     val nextEvent = message.jda.await<MessageReceivedEvent> { it.author == message.author }
 *     println("User responded with ${nextEvent.message.contentDisplay}")
 *   }
 * }
 * ```
 *
 * @param [filter] The event filter function (optional)
 *
 * @return The filtered event
 */
suspend inline fun <reified T : GenericEvent> JDA.await(crossinline filter: (T) -> Boolean = { true }) =
    suspendCancellableCoroutine<T> {
        val listener = object : EventListener {
            override fun onEvent(event: GenericEvent) {
                if (event is T && filter(event)) {
                    removeEventListener(this)
                    it.resume(event)
                }
            }
        }
        addEventListener(listener)
        it.invokeOnCancellation { removeEventListener(listener) }
    }
