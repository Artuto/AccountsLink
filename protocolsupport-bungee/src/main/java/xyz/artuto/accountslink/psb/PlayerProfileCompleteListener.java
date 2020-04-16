package xyz.artuto.accountslink.psb;

import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import protocolsupport.api.events.PlayerProfileCompleteEvent;
import xyz.artuto.accountslink.common.AccountsLink;

import java.util.UUID;

public class PlayerProfileCompleteListener implements Listener
{
    private final AccountsLink<?> plugin;

    public PlayerProfileCompleteListener(AccountsLink<?> plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerProfileComplete(PlayerProfileCompleteEvent event)
    {
        UUID primary = plugin.getDataManager().getPrimary(event.getConnection().getPlayer().getUniqueId());

        if(primary == null)
            return;

        event.setForcedUUID(primary);
        event.setForcedName(plugin.getDataManager().getUsername(primary));
    }
}
