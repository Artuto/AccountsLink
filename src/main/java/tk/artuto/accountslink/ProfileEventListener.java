package tk.artuto.accountslink;

import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.api.event.LoginEvent;

import java.lang.reflect.Field;

public class ProfileEventListener implements Listener
{
    private final AccountsLink plugin;
    private final Field field;

    ProfileEventListener(AccountsLink plugin) throws NoSuchFieldException
    {
        this.plugin = plugin;
        this.field = InitialHandler.class.getDeclaredField("uniqueId");
        this.field.setAccessible(true);
    }

    @EventHandler
    public void onLogin(LoginEvent event)
    {
		PendingConnection conn = event.getConnection();
        plugin.getDatabase().getMainAccount(conn.getUniqueId()).ifPresent(newUUID ->
        {
            try
            {
                field.set(conn, newUUID);
            }
            catch(IllegalAccessException e)
            {
                e.printStackTrace();
            }
        });
    }
}
