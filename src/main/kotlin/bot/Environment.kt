package bot

object Environment {
    /**
     * The bot token for this application.
     */
    val BOT_TOKEN: String
        get() =
            System.getenv("BOT_TOKEN")

    /**
     * The bot shard identifier.
     */
    val BOT_SHARD_ID: Int
        get() =
            System.getenv("BOT_SHARD_ID")
                .toInt()

    /**
     * The bot shard total.
     */
    val BOT_SHARD_TOTAL: Int
        get() =
            System.getenv("BOT_SHARD_TOTAL")
                .toInt()

    /**
     * Flag whether to update interaction commands on startup.
     */
    val BOT_UPDATE_COMMANDS: Boolean
        get() =
            System.getenv("BOT_UPDATE_COMMANDS")
                .toBoolean()
}