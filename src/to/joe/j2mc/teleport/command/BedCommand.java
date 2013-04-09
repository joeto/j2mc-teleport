package to.joe.j2mc.teleport.command;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.j2mc.core.command.MasterCommand;
import to.joe.j2mc.teleport.J2MC_Teleport;

public class BedCommand extends MasterCommand<J2MC_Teleport> {

    public BedCommand(J2MC_Teleport plugin) {
        super(plugin);
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, boolean isPlayer) {
        if (!isPlayer) {
            sender.sendMessage(ChatColor.RED + "Only players may use this command");
            return;
        }
        Location bedLoc = player.getBedSpawnLocation();
        if (bedLoc != null) {
            player.teleport(bedLoc);
            player.sendMessage(ChatColor.GOLD + "There's no place like home, there's no place like home, there's no place like home...");
        } else {
            player.sendMessage(ChatColor.RED + "You do not appear to have a bed");
        }
    }
}
