package bot.command

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent

const val GUILD_CONTEXT_ERROR = "Not in a guild!"

class GuildContext(
    val event: SlashCommandEvent,
    private val _guild: Guild? = null,
    private val _invoker: Member? = null,
) {
    val invoker: Member
        get() {
            return _invoker ?: throw IllegalStateException(GUILD_CONTEXT_ERROR)
        }
    val guild: Guild
        get() {
            return _guild ?: throw IllegalStateException(GUILD_CONTEXT_ERROR)
        }
    val selfMember: Member
        get() {
            return _guild?.selfMember ?: throw IllegalStateException(GUILD_CONTEXT_ERROR)
        }
}

/**
 * Assuming this command is in a guild context.
 */
val SlashCommandEvent.guildContext: GuildContext
    get() {
        if(isDirectMessage) throw IllegalStateException(GUILD_CONTEXT_ERROR)

        return GuildContext(
            event = this,
            _guild = this.guild,
            _invoker = this.member,
        )
    }

/**
 * Checks whether this slash command happened inside a guild.
 */
val SlashCommandEvent.isDirectMessage: Boolean get() = guild == null

