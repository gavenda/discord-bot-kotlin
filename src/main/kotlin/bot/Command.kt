package bot

import bot.discord.interaction.command
import bot.discord.interaction.updateCommands
import net.dv8tion.jda.api.JDA

object Command {
    const val ABOUT = "about"
    const val CLEAR = "clear"
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
