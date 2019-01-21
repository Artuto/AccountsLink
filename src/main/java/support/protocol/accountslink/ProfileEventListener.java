package support.protocol.accountslink;

import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import protocolsupport.api.events.PlayerProfileCompleteEvent;

public class ProfileEventListener implements Listener
{
    protected final Storage storage;

    public ProfileEventListener(Storage storage)
    {
        this.storage = storage;
    }

    @EventHandler
    public void onProfileComplete(PlayerProfileCompleteEvent event)
    {
        storage.getMainAccount(event.getForcedUUID() != null ? event.getForcedUUID() : event.getConnection().getProfile().getUUID())
                .ifPresent(event::setForcedUUID);
    }
}
