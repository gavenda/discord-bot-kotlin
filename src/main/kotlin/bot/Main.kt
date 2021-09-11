package bot

import bot.discord.await
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.ReadyEvent
import org.kodein.di.instance
import java.net.SocketException
import kotlin.system.exitProcess

fun main() = runBlocking {
    val log by Log4j2("Main")

    try {
        val jda by bot.instance<JDA>()

        // Await and update commands
        jda.await<ReadyEvent>()

        // Only update when specified
        if (Environment.BOT_UPDATE_COMMANDS) {
            jda.updateBotCommands().await()
        }
    } catch (e: SocketException) {
        // Unable to connect, exit
        log.error("Cannot connect", e)
        exitProcess(1)
    } catch (e: Exception) {
        // Something went wrong and we do not know
        log.error("Cannot start", e)
        exitProcess(-1)
    }

}