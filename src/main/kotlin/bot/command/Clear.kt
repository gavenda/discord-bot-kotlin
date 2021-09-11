package bot.command

import bot.Log4j2
import bot.deleteMessages
import bot.discord.await
import bot.discord.interaction.deferReplyAwait
import bot.discord.interaction.sendMessageAwait
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent

suspend fun onClear(event: SlashCommandEvent) {
    val log by Log4j2("Clear")

    event.deferReplyAwait(true)

    // Assure direct message
    if (event.isFromGuild) {
        event.sendMessageAwait("This can only be done inside direct messages.")
        return
    }

    log.debug("Clearing direct messages for user: ${event.user.name}")

    var messages: List<Message>

    do {
        messages = event.privateChannel.history
            .retrievePast(100)
            .await()
        event.privateChannel.deleteMessages(messages)
    } while (messages.isNotEmpty())

    event.sendMessageAwait("I have cleared the trash for you.")
}
