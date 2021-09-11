package bot

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.User
import java.util.*

/**
 * Message key constants.
 */
object LocaleMessage {
    const val About = "about"
    const val DirectMessageOnly = "direct-message-only"
    const val DirectMessageCleared = "direct-message-cleared"
    const val UnknownError = "unknown-error"
}

/**
 * Simple localization message helper for users/guilds with different locales.
 */
object Messages {

    /**
     * Use user locale.
     */
    fun whenApplicableFor(user: User? = null, guild: Guild? = null): MessageContext {
        if (user != null) return MessageUserContext(user)
        if (guild != null) return MessageGuildContext(guild)
        return MessageDefaultContext()
    }
}

interface MessageContext {
    /**
     * Get the message given the key.
     * @param key the key
     */
    fun get(key: String): String

    /**
     * Get a list of messages given the key.
     * @param key the key
     */
    fun getList(key: String): List<String>
}

class MessageDefaultContext : MessageContext {
    private val messages = ResourceBundle.getBundle("i18n.messages", Locale.getDefault())
    override fun get(key: String): String = messages.getString(key)
    override fun getList(key: String) = messages.getStringArray(key).toList()
}

/**
 * Messages in a guild context.
 * @param guild the guild to get messages from
 */
@Suppress("UNUSED_PARAMETER")
class MessageGuildContext(guild: Guild) : MessageContext {
    private val messages = ResourceBundle.getBundle("i18n.messages", guild.appLocale)
    override fun get(key: String): String = messages.getString(key)
    override fun getList(key: String) = messages.getStringArray(key).toList()
}

/**
 * Messages in a user context.
 * @param user the user to get messages from
 */
@Suppress("UNUSED_PARAMETER")
class MessageUserContext(user: User) : MessageContext {
    private val messages = ResourceBundle.getBundle("i18n.messages", user.locale)
    override fun get(key: String): String = messages.getString(key)
    override fun getList(key: String) = messages.getStringArray(key).toList()
}