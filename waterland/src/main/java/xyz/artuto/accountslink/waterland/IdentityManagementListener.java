package xyz.artuto.accountslink.waterland;

import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import xyz.artuto.accountslink.common.AccountsLink;
import xyz.liblnd.waterland.event.IdentityManagementEvent;

import java.util.UUID;

public class IdentityManagementListener implements Listener
{
    private final AccountsLink<Plugin> plugin;

    public IdentityManagementListener(AccountsLink<Plugin> plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onIdentityManagement(IdentityManagementEvent event)
    {
        UUID primary = plugin.getDataManager().getPrimary(event.getUuid());

        if(primary == null)
            return;

        event.setUuid(primary);
        event.setUsername(plugin.getDataManager().getUsername(primary));
    }
}
