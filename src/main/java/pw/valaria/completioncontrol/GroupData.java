package pw.valaria.completioncontrol;

import java.util.Set;

class GroupData {

  private boolean isWhitelist;
  private Set<String> commands;

  GroupData(Set<String> commands, boolean isWhitelist) {
    this.commands = commands;
    this.isWhitelist = isWhitelist;
  }

  boolean has(String command) {
    return this.commands.contains(command);
  }

  boolean isWhitelist() {
    return this.isWhitelist;
  }

  @Override
  public String toString() {
    return "GroupData{" +
        "isWhitelist=" + isWhitelist +
        ", commands=" + commands +
        '}';
  }
}
