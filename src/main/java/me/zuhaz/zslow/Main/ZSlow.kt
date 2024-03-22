package me.zuhaz.zslow.Main

import me.zuhaz.zslow.Commands.ZSlowmode
import me.zuhaz.zslow.Other.TabCompleterManager
import me.zuhaz.zslow.EventListeners.PlayerChatSendListener
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class ZSlow : JavaPlugin() {
    private var tabCompleterManager = TabCompleterManager()
    override fun onEnable() {
        logger.info("Enabled ZSlow", )

        // Save the default config.yml file if it does not exist
        saveDefaultConfig()
        reloadConfig()

        // Messages
        if(!File(dataFolder, "messages.yml").exists())
            saveResource("messages.yml", false)


        // Registering Commands
        getCommand("zslow")?.setExecutor(ZSlowmode(this))

        // Registering PlayerChat Send Listener
        server.pluginManager.registerEvents(PlayerChatSendListener(this), this)

        // Tab completers
        getCommand("zslow")?.tabCompleter = tabCompleterManager

    }
    override fun onDisable() {
        logger.info("Disabled ZSlow")
    }
    fun getMessageFile(path: String): String{
        return (config.getString("prefix") + YamlConfiguration.loadConfiguration(File(dataFolder, "messages.yml")).getString(path)) ?: "Message not found $path"
    }
    fun getMessageFileList(key: String): List<String> {
        return  YamlConfiguration.loadConfiguration(File(dataFolder, "messages.yml")).getStringList(key)
    }

}
