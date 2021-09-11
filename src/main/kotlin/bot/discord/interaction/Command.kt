/*
 * Copyright 2020 Florian Spieß
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package bot.discord.interaction

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction

inline fun command(name: String, description: String, builder: CommandData.() -> Unit = {}) =
    CommandData(name, description).apply(builder)

inline fun subcommand(name: String, description: String, builder: SubcommandData.() -> Unit = {}) =
    SubcommandData(name, description).apply(builder)

inline fun subcommandGroup(name: String, description: String, builder: SubcommandGroupData.() -> Unit = {}) =
    SubcommandGroupData(name, description).apply(builder)

inline fun <reified T> option(
    name: String,
    description: String,
    required: Boolean = false,
    builder: OptionData.() -> Unit = {}
): OptionData {
    val type = optionType<T>()
    if (type == OptionType.UNKNOWN)
        throw IllegalArgumentException("Cannot resolve type " + T::class.java.simpleName + " to OptionType!")
    return OptionData(type, name, description).setRequired(required).apply(builder)
}

inline fun CommandListUpdateAction.command(name: String, description: String, builder: CommandData.() -> Unit = {}) =
    addCommands(bot.discord.interaction.command(name, description, builder))

inline fun CommandData.subcommand(name: String, description: String, builder: SubcommandData.() -> Unit = {}) =
    addSubcommands(bot.discord.interaction.subcommand(name, description, builder))

inline fun SubcommandGroupData.subcommand(name: String, description: String, builder: SubcommandData.() -> Unit = {}) =
    addSubcommands(bot.discord.interaction.subcommand(name, description, builder))

inline fun CommandData.group(name: String, description: String, builder: SubcommandGroupData.() -> Unit = {}) =
    addSubcommandGroups(subcommandGroup(name, description, builder))

inline fun <reified T> CommandData.option(
    name: String,
    description: String,
    required: Boolean = false,
    builder: OptionData.() -> Unit = {}
) = addOptions(bot.discord.interaction.option<T>(name, description, required, builder))

inline fun <reified T> SubcommandData.option(
    name: String,
    description: String,
    required: Boolean = false,
    builder: OptionData.() -> Unit = {}
) = addOptions(bot.discord.interaction.option<T>(name, description, required, builder))

fun CommandListUpdateAction.command(name: String, description: String) =
    addCommands(bot.discord.interaction.command(name, description) {})

fun CommandData.subcommand(name: String, description: String) =
    addSubcommands(bot.discord.interaction.subcommand(name, description) {})

fun SubcommandGroupData.subcommand(name: String, description: String) =
    addSubcommands(bot.discord.interaction.subcommand(name, description) {})

fun CommandData.group(name: String, description: String) = addSubcommandGroups(subcommandGroup(name, description) {})

inline fun <reified T> CommandData.option(name: String, description: String, required: Boolean = false) =
    addOptions(bot.discord.interaction.option<T>(name, description, required) {})

inline fun <reified T> SubcommandData.option(name: String, description: String, required: Boolean = false) =
    addOptions(bot.discord.interaction.option<T>(name, description, required) {})

fun OptionData.choice(name: String, value: String) = addChoice(name, value)
fun OptionData.choice(name: String, value: Int) = addChoice(name, value)

inline fun JDA.updateCommands(builder: CommandListUpdateAction.() -> Unit) = updateCommands().apply(builder)
inline fun JDA.upsertCommand(name: String, description: String, builder: CommandData.() -> Unit) =
    upsertCommand(command(name, description, builder))

inline fun Guild.updateCommands(builder: CommandListUpdateAction.() -> Unit) = updateCommands().apply(builder)
inline fun Guild.upsertCommand(name: String, description: String, builder: CommandData.() -> Unit) =
    upsertCommand(command(name, description, builder))

inline fun Command.editCommand(builder: CommandData.() -> Unit) =
    editCommand().apply(CommandData(name, description).apply(builder))

inline fun <reified T> optionType() = when (T::class) {
    Integer::class, Long::class, Short::class, Byte::class -> OptionType.INTEGER
    String::class -> OptionType.STRING
    User::class -> OptionType.USER
    Role::class -> OptionType.ROLE
    Boolean::class -> OptionType.BOOLEAN
    else -> when {
        AbstractChannel::class.java.isAssignableFrom(T::class.java) -> OptionType.CHANNEL
        IMentionable::class.java.isAssignableFrom(T::class.java) -> OptionType.MENTIONABLE
        else -> OptionType.UNKNOWN
    }
}