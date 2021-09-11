package bot

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.User
import java.util.*

/**
 * Retrieves the locale of this guild. Will prioritize community locale over app setting.
 */
val Guild.appLocale: Locale
    get() {
        // return the community locale if enabled
        if (features.contains("COMMUNITY")) {
            return locale
        }
        return Locale.getDefault()
    }

/**
 * Retrieves the user set locale.
 */
val User.locale: Locale
    get() {
        return Locale.getDefault()
    }