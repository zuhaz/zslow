// Had to write the code in this way to make it as short as I could. :<
package me.zuhaz.zslow.Other

import me.zuhaz.zslow.Main.ZSlow
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class TabCompleterManager : TabCompleter {

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String>? {
        if (!command.name.equals("zslow", ignoreCase = true)) return null

        val mainCommands = mutableListOf("enable", "disable", "settings", "info", "whitelist", "blacklist", "pausechat", "help", "status", "chatlogs", "reload")
        val isAdmin = sender.hasPermission("zslow.admin")

        return when (args.size) {
            1 -> if (isAdmin) mainCommands.filter { it.startsWith(args[0], ignoreCase = true) }.toMutableList()
            else mainCommands.filter { sender.hasPermission("zslow.$it") && it.startsWith(args[0], ignoreCase = true) }.toMutableList()
            2 -> getSubcommands(sender, "zslow.${args[0].toLowerCase()}", args[1], when (args[0].toLowerCase()) {
                "settings" -> listOf("cooldown")
                "whitelist", "blacklist" -> listOf("add", "remove", "list")
                "pausechat", "chatlogs" -> listOf("on", "off")
                else -> emptyList()
            }, isAdmin)
            3 -> if (args[0].equals("settings", ignoreCase = true) && args[1].equals("cooldown", ignoreCase = true))
                getSubcommands(sender, "zslow.settings.cooldown", args[2], listOf("set", "reset"), isAdmin)
            else null
            4 -> if (args[0].equals("settings", ignoreCase = true) && args[1].equals("cooldown", ignoreCase = true) && args[2].equals("set", ignoreCase = true))
                listOf("<time>").filter { it.startsWith(args[3], ignoreCase = true) }.toMutableList()
            else null
            else -> null
        }
    }

    private fun getSubcommands(sender: CommandSender, permission: String, arg: String, validSubcommands: List<String>, isAdmin: Boolean): MutableList<String>? {
        if (isAdmin) {
            return validSubcommands.filter { it.startsWith(arg, ignoreCase = true) }.toMutableList()
        }
        return validSubcommands.filter { sender.hasPermission("$permission.$it") && it.startsWith(arg, ignoreCase = true) }.toMutableList()
    }

}
