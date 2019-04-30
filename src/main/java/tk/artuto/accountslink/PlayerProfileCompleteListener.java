package tk.artuto.accountslink;

import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import protocolsupport.api.events.PlayerProfileCompleteEvent;

public class PlayerProfileCompleteListener implements Listener
{
    private final AccountsLink plugin;

    PlayerProfileCompleteListener(AccountsLink plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerProfileComplete(PlayerProfileCompleteEvent event)
    {
		plugin.getDatabase().getMainAccount(!(event.getForcedUUID() == null) ? event.getForcedUUID() : event.getConnection().getProfile().getUUID())	
                .ifPresent(event::setForcedUUID);
    }
}
