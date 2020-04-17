package xyz.artuto.accountslink;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class UsernameUpdaterListener implements Listener
{
    private final AccountsLinkBungee plugin;

    public UsernameUpdaterListener(AccountsLinkBungee plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event)
    {
        ProxiedPlayer player = event.getPlayer();
        plugin.getDataManager().updateUsername(player.getDisplayName(), player.getUniqueId());
    }
}
