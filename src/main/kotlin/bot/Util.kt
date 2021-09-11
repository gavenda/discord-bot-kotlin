package bot

import bot.discord.await
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.PrivateChannel
import java.util.*

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
