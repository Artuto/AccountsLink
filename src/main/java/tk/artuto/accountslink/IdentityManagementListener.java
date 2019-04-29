package tk.artuto.accountslink;

import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import network.ycc.waterdog.api.event.IdentityManagementEvent;

public class IdentityManagementListener implements Listener
{
    private final AccountsLink plugin;

    IdentityManagementListener(AccountsLink plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void onIdentityManagement(IdentityManagementEvent event)
    {
        /*event.registerIntent(plugin);
        plugin.getProxy().getScheduler().runAsync(plugin, () ->
        {*/
            plugin.getDatabase().getMainAccount(event.getUuid()).ifPresent(event::setUuid);
            /*event.completeIntent(plugin);
        });*/
    }
}
