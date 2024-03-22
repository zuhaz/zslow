package me.zuhaz.zslow.Utils

import me.zuhaz.zslow.Main.ZSlow
import org.bukkit.ChatColor.translateAlternateColorCodes
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet

object ZSlowUtils {
    fun sendMessageToPlayer(commandSender: CommandSender, message: Any) {
        commandSender.sendMessage(translateAlternateColorCodes('&', message.toString()))
    }

    class ZDatabaseManager {
        private val dbUrl = "jdbc:sqlite:plugins/ZSlow/zdatabase.db"

        fun isPlayerInList(playerUID: String, listType: String): Boolean {
            val query = "SELECT * FROM $listType WHERE player_uid = ?"
            return try {
                DriverManager.getConnection(dbUrl).use { connection ->
                    val preparedStatement: PreparedStatement = connection.prepareStatement(query)
                    preparedStatement.setString(1, playerUID)
                    val resultSet: ResultSet = preparedStatement.executeQuery()
                    resultSet.next()
                }
            } catch (e: Exception) {
                println("Error executing query: ${e.message}")
                false
            }
        }
        fun listWhitelistEntries(): List<String> {
            val query = "SELECT player_uid FROM whitelist"
            val whitelistEntries = mutableListOf<String>()
            try {
                DriverManager.getConnection(dbUrl).use { connection ->
                    val preparedStatement: PreparedStatement = connection.prepareStatement(query)
                    val resultSet: ResultSet = preparedStatement.executeQuery()
                    while (resultSet.next()) {
                        whitelistEntries.add(resultSet.getString("player_uid"))
                    }
                }
            } catch (e: Exception) {
                println("Error executing query: ${e.message}")
            }
            return whitelistEntries
        }

        fun listBlacklistEntries(): List<String> {
            val query = "SELECT player_uid FROM blacklist"
            val blacklistEntries = mutableListOf<String>()
            try {
                DriverManager.getConnection(dbUrl).use { connection ->
                    val preparedStatement: PreparedStatement = connection.prepareStatement(query)
                    val resultSet: ResultSet = preparedStatement.executeQuery()
                    while (resultSet.next()) {
                        blacklistEntries.add(resultSet.getString("player_uid"))
                    }
                }
            } catch (e: Exception) {
                println("Error executing query: ${e.message}")
            }
            return blacklistEntries
        }
    }

    fun checkWhitelist(player: Player): Boolean = ZDatabaseManager().isPlayerInList(player.uniqueId.toString(), "whitelist")

    fun checkBlacklist(player: Player): Boolean = ZDatabaseManager().isPlayerInList(player.uniqueId.toString(), "blacklist")

    fun getPluginPermissions(): List<String> = listOf(
        "zslow.whitelist", "zslow.blacklist", "zslow.info", "zslow.pausechat", "zslow.help",
        "zslow.status", "zslow.reload", "zslow.enable", "zslow.disable", "zslow.whitelist",
        "zslow.settings", "zslow.chatlogs"
    )

    fun checkCommandPermission(sender: CommandSender, plugin: ZSlow, permissionNode: String, sendMessage: Boolean): Boolean {
        return if (permissionNode == "zslow.admin") true
        else sender.hasPermission(permissionNode).also {
            if (!it && sendMessage)
                sendMessageToPlayer(sender, plugin.getMessageFile("messages.no_permission"))
        }
    }

}
