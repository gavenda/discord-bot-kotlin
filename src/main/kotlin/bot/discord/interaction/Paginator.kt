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

package bot.discord.interaction

import bot.Message
import bot.discord.CoroutineEventListener
import bot.discord.await
import net.dv8tion.jda.api.entities.Emoji
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.interactions.Interaction
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.Button
import net.dv8tion.jda.api.interactions.components.ButtonInteraction
import java.security.SecureRandom
import java.time.Duration
import java.util.*

const val DEFAULT_DURATION = 300L
val DEFAULT_PREV = Button.secondary("prev", Emoji.fromUnicode("⬅️"))
val DEFAULT_NEXT = Button.secondary("next", Emoji.fromUnicode("➡️"))

class Paginator internal constructor(
    private val nonce: String,
    val duration: Duration
) : CoroutineEventListener {
    private var expiresAt: Long = System.currentTimeMillis() + duration.toMillis()
    private var index = 0
    private val pageCache = mutableListOf<Message>()
    private val nextPage: Message
        get() {
            val nextIndex = ++index
            if (nextIndex > pageCache.lastIndex) {
                index = 0
                return pageCache.first()
            }
            return pageCache[nextIndex]
        }

    private val prevPage: Message
        get() {
            val nextIndex = --index
            if (nextIndex < 0) {
                index = pageCache.lastIndex
                return pageCache.last()
            }
            return pageCache[nextIndex]
        }

    var filter: (ButtonInteraction) -> Boolean = { true }

    fun filterBy(filter: (ButtonInteraction) -> Boolean): Paginator {
        this.filter = filter
        return this
    }

    private var prev = DEFAULT_PREV
    private var next = DEFAULT_NEXT

    internal val controls: ActionRow
        get() = ActionRow.of(
            prev.withId("$nonce:prev"),
            next.withId("$nonce:next"),
        )

    val pages: List<Message> get() = pageCache.toList()

    fun addPages(vararg page: Message) {
        pageCache.addAll(page)
    }

    override suspend fun onEvent(event: GenericEvent) {
        if (expiresAt < System.currentTimeMillis()) {
            event.jda.removeEventListener(this)
        }
        if (event !is ButtonInteraction) return
        val buttonId = event.componentId
        if (!buttonId.startsWith(nonce) || !filter(event)) return
        expiresAt = System.currentTimeMillis() + duration.toMillis()
        val (_, operation) = buttonId.split(":")

        try {
            when (operation) {
                "prev" -> {
                    event.editMessage(prevPage)
                        .setActionRows(controls)
                        .await()
                }
                "next" -> {
                    event.editMessage(nextPage)
                        .setActionRows(controls)
                        .await()
                }
            }
        } catch (ex: Exception) {
            event.jda.removeEventListener(this)
        }
    }
}

fun paginator(vararg pages: Message, expireAfter: Duration = Duration.ofSeconds(DEFAULT_DURATION)): Paginator {
    val nonce = ByteArray(32)
    SecureRandom().nextBytes(nonce)
    return Paginator(Base64.getEncoder().encodeToString(nonce), expireAfter)
        .also { it.addPages(*pages) }
}

fun paginator(vararg pages: MessageEmbed, expireAfter: Duration = Duration.ofSeconds(DEFAULT_DURATION)): Paginator {
    return paginator(*pages.map { Message(embed = it) }.toTypedArray(), expireAfter = expireAfter)
}

fun MessageChannel.sendPaginator(
    paginator: Paginator
) = sendMessage(paginator.also { jda.addEventListener(it) }.pages.first())
    .setActionRows(paginator.controls)
    .delay(paginator.duration)
    .flatMap {
        it.editMessageComponents()
    }

fun MessageChannel.sendPaginator(
    vararg pages: Message,
    expireAfter: Duration = Duration.ofSeconds(DEFAULT_DURATION),
    filter: (ButtonInteraction) -> Boolean = { true }
) = sendPaginator(
    paginator = paginator(*pages, expireAfter = expireAfter).filterBy(filter)
)

fun MessageChannel.sendPaginator(
    vararg pages: MessageEmbed,
    expireAfter: Duration = Duration.ofSeconds(DEFAULT_DURATION),
    filter: (ButtonInteraction) -> Boolean = { true }
) = sendPaginator(
    paginator = paginator(*pages, expireAfter = expireAfter).filterBy(filter)
)

fun InteractionHook.sendPaginator(
    paginator: Paginator,
) = sendMessage(paginator.also { jda.addEventListener(it) }.pages.first())
    .addActionRows(paginator.controls)
    .delay(paginator.duration)
    .flatMap { it.editMessageComponents() }

fun InteractionHook.sendPaginator(
    vararg pages: Message,
    expireAfter: Duration = Duration.ofSeconds(DEFAULT_DURATION),
    filter: (ButtonInteraction) -> Boolean = { true }
) = sendPaginator(
    paginator = paginator(*pages, expireAfter = expireAfter).filterBy(filter)
)

fun InteractionHook.sendPaginator(
    vararg pages: MessageEmbed,
    expireAfter: Duration = Duration.ofSeconds(DEFAULT_DURATION),
    filter: (ButtonInteraction) -> Boolean = { true }
) = sendPaginator(
    paginator = paginator(*pages, expireAfter = expireAfter).filterBy(filter)
)

fun Interaction.replyPaginator(paginator: Paginator) =
    reply(paginator.also { user.jda.addEventListener(it) }.pages.first())
        .addActionRows(paginator.controls)
        .delay(paginator.duration)
        .flatMap { it.editOriginalComponents() }

fun Interaction.replyPaginator(
    vararg pages: Message,
    expireAfter: Duration = Duration.ofSeconds(DEFAULT_DURATION),
    filter: (ButtonInteraction) -> Boolean = { true }
) = replyPaginator(paginator(*pages, expireAfter = expireAfter).filterBy(filter))

fun Interaction.replyPaginator(
    vararg pages: MessageEmbed,
    expireAfter: Duration = Duration.ofSeconds(DEFAULT_DURATION),
    filter: (ButtonInteraction) -> Boolean = { true }
) = replyPaginator(paginator(*pages, expireAfter = expireAfter).filterBy(filter))
