package bot

import net.dv8tion.jda.api.JDA
import org.kodein.di.instance
import java.net.SocketException
import kotlin.system.exitProcess

fun main() {
    val log by Log4j2("Main")

    try {
        val jda by bot.instance<JDA>()

        // Await and update commands
        jda.awaitReady()

        // Only update when specified
        if (Environment.BOT_UPDATE_COMMANDS) {
            jda.updateBotCommands().queue()
        }

        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() = jda.shutdown()
        })
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