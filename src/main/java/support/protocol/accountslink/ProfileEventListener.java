package support.protocol.accountslink;

import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import protocolsupport.api.events.PlayerProfileCompleteEvent;

public class ProfileEventListener implements Listener
{
    private final AccountsLink plugin;

    ProfileEventListener(AccountsLink plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void onProfileComplete(PlayerProfileCompleteEvent event)
    {
        plugin.getDatabase().getMainAccount(event.getForcedUUID() != null ? event.getForcedUUID() : event.getConnection().getProfile().getUUID())
                .ifPresent(event::setForcedUUID);
    }
}
