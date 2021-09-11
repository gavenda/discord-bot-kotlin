package bot

import bot.command.onAbout
import bot.command.onClear
import bot.discord.interaction.command
import bot.discord.interaction.updateCommands
import bot.discord.listener
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent

object Command {
    const val ABOUT = "about"
    const val CLEAR = "clear"
}

fun JDA.bindCommands(): JDA {
    listener<SlashCommandEvent> {
        when (it.name) {
            Command.ABOUT -> onAbout(it)
            Command.CLEAR -> onClear(it)
        }
    }

    return this
}

fun JDA.updateBotCommands() = updateCommands {
    command(
        name = Command.ABOUT,
        description = "Shows more information about this bot."
    )
    command(
        name = Command.CLEAR,
        description = "Clear the message history between you and the bot. (only works for direct messages)"
    )
}
