package pw.valaria.completioncontrol.listeners;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ProxyReloadEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.IOException;

import io.github.waterfallmc.waterfall.event.ProxyDefineCommandsEvent;
import pw.valaria.completioncontrol.WaterfallCompletionControl;

public class CommandsListener implements Listener {

    private WaterfallCompletionControl plugin;

    public CommandsListener(WaterfallCompletionControl plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void declareCommands(ProxyDefineCommandsEvent event) {
        if (!(event.getReceiver() instanceof ProxiedPlayer)) {
            plugin.getLogger().fine(event.getReceiver() + " is not a player");
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) event.getReceiver();

        if (player.hasPermission("completions.whitelist")) {
            plugin.debug(player.getName() + " is whitelisted");
            return;
        }

        if (player.hasPermission("completions.blacklist")) {
            plugin.debug(player.getName() + " is blacklisted");
            event.getCommands().clear();
            return;
        }

        event.getCommands().entrySet().removeIf(commandEntry -> !plugin.checkCommand(player, commandEntry.getKey(), commandEntry.getValue()));

    }

    @EventHandler
    public void onReload(ProxyReloadEvent event) {
        try {
            plugin.loadConfig();
        } catch (IOException e) {
            plugin.getLogger().info("Failed to read configuration file!");
            e.printStackTrace();
        }
    }
}
