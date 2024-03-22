package me.zuhaz.zslow.Commands

import me.zuhaz.zslow.Main.ZSlow
import me.zuhaz.zslow.Utils.ZSlowUtils
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType
import java.io.File
import java.sql.DriverManager
import java.sql.SQLException
import java.util.*

class ZSlowmode(private var plugin: ZSlow) : CommandExecutor {
    private val dbUrl = "jdbc:sqlite:plugins/ZSlow/zdatabase.db"

    init {
        initializeDatabase()
    }
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (command.name.equals("zslow", ignoreCase = true)) {
            when {
                args.isEmpty() -> {
                    ZSlowUtils.sendMessageToPlayer(sender, plugin.getMessageFile("messages.invalid_command"))
                    return false
                }
                else -> handleCommand(sender, args)
            }
            return true
        }
        return false
    }

    private fun handleCommand(sender: CommandSender, args: Array<out String>) {
        when (args[0].lowercase()) {
            "whitelist", "blacklist" -> handleListCommand(sender, args)
            "info", "pausechat", "help", "status", "reload" -> handleOtherCommands(sender, args)
            "enable", "disable" -> handleEnableDisableCommand(sender, args)
            "settings" -> handleSettingsCommand(sender, args)
            "chatlogs" -> handleChatLogsCommand(sender, args)
            else -> ZSlowUtils.sendMessageToPlayer(sender, plugin.getMessageFile("messages.invalid_command"))
        }
    }

    private fun handleListCommand(sender: CommandSender, args: Array<out String>) {
        if (args.size >= 3) {
            val playerName = args[2]
            // Check if playerName is not null or empty
            if (playerName.isNotEmpty()) {
                val player = Bukkit.getPlayer(playerName)
                if (player != null) {
                    when (args[1].lowercase()) {
                        "add" -> {
                            when (args[0].toLowerCase()) {
                                "whitelist" -> {
                                    if (ZSlowUtils.checkBlacklist(player.player!!)) return ZSlowUtils.sendMessageToPlayer(sender, plugin.getMessageFile("messages.remove_from_blacklist"))
                                    addToWhitelist(player, sender)
                                }
                                "blacklist" -> {
                                    if (ZSlowUtils.checkWhitelist(player.player!!)) return ZSlowUtils.sendMessageToPlayer(sender, plugin.getMessageFile("messages.remove_from_whitelist"))
                                    addToBlacklist(player, sender)
                                }
                                else -> ZSlowUtils.sendMessageToPlayer(sender, plugin.getMessageFile("messages.invalid_command"))
                            }
                        }
                        "remove" -> {
                            when (args[0].toLowerCase()) {
                                "whitelist" -> {
                                    removeFromWhitelist(player, sender)
                                }
                                "blacklist" -> {
                                    removeFromBlacklist(player, sender)
                                }
                                else -> ZSlowUtils.sendMessageToPlayer(sender, plugin.getMessageFile("messages.invalid_command"))
                            }
                        }
                        else -> ZSlowUtils.sendMessageToPlayer(sender, plugin.getMessageFile("messages.invalid_command"))
                    }
                } else {
                    ZSlowUtils.sendMessageToPlayer(sender, plugin.getMessageFile("messages.player_not_found").replace("{player}", args[2]))
                }
            } else {
                ZSlowUtils.sendMessageToPlayer(sender, plugin.getMessageFile("messages.not_a_valid_player"))
            }
        } else if (args.size >= 2) {
            when (args[1].lowercase()) {
                "list" -> {
                    when (args[0].lowercase()) {
                        "whitelist" -> {
                            val whitelistEntries = ZSlowUtils.ZDatabaseManager().listWhitelistEntries()
                            val playerNames = whitelistEntries.mapNotNull { Bukkit.getPlayer(UUID.fromString(it))?.name }
                            ZSlowUtils.sendMessageToPlayer(sender, plugin.getMessageFile("messages.whitelisted_players").replace("{whitelistedplayers}",
                                playerNames.joinToString().ifEmpty { "Not found" }))
                        }
                        "blacklist" -> {
                            val blacklistEntries = ZSlowUtils.ZDatabaseManager().listBlacklistEntries()
                            val playerNames = blacklistEntries.mapNotNull { Bukkit.getPlayer(UUID.fromString(it))?.name }
                            ZSlowUtils.sendMessageToPlayer(sender, plugin.getMessageFile("messages.blacklisted_players").replace("{blacklistedplayers}",
                                playerNames.joinToString().ifEmpty { "Not found" }))
                        }
                        else -> ZSlowUtils.sendMessageToPlayer(sender, plugin.getMessageFile("messages.invalid_command"))
                    }
                }
                else -> ZSlowUtils.sendMessageToPlayer(sender, plugin.getMessageFile("messages.invalid_command"))
            }
        } else {
            ZSlowUtils.sendMessageToPlayer(sender, plugin.getMessageFile("messages.invalid_command"))
        }
    }

    private fun handleOtherCommands(sender: CommandSender, args: Array<out String>) : Boolean{
        // Handle other commands
        // Implement logic for info, pause, help, status, log commands
        when(args[0].toLowerCase()) {
            "info"-> sendPluginInfoMessage(sender)
            "pausechat" -> handlePauseCommand(sender, args.sliceArray(1 until args.size))
            "status"-> sendPluginStatusMessage(sender)
            "help" ->{
                val page = if (args.getOrNull(1)?.matches(Regex("\\d+")) == true) {
                    args[1].toInt()
                } else {
                    1
                }
                sendHelpMessage(sender, page)
            }
            "reload" ->{
                // Reload the plugin configuration
                if(!ZSlowUtils.checkCommandPermission(sender, plugin, "zslow.reload", true)) return false
                plugin.saveDefaultConfig()

                val customConfig = YamlConfiguration.loadConfiguration(File(plugin.dataFolder, "messages.yml"))
                customConfig.load(File(plugin.dataFolder, "messages.yml"))

                // Send a confirmation message to the sender
                ZSlowUtils.sendMessageToPlayer(sender, plugin.getMessageFile("messages.reloaded_plugin"))
            }
            else -> ZSlowUtils.sendMessageToPlayer(sender, plugin.getMessageFile("messages.invalid_command"))
        }
        return true
    }

    private fun handleEnableDisableCommand(sender: CommandSender, args: Array<out String>) {
        val isEnabled = args[0].equals("enable", ignoreCase = true)
        plugin.config.set("enabled", isEnabled)
        plugin.saveConfig()
        val messageKey = if (isEnabled) "messages.slowmode_enabled" else "messages.slowmode_disabled"
        val delay = plugin.config.getLong("slowmode.cooldown", 2) * 1000 // Default to 2000 if not specified
        ZSlowUtils.sendMessageToPlayer(sender, plugin.getMessageFile(messageKey).replace("{cooldown}", (delay / 1000).toString()))
    }

    private fun handleSettingsCommand(sender: CommandSender, args: Array<out String>): Boolean {
        if (args.size >= 2) {
            when (args[1].lowercase()) {
                "cooldown" -> {
                    if (args.size >= 3) {
                        when (args[2].lowercase()) {
                            "set" -> {
                                return if (args.size == 4) {
                                    val time = args[3].toIntOrNull()
                                    if (time != null) {
                                        // Implement logic to set cooldown here
                                        plugin.config.set("slowmode.cooldown", time)
                                        plugin.saveConfig()
                                        ZSlowUtils.sendMessageToPlayer(sender, plugin.getMessageFile("messages.cooldown_set").replace("{time}", time.toString()))
                                        true
                                    } else {
                                        ZSlowUtils.sendMessageToPlayer(sender, plugin.getMessageFile("messages.invalid_time_format"))
                                        false
                                    }
                                } else {
                                    ZSlowUtils.sendMessageToPlayer(sender, plugin.getMessageFile("messages.invalid_command"))
                                    false
                                }
                            }
                            "reset" -> {
                                // Implement logic to reset cooldown here
                                plugin.config.set("slowmode.cooldown", 2)
                                plugin.saveConfig()
                                ZSlowUtils.sendMessageToPlayer(sender,
                                    plugin.getMessageFile("messages.cooldown_reset")
                                )
                                return true
                            }
                            else -> {
                                ZSlowUtils.sendMessageToPlayer(sender, plugin.getMessageFile("messages.invalid_command"))
                                return false
                            }
                        }
                    } else {
                        ZSlowUtils.sendMessageToPlayer(sender, plugin.getMessageFile("messages.invalid_command"))
                        return false
                    }
                }
                else -> {
                    ZSlowUtils.sendMessageToPlayer(sender, plugin.getMessageFile("messages.invalid_command"))
                    return false
                }
            }
        } else {
            ZSlowUtils.sendMessageToPlayer(sender, plugin.getMessageFile("messages.invalid_command"))
            return false
        }
    }
    private fun handleChatLogsCommand(commandSender: CommandSender, args: Array<out String>): Boolean {
        if (args.size >= 2) {

            return when (args[1].lowercase()) {
                "on" -> {
                    plugin.config.set("chat_logs.enabled", true)
                    plugin.saveConfig()
                    ZSlowUtils.sendMessageToPlayer(commandSender,
                        plugin.getMessageFile("messages.chatlogs_enabled")
                    )
                    true
                }

                "off" -> {
                    plugin.config.set("chat_logs.enabled", false)
                    plugin.saveConfig()
                    ZSlowUtils.sendMessageToPlayer(commandSender,
                        plugin.getMessageFile("messages.chatlogs_disabled")
                    )
                    true
                }

                else -> {
                    ZSlowUtils.sendMessageToPlayer(commandSender,
                        plugin.getMessageFile("messages.invalid_command")
                    )
                    false
                }
            }
        } else {
            ZSlowUtils.sendMessageToPlayer(commandSender,plugin.getMessageFile("messages.invalid_command"))
            return false
        }
    }
    private fun handlePauseCommand(commandSender: CommandSender, args: Array<out String>): Boolean {
        if (args.size in 1..1) {
            val chatStateKey = NamespacedKey(plugin, "chat_paused")
            val newChatState = when (args[0].lowercase()) {
                "on" -> 1
                "off" -> 0
                else -> {
                    ZSlowUtils.sendMessageToPlayer(commandSender,
                        plugin.getMessageFile("messages.invalid_command")
                    )
                    return false
                }
            }

            // Directly update the chat state for all players
            plugin.server.worlds.forEach { world ->
                world.players.forEach { player ->
                    player.persistentDataContainer.set(chatStateKey, PersistentDataType.INTEGER, newChatState)
                }
            }

            // Send a message to the command sender
            ZSlowUtils.sendMessageToPlayer(commandSender,
                (if (newChatState == 1) plugin.getMessageFile("messages.enable_pause") else plugin.getMessageFile("messages.disable_pause")).toString()
            )

        } else {
            ZSlowUtils.sendMessageToPlayer(commandSender, plugin.getMessageFile("messages.invalid_command"))
            return false
        }
        return true
    }
    private fun sendHelpMessage(player: CommandSender, page: Int) {
        val helpMessages = plugin.getMessageFileList("messages.help_messages") ?: return

        val pageSize = 5
        val totalPages = (helpMessages.size + pageSize - 1) / pageSize

        if (page < 1 || page > totalPages) {
            ZSlowUtils.sendMessageToPlayer(
                player,
                plugin.getMessageFile("messages.invalid_page_number").replace(plugin.config.getString("prefix").toString(), "").replace("{totalpages}", totalPages.toString())
            )
            return
        }

        val start = (page - 1) * pageSize
        val end = minOf(start + pageSize, helpMessages.size)

        // Send the header message on each page
        ZSlowUtils.sendMessageToPlayer(
            player,
            plugin.getMessageFile("messages.help_header").replace(plugin.config.getString("prefix").toString(), "").replace("{page}", page.toString()).replace("{totalPages}", totalPages.toString())
        )

        for (i in start until end) {
            ZSlowUtils.sendMessageToPlayer(
                player,
                helpMessages[i]
            )
        }
        // Send the "Type /zslow help {page} for the next page." message except on the last page
        if (page < totalPages) {
            ZSlowUtils.sendMessageToPlayer(
                player,
                plugin.getMessageFile("messages.help_next_page").replace(plugin.config.getString("prefix").toString(), "").replace("{page}", (page + 1).toString())
            )
        }
    }

    private fun sendPluginInfoMessage(commandSender: CommandSender) {
        val infoMessages = plugin.getMessageFileList("messages.info_messages") ?: return

        val version = plugin.description.version
        val author = plugin.description.authors.joinToString(", ")
        val description = plugin.description.description
        val website = plugin.description.website ?: "Not available"

        for (message in infoMessages) {
            val formattedMessage = message
                .replace("{version}", version)
                .replace("{author}", author)
                .replace("{description}", description.toString())
                .replace("{website}", website)

            ZSlowUtils.sendMessageToPlayer(commandSender, formattedMessage)
        }
    }

    private fun sendPluginStatusMessage(commandSender: CommandSender) {
        val statusMessage = plugin.getMessageFileList("messages.status_messages")

        val enabled = if (plugin.config.getBoolean("slowmode.enabled", false)) "Enabled" else "Disabled"
        val cooldown = plugin.config.getLong("slowmode.cooldown", 2).toString() + " seconds"
        val chatPaused = when (commandSender) {
            is Player -> commandSender.persistentDataContainer.get(NamespacedKey(plugin, "chat_paused"), PersistentDataType.INTEGER)
            else -> 0 // Assume chat is not paused for non-players
        }
        val status = if (chatPaused == 1) "Enabled" else "Disabled"
        val chatLoggingState = if (plugin.config.getBoolean("chat_logs", false)) "Enabled" else "Disabled"

        for (message in statusMessage) {
            val formattedMessage = message
                .replace("{enabled}", enabled)
                .replace("{cooldown}", cooldown)
                .replace("{paused}", status)
                .replace("{logging}", chatLoggingState)

            ZSlowUtils.sendMessageToPlayer(commandSender, formattedMessage)
        }
    }

    private fun initializeDatabase() {
        try {
            DriverManager.getConnection(dbUrl).use { connection ->
                val statement = connection.createStatement()
                statement.executeUpdate(
                    """
                    CREATE TABLE IF NOT EXISTS whitelist (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        player_uid TEXT UNIQUE
                    )
                    """
                )
                statement.executeUpdate(
                    """
                    CREATE TABLE IF NOT EXISTS blacklist (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        player_uid TEXT UNIQUE
                    )
                    """
                )
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    private fun addToWhitelist(player: Player, sender: CommandSender) {
        try {
            if(player.hasPermission("zslow.admin") || player.isOp) return ZSlowUtils.sendMessageToPlayer(sender, plugin.getMessageFile("messages.cant_whitelist_admin"))
            DriverManager.getConnection(dbUrl).use { connection ->
                val statement = connection.prepareStatement("INSERT OR IGNORE INTO whitelist (player_uid) VALUES (?)")
                statement.setString(1, player.uniqueId.toString())
                val rowsAffected = statement.executeUpdate()
                if (rowsAffected > 0) {
                    ZSlowUtils.sendMessageToPlayer(sender, plugin.getMessageFile("messages.whitelist_add").replace("{player}", player.name))
                    connection.close()
                } else {
                    ZSlowUtils.sendMessageToPlayer(sender, plugin.getMessageFile("messages.already_in_whitelist"))
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            ZSlowUtils.sendMessageToPlayer(sender, plugin.getMessageFile("messages.error_occurred_add_to_whitelist").replace("{player}", player.name))
        }
    }
    private fun removeFromWhitelist(player: Player, sender: CommandSender) {
        try {
            DriverManager.getConnection(dbUrl).use { connection ->
                val statement = connection.prepareStatement("DELETE FROM whitelist WHERE player_uid = ?")
                statement.setString(1, player.uniqueId.toString())
                val rowsAffected = statement.executeUpdate()
                if (rowsAffected > 0) {
                    ZSlowUtils.sendMessageToPlayer(sender, plugin.getMessageFile("messages.whitelist_remove").replace("{player}", player.name))
                    connection.close()
                } else {
                    ZSlowUtils.sendMessageToPlayer(sender, plugin.getMessageFile("messages.whitelist_not_found"))
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            ZSlowUtils.sendMessageToPlayer(sender, plugin.getMessageFile("messages.error_occurred_remove_from_whitelist").replace("{player}", player.name))

        }
    }

    private fun addToBlacklist(player: Player, sender: CommandSender) {
        try {
            if(player.hasPermission("zslow.admin") || player.isOp) return ZSlowUtils.sendMessageToPlayer(sender, plugin.getMessageFile("messages.cant_blacklist_admin"))
            DriverManager.getConnection(dbUrl).use { connection ->
                val statement = connection.prepareStatement("INSERT OR IGNORE INTO blacklist (player_uid) VALUES (?)")
                statement.setString(1, player.uniqueId.toString())
                val rowsAffected = statement.executeUpdate()
                if (rowsAffected > 0) {
                    ZSlowUtils.sendMessageToPlayer(sender, plugin.getMessageFile("messages.blacklist_add").replace("{player}", player.name))
                    connection.close()
                } else {
                    ZSlowUtils.sendMessageToPlayer(sender, plugin.getMessageFile("messages.already_in_blacklist"))
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            ZSlowUtils.sendMessageToPlayer(sender, plugin.getMessageFile("messages.error_occurred_add_to_blacklist").replace("{player}", player.name))

        }
    }
    private fun removeFromBlacklist(player: Player, sender: CommandSender) {
        try {
            DriverManager.getConnection(dbUrl).use { connection ->
                val statement = connection.prepareStatement("DELETE FROM blacklist WHERE player_uid = ?")
                statement.setString(1, player.uniqueId.toString())
                val rowsAffected = statement.executeUpdate()
                if (rowsAffected > 0) {
                    ZSlowUtils.sendMessageToPlayer(sender, plugin.getMessageFile("messages.blacklist_remove").replace("{player}", player.name))
                    connection.close()
                } else {
                    ZSlowUtils.sendMessageToPlayer(sender, plugin.getMessageFile("messages.blacklist_not_found"))
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            ZSlowUtils.sendMessageToPlayer(sender, plugin.getMessageFile("messages.error_occurred_remove_from_blacklist").replace("{player}", player.name))
        }
    }
}
