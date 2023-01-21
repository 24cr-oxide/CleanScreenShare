package it.frafol.cleanss.velocity.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.proxy.Player;
import it.frafol.cleanss.velocity.CleanSS;
import it.frafol.cleanss.velocity.enums.VelocityConfig2;
import it.frafol.cleanss.velocity.objects.PlayerCache;
import org.jetbrains.annotations.NotNull;

public class CommandListener {

    public final CleanSS instance;

    public CommandListener(CleanSS instance) {
        this.instance = instance;
    }

    @Subscribe
    public void onPlayerCommand(@NotNull CommandExecuteEvent event) {

        if (!(event.getCommandSource() instanceof Player)) {
            return;
        }

        final Player player = (Player) event.getCommandSource();

        if (PlayerCache.getSuspicious().contains(player.getUniqueId())) {

            if (!(player.getProtocolVersion().getProtocol() >= ProtocolVersion.getProtocolVersion(759).getProtocol())) {

                event.setResult(CommandExecuteEvent.CommandResult.denied());

            } else {

                if (!VelocityConfig2.REMOVE_WARNINGS.get(Boolean.class)) {

                    instance.getLogger().warn("Unable to delete command for " + player.getUsername() + ". " +
                            "This is a Velocity issue affecting Minecraft 1.19+ clients. " +
                            "This cannot be fixed on Velocity, please do not ask for support if you have this issue.");

                }

            }

        }

    }
}
