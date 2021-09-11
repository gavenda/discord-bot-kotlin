package bot.command

import bot.Embed
import bot.discord.await
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import java.util.*

suspend fun onAbout(event: SlashCommandEvent) {
    val java = System.getProperty("java.vendor")
    val javaVersion = System.getProperty("java.version")
    val sys = System.getProperty("os.name")
    val sysArch = System.getProperty("os.arch")
    val sysVersion = System.getProperty("os.version")

    event.replyEmbeds(
        Embed {
            title = "About"
            url = "https://github.com/gavenda/discord-bot-kotlin"
            description = "Your discord bot description goes here."
            field {
                name = "Version"
                value = VERSION
                inline = true
            }
            field {
                name = "Language"
                value = "[Kotlin](https://kotlinlang.org)"
                inline = true
            }
            field {
                name = "Framework"
                value = "[JDA](https://github.com/DV8FromTheWorld/JDA)"
                inline = true
            }
            field {
                name = "Shard"
                value = event.jda.shardInfo.shardString
                inline = true
            }
            field {
                name = "Operating System"
                value = "$java Java $javaVersion on $sys $sysVersion ($sysArch)"
                inline = true
            }
            footer {
                name = "You can help with the development by dropping by on GitHub."
                iconUrl = "https://github.com/fluidicon.png"
            }
        }
    )
        .setEphemeral(true)
        .await()
}

val VERSION: String
    get() =
        Properties().apply {
            load(object {}.javaClass.getResourceAsStream("/version.properties"))
        }.getProperty("version") ?: "-"