![GitHub License](https://img.shields.io/github/license/zuhaz/zslow?style=for-the-badge)
![GitHub last commit](https://img.shields.io/github/last-commit/zuhaz/zslow?style=for-the-badge)
![GitHub language count](https://img.shields.io/github/languages/count/zuhaz/zslow?style=for-the-badge)
![Discord](https://img.shields.io/discord/1220631055845822485?style=for-the-badge&label=Discord%20Server)
![GitHub Downloads (all assets, all releases)](https://img.shields.io/github/downloads/zuhaz/zslow/total?style=for-the-badge)
![GitHub repo size](https://img.shields.io/github/repo-size/zuhaz/zslow?style=for-the-badge)


# ZSlow

ZSlow is a versatile plugin designed to manage chat activity on your Minecraft server. With ZSlow, server administrators can easily control chat by enabling features such as chat pausing, slow mode, and chat logging. Whether you're looking to maintain a more organized chat environment or implement stricter chat regulations, ZSlow provides the tools you need to manage chat activity effectively.

## Features

- **Chat Pausing:** Temporarily pause chat activity to prevent messages from being sent.
- **Slow Mode:** Limit the frequency of messages in the chat to reduce spam and maintain order.
- **Chat Logging:** Log chat messages to keep track of conversations and monitor player activity.
- **Whitelist and Blacklist:** Control who can send messages by adding players to either the whitelist or blacklist.
- **Customizable Settings:** Adjust plugin settings to suit your server's needs and preferences.

## Commands

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


## Permissions

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

1. Download the latest version of ZSlow from the [releases page](https://github.com/yourusername/zslow/releases).
2. Place the downloaded `.jar` file in your server's `plugins` folder.
3. Restart or reload your server.


## License

ZSlow is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
