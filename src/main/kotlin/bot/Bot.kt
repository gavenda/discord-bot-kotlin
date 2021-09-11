package bot

import bot.discord.useCoroutines
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton

/**
 * Main application module. All your instances should come from here.
 */
val bot = DI {
    bind<JDA>() with singleton {
        JDABuilder.createLight(Environment.BOT_TOKEN)
            .useCoroutines()
            .useSharding(Environment.BOT_SHARD_ID, Environment.BOT_SHARD_TOTAL)
            .setEnableShutdownHook(true)
            .build()
            .bindCommands()
    }
}
