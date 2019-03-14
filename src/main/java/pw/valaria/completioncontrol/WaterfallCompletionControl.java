package pw.valaria.completioncontrol;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import pw.valaria.completioncontrol.listeners.CommandsListener;

public final class WaterfallCompletionControl extends Plugin {

  private final ConfigurationProvider provider = ConfigurationProvider
      .getProvider(YamlConfiguration.class);
  private File configFile;
  private Configuration config;

  private boolean isDefaultBlacklist = false;
  private boolean isDebug = false;

  private final Set<String> defaults = new HashSet<>();
  private final Map<String, GroupData> groups = new HashMap<>();

  @Override
  public void onEnable() {
    // Plugin startup logic

    this.configFile = new File(getDataFolder(), "config.yml");

    try {
      loadConfig();
    } catch (IOException e) {
      e.printStackTrace();
      return;
    }

    this.getProxy().getPluginManager().registerListener(this, new CommandsListener(this));

  }

  @Override
  public void onDisable() {
    // Plugin shutdown logic
  }

  public synchronized void loadConfig() throws IOException {


    if (!configFile.exists()) {
      configFile.getParentFile().mkdirs();

      final InputStream resourceConfigStream = this.getClass().getClassLoader()
          .getResourceAsStream("config.yml");
      if (resourceConfigStream != null) {
        Files.copy(resourceConfigStream, configFile.toPath());
      }
    }


    this.config = this.provider.load(configFile, null);

    this.defaults.clear();
    this.groups.clear();

    this.isDebug = this.config.getBoolean("debug", false);

    final Configuration defaultsSection = this.config.getSection("defaults");

    this.isDefaultBlacklist = defaultsSection.getBoolean("blacklist", false);
    this.defaults.addAll(defaultsSection.getStringList("completions"));

    final Configuration groupsSection = this.config.getSection("groups");
    for (String key : groupsSection.getKeys()) {

      final Configuration groupSection = groupsSection.getSection(key);
      boolean isWhitelist = !groupSection.getBoolean("blacklist", isDefaultBlacklist);
      Set<String> completions = new HashSet<>(groupSection.getStringList("completions"));

      this.groups.put(key, new GroupData(completions, isWhitelist));

    }


  }

  private void saveConfig() throws IOException {
    this.provider.save(this.config, configFile);
  }

  public synchronized boolean checkCommand(ProxiedPlayer player, String label, Command command) {
    boolean isAllowed = true;

    if (this.defaults.contains(label) && this.isDefaultBlacklist) {
      isAllowed = false;
      debug(player.getName() + " got '" + label + "' as blacklisted by default");
    }

    for (Map.Entry<String, GroupData> entry : this.groups.entrySet()) {
      String groupName = entry.getKey();
      GroupData groupData = entry.getValue();

      if (player.hasPermission("completions.group." + groupName)) {
        if (isDebug()) {
          debug("Processing group '" + groupName + "'{"+  groupData + "} for " + player.getName());
        }

        debug(groupData.toString());
        // If we are allowed, and the group is not a whitelist, kill
        if (!groupData.isWhitelist() && isAllowed &&  groupData.has(label)) {
          isAllowed = false;
          debug(player.getName() + " was denied '" + label + "' by group " + groupName);
        } else if (!isAllowed && groupData.isWhitelist() && groupData.has(label)) {
          debug(player.getName() + " was granted '" + label + "' by group " + groupName);
          isAllowed = true;
        }
      }

    }
    return isAllowed;
  }


  public boolean isDebug() {
    return isDebug;
  }

  public void debug(String message) {
    if (isDebug) {
      getLogger().info(String.format("[debug] %s", message));
    }
  }

}
