package bot.command

import bot.*
import bot.discord.await
import bot.discord.onCommand
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Message

fun JDA.handleClear(): JDA {
    val log by Log4j2("Clear")

    onCommand(Command.CLEAR) { event ->
        event.awaitDeferReply(true)

        val guild = event.guild
        // Assure direct message
        if (guild != null) {
            event.sendLocalizedMessage(LocaleMessage.DirectMessageOnly)
            return@onCommand
        }

        log.debug("Clearing direct messages for user: ${event.user.name}")

        var messages: List<Message>

        do {
            messages = event.privateChannel.history
                .retrievePast(100)
                .await()
            event.privateChannel.deleteMessages(messages)
        } while (messages.isNotEmpty())

        event.sendLocalizedMessage(LocaleMessage.DirectMessageCleared)
    }

    return this
}

