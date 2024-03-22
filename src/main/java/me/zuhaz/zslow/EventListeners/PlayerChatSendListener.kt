package me.zuhaz.zslow.EventListeners

import me.zuhaz.zslow.Main.ZSlow
import me.zuhaz.zslow.Utils.ZSlowUtils
import org.bukkit.NamespacedKey
import org.bukkit.command.CommandSender
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.persistence.PersistentDataType
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

class PlayerChatSendListener(private val plugin: ZSlow) : Listener {

    companion object {
        private const val CHAT_PAUSED_KEY = "chat_paused"
    }

    private val lastMessageTimes = ConcurrentHashMap<String, Long>()
    private val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onChat(event: AsyncPlayerChatEvent) {
        // Handling chat log system
        if (plugin.config.getBoolean("chat_logs.enabled", false))
            chatLogHandler(event)

        if (ZSlowUtils.checkBlacklist(event.player) && !event.player.hasPermission("zslow.admin")) {
            event.isCancelled = true
            ZSlowUtils.sendMessageToPlayer(event.player as CommandSender, plugin.getMessageFile("messages.you_are_blacklisted"))
            return
        }

        if (ZSlowUtils.checkWhitelist(event.player)) return

        // Handling the chat pausing
        pauseChatHandler(event)

        // Handling chat slow mode
        slowmodeHandler(event)


    }

    private fun pauseChatHandler(event: AsyncPlayerChatEvent) {
        val chatState = event.player.persistentDataContainer.get(NamespacedKey(plugin, CHAT_PAUSED_KEY), PersistentDataType.INTEGER) ?: 0
        if (chatState == 1 && !event.player.isOp && !event.player.hasPermission("zslow.bypass.pausechat") && !event.player.hasPermission("zslow.admin")) {
            event.isCancelled = true
            ZSlowUtils.sendMessageToPlayer(event.player as CommandSender, plugin.getMessageFile("messages.pause_chat"))
        }
    }

    private fun chatLogHandler(event: AsyncPlayerChatEvent) {
        if (event.player.hasPermission("zslow.chatlogs.exempt")) return

        val logsFolder = File(plugin.dataFolder, "chatlogs").apply { mkdirs() }
        val logFile = File(logsFolder, "chatlog_${LocalDate.now()}.txt").apply { createNewFile() }
        appendChatLogMessage(logFile, event)
    }

    private fun slowmodeHandler(event: AsyncPlayerChatEvent) {
        if (!plugin.config.getBoolean("slowmode.enabled", false) || event.isCancelled || event.player.isOp || event.player.hasPermission("zslow.bypass.slowmode") || event.player.hasPermission("zslow.admin")) return

        val playerId = event.player.uniqueId.toString()
        val lastMessageTime = lastMessageTimes[playerId] ?: 0
        plugin.reloadConfig()
        val currentTime = System.currentTimeMillis()
        val cooldownEnd = lastMessageTime + plugin.config.getLong("slowmode.cooldown", 2) * 1000

        // If the current time is greater than or equal to the cooldown end time,
        // update the last message time and return
        if (currentTime >= cooldownEnd) {
            lastMessageTimes[playerId] = currentTime
            return
        }

        // If the cooldown is still active, cancel the event and send a message to the player
        event.isCancelled = true
        if (plugin.config.getBoolean("slowmode.notification_enabled")) {
            val remainingTime = cooldownEnd - currentTime
            val remainingSeconds = TimeUnit.MILLISECONDS.toSeconds(remainingTime)
            ZSlowUtils.sendMessageToPlayer(
                event.player as CommandSender,
                plugin.getMessageFile("messages.slowmode_remaining").toString().replace("{remaining}", remainingSeconds.toString())
            )
        }
    }
    private fun appendChatLogMessage(logFile: File, event: AsyncPlayerChatEvent) {
        try {
            logFile.appendText("${LocalDateTime.now().format(dateTimeFormatter)} - ${event.player.name}: ${event.message}\n")
        } catch (error: Exception) {
            plugin.logger.warning("Failed to append chat log message: ${error.message}")
        }
    }
}
