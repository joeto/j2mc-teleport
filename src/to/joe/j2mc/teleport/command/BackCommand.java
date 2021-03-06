package to.joe.j2mc.teleport.command;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import to.joe.j2mc.core.command.MasterCommand;
import to.joe.j2mc.teleport.J2MC_Teleport;
import to.joe.j2mc.teleport.util.ImmutableLocation;

public class BackCommand extends MasterCommand<J2MC_Teleport> {

    public BackCommand(J2MC_Teleport teleport) {
        super(teleport);
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, boolean isPlayer) {
        if (isPlayer) {
            ImmutableLocation loc = plugin.lastLocations.get(player.getName().toLowerCase());
            if (loc == null) {
                sender.sendMessage(ChatColor.RED + "Ain't no place to go back to");
                return;
            }
            Location target = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
            player.teleport(target);
        }
    }

}
