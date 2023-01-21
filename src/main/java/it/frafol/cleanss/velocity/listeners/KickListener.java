package it.frafol.cleanss.velocity.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import it.frafol.cleanss.velocity.CleanSS;
import it.frafol.cleanss.velocity.enums.VelocityConfig;
import it.frafol.cleanss.velocity.enums.VelocityMessages;
import it.frafol.cleanss.velocity.objects.PlayerCache;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class KickListener {

    public CleanSS instance;

    public KickListener(CleanSS instance) {
        this.instance = instance;
    }

    @Subscribe
    public void onPlayerConnect(@NotNull ServerPreConnectEvent event) {

        final Player player = event.getPlayer();
        final RegisteredServer server = event.getOriginalServer();

        if (server == null) {

            if (player.hasPermission(VelocityConfig.RELOAD_PERMISSION.get(String.class))) {
                instance.UpdateChecker(player);
            }

            return;
        }

        if (!server.getServerInfo().getName().equals(VelocityConfig.CONTROL.get(String.class))
                && (PlayerCache.getSuspicious().contains(player.getUniqueId()) || PlayerCache.getAdministrator().contains(player.getUniqueId()))) {

            event.setResult(ServerPreConnectEvent.ServerResult.denied());

        }
    }

    @Subscribe
    public void onPlayerDisconnect(@NotNull DisconnectEvent event) {

        final Optional<RegisteredServer> proxyServer = instance.getServer().getServer(VelocityConfig.CONTROL_FALLBACK.get(String.class));
        final Player player = event.getPlayer();

        if (!proxyServer.isPresent()) {
            return;
        }

        if (PlayerCache.getAdministrator().contains(player.getUniqueId())) {

            PlayerCache.getSuspicious().remove(instance.getValue(PlayerCache.getCouples(), player).getUniqueId());
            PlayerCache.getAdministrator().remove(player.getUniqueId());

            instance.getValue(PlayerCache.getCouples(), player).createConnectionRequest(proxyServer.get()).fireAndForget();

            instance.getValue(PlayerCache.getCouples(), player).sendMessage(LegacyComponentSerializer.legacy('§').deserialize(VelocityMessages.FINISHSUS.color()
                    .replace("%prefix%", VelocityMessages.PREFIX.color())));

            PlayerCache.getCouples().remove(player);

            return;

        }

        if (PlayerCache.getSuspicious().contains(player.getUniqueId())) {

            PlayerCache.getAdministrator().remove(instance.getKey(PlayerCache.getCouples(), player).getUniqueId());
            PlayerCache.getSuspicious().remove(player.getUniqueId());

            instance.getKey(PlayerCache.getCouples(), player).createConnectionRequest(proxyServer.get()).fireAndForget();

            instance.getKey(PlayerCache.getCouples(), player).sendMessage(LegacyComponentSerializer.legacy('§').deserialize(VelocityMessages.LEAVESUS.color()
                    .replace("%prefix%", VelocityMessages.PREFIX.color())
                    .replace("%player%", player.getUsername())));

            PlayerCache.getCouples().remove(instance.getKey(PlayerCache.getCouples(), player));

        }

    }
}
