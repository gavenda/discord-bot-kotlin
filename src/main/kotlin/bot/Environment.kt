package bot

object Environment {
    /**
     * The bot token for this application.
     */
    val BOT_TOKEN: String
        get() =
            System.getenv("BOT_TOKEN")

    val BOT_SHARD_ID: Int
        get() =
            System.getenv("BOT_SHARD_ID")
                .toInt()

    val BOT_SHARD_TOTAL: Int
        get() =
            System.getenv("BOT_SHARD_TOTAL")
                .toInt()

    /**
     * Database JDBC url.
     */
    val DB_URL: String
        get() =
            System.getenv("DB_URL")

    /**
     * Database user name.
     */
    val DB_USER: String
        get() =
            System.getenv("DB_USER")

    /**
     * Database user password.
     */
    val DB_PASS: String
        get() =
            System.getenv("DB_PASS")
}