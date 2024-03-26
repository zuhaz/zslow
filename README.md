![GitHub License](https://img.shields.io/github/license/zuhaz/zslow?style=for-the-badge)
![GitHub last commit](https://img.shields.io/github/last-commit/zuhaz/zslow?style=for-the-badge)
![GitHub language count](https://img.shields.io/github/languages/count/zuhaz/zslow?style=for-the-badge)
![Discord](https://img.shields.io/discord/1220631055845822485?style=for-the-badge&label=Discord%20Server)
![GitHub Downloads (all assets, all releases)](https://img.shields.io/github/downloads/zuhaz/zslow/total?style=for-the-badge)
![GitHub repo size](https://img.shields.io/github/repo-size/zuhaz/zslow?style=for-the-badge)

![ZSLOW](https://cdn.discordapp.com/attachments/1192128307731378266/1220896106251288699/Zslow_Description_Stuff.png?ex=66109b1a&is=65fe261a&hm=79c534fd5adf692544f9fb5512065346f39d7cbeb9dad8985f7243f9f9752a09&)

# ZSlow

ZSlow is a versatile plugin designed to manage chat activity on your Minecraft server. With ZSlow, server administrators can easily control chat by enabling features such as chat pausing, slow mode, and chat logging. Whether you're looking to maintain a more organized chat environment or implement stricter chat regulations, ZSlow provides the tools you need to manage chat activity effectively.

![FEATURES](https://media.discordapp.net/attachments/1192128307731378266/1220899246136365137/ZSlow_Features_Banner.png?ex=66109e07&is=65fe2907&hm=da3c8b36d56f37d79d4b98a17d4605563723a5d90a252ac6706cc40124c9fb61&=&format=webp&quality=lossless&width=1440&height=168)

- **Chat Pausing:** Temporarily pause chat activity to prevent messages from being sent.
- **Slow Mode:** Limit the frequency of messages in the chat to reduce spam and maintain order.
- **Chat Logging:** Log chat messages to keep track of conversations and monitor player activity.
- **Whitelist and Blacklist:** Control who can send messages by adding players to either the whitelist or blacklist.
- **Customizable Settings:** Adjust plugin settings to suit your server's needs and preferences.

![COMMANDS](https://media.discordapp.net/attachments/1192128307731378266/1220898956914200677/ZSlow_Commands_Banner.png?ex=66109dc2&is=65fe28c2&hm=1236a9952d2ea2b7e39c5431904e00ea8b65693a852944278069599849573d60&=&format=webp&quality=lossless&width=1440&height=168)

- **/zslow enable**: Activate the chat management system.
- **/zslow disable**: Deactivate the chat management system.
- **/zslow settings**: Configure plugin settings, including chat cooldown.
- **/zslow info**: Obtain information about the plugin.
- **/zslow whitelist**:
    - `/zslow whitelist add <player>`: Add a player to the whitelist.
    - `/zslow whitelist remove <player>`: Remove a player from the whitelist.
    - `/zslow whitelist list`: Display all whitelisted players.
- **/zslow blacklist**:
    - `/zslow blacklist add <player>`: Add a player to the blacklist.
    - `/zslow blacklist remove <player>`: Remove a player from the blacklist.
    - `/zslow blacklist list`: Display all blacklisted players.
- **/zslow pausechat**:
    - `/zslow pausechat on`: Pause the chat.
    - `/zslow pausechat off`: Unpause the chat.
- **/zslow help**: Access plugin help information.
- **/zslow status**: Check the status of the chat management system.
- **/zslow reload**: Reload plugin configurations.
- **/zslow chatlogs**:
    - `/zslow chatlogs on`: Enable chat logging.
    - `/zslow chatlogs off`: Disable chat logging.


![PERMISSIONS](https://media.discordapp.net/attachments/1192128307731378266/1220898991924056104/ZSlow_Permissions_Banner.png?ex=66109dca&is=65fe28ca&hm=c3190c75f54d191e40e17da1e5132e5bb22f07f16bc29bb0780dc89f99315fc9&=&format=webp&quality=lossless&width=1440&height=168)

- `zslow.admin`: Allows access to all ZSlow commands and features.
- `zslow.whitelist`: Allows access to manage players on the whitelist.
- `zslow.blacklist`: Allows access to manage players on the blacklist.
- `zslow.info`: Allows access to view plugin information.
- `zslow.pausechat`: Allows access to pause or resume chat activity.
- `zslow.help`: Allows access to view plugin help information.
- `zslow.status`: Allows access to view the current status of chat management features.
- `zslow.reload`: Allows access to reload plugin configurations.
- `zslow.chatlogs`: Allows access to enable or disable chat logging.
- `zslow.bypass.slowmode`: Allows bypassing the slowmode restrictions in ZSlow.
- `zslow.bypass.pausechat`:  Allows bypassing the pausechat restrictions in ZSlow.
- `zslow.chatlogs.exempt`: Allows bypassing the pausechat restrictions in ZSlow.

## Support

For support or assistance with the ZSlow plugin, please visit our [GitHub repository](https://github.com/zuhaz/zslow) or contact us through the SpigotMC forums or join our [Discord Server](https://discord.gg/7mhdvfgybX).

## Installation

1. Download the latest version of ZSlow from the [releases page](https://github.com/zuhaz/zslow/releases). 
2. Place the downloaded `.jar` file in your server's `plugins` folder.
3. Restart or reload your server.


## License

ZSlow is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
