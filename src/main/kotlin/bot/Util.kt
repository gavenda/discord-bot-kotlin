package bot

import bot.discord.await
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.PrivateChannel
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import java.util.*

/**
 * Defer for a reply.
 */
suspend fun SlashCommandEvent.awaitDeferReply(ephemeral: Boolean = false) = deferReply()
    .setEphemeral(ephemeral)
    .await()

/**
 * Send a localized message.
 */
suspend fun SlashCommandEvent.sendLocalizedMessage(key: String, ephemeral: Boolean = false) =
    hook.sendMessage(
        Messages.whenApplicableFor(user, guild)
            .get(key)
    )
        .setEphemeral(ephemeral)
        .await()

/**
 * Send a localized message.
 */
suspend fun SlashCommandEvent.sendLocalizedMessageIfAcknowledged(key: String) {
    if (hook.interaction.isAcknowledged) {
        sendLocalizedMessage(key)
    }
}

/**
 * Delete the messages in bulk.
 * @param messages messages to delete
 */
suspend fun PrivateChannel.deleteMessages(messages: List<Message>) {
    val messageIds = messages.map { it.idLong }
    val sortedMessageIds = TreeSet<Long>(Comparator.reverseOrder()).apply {
        addAll(messageIds)
    }

    sortedMessageIds.forEach {
        deleteMessageById(it).await()
    }
}
