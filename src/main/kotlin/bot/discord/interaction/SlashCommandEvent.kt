package bot.discord.interaction

import bot.discord.await
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.commands.CommandInteraction
import net.dv8tion.jda.api.interactions.commands.OptionMapping

/**
 * Finds the first required option with the specified name.
 */
fun CommandInteraction.requiredOption(option: String): OptionMapping {
    return getOption(option) ?: throw IllegalStateException("Required option does not exist!")
}

/**
 * The Integer value for this option.
 */
val OptionMapping.asInt: Int
    get() {
        return this.asLong.toInt()
    }

/**
 * Defer for a reply.
 */
suspend fun SlashCommandEvent.deferReplyAwait(ephemeral: Boolean = false): InteractionHook = deferReply()
    .setEphemeral(ephemeral)
    .await()

/**
 * Send a message.
 */
suspend fun SlashCommandEvent.sendMessageAwait(content: String): Message =
    hook.sendMessage(content)
        .await()
