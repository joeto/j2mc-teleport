package to.joe.j2mc.teleport.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import to.joe.j2mc.core.command.MasterCommand;
import to.joe.j2mc.teleport.J2MC_Teleport;

public class ProtectMeCommand extends MasterCommand {

    public ProtectMeCommand(J2MC_Teleport teleport) {
        super(teleport);
    }

    @Override
    public void exec(CommandSender sender, String commandName, String[] args, Player player, boolean isPlayer) {
        if (isPlayer) {
            if (((J2MC_Teleport) this.plugin).isProtected(player.getName())) {
                ((J2MC_Teleport) this.plugin).setProtected(player.getName(), false);
                player.sendMessage(ChatColor.RED + "You are now no longer protected from teleportation");
            } else {
                ((J2MC_Teleport) this.plugin).setProtected(player.getName(), true);
                player.sendMessage(ChatColor.RED + "You are protected from teleportation");
            }
        }
    }
}
