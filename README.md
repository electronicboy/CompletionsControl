### Completions Control

This plugin serves as a demonstration for the ProxyDefineCommandsEvent added in Waterfall (and Travertine), allowing for plugins and server owners to control the commands sent to the client

## Usage/Configuration

The plugin defines several permissions

| Permission                     | Usage                                               |
|:-------------------------------|:----------------------------------------------------|
| completions.whitelist          | Used to whitelist a user from command exclusions    |
| completions.blacklist          | Used to blacklist users from receiving all commands |
| completions.group.\<groupname> | Used to attach a group to a users name              |

The configuration file provides a group mechanism, however, does not (yet?) support inheritance, There is no guarantee of behavior when multiple groups define different permissions

Due to the lack of an inheritance system, the system provides a built-in set of defaults in the config file, which can then be overriden with groups

## License

This project is released under MIT, PRs welcome!
