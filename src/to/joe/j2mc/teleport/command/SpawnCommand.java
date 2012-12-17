package to.joe.j2mc.teleport.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.j2mc.core.J2MC_Manager;
import to.joe.j2mc.core.command.MasterCommand;
import to.joe.j2mc.core.exceptions.BadPlayerMatchException;
import to.joe.j2mc.teleport.J2MC_Teleport;

public class SpawnCommand extends MasterCommand<J2MC_Teleport> {

    public SpawnCommand(J2MC_Teleport plugin) {
        super(plugin);
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, boolean isPlayer) {
        if (isPlayer && (!player.hasPermission("j2mc.teleport.spawn.send") || (args.length < 1))) {
            player.sendMessage(ChatColor.RED + "WHEEEEEEEEEEEEEEE");
            this.plugin.teleport(player, this.plugin.getServer().getWorlds().get(0).getSpawnLocation());
        } else if ((args.length == 1) && sender.hasPermission("j2mc.teleport.spawn.send")) {
            Player target;
            try {
                target = J2MC_Manager.getVisibility().getPlayer(args[0], null);
            } catch (final BadPlayerMatchException e) {
                sender.sendMessage(ChatColor.RED + e.getMessage());
                return;
            }
            this.plugin.teleport(target, this.plugin.getServer().getWorlds().get(0).getSpawnLocation());
            target.sendMessage(ChatColor.RED + "You have been pulled to spawn.");
            J2MC_Manager.getCore().adminAndLog(ChatColor.RED + sender.getName() + " pulled " + target.getName() + " to spawn");
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /spawn playername");
        }
    }

}
