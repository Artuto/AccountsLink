package xyz.artuto.accountslink;

import net.md_5.bungee.api.plugin.Plugin;
import xyz.artuto.accountslink.common.AccountsLink;
import xyz.artuto.accountslink.common.database.DataManager;
import xyz.artuto.accountslink.common.database.Database;
import xyz.artuto.accountslink.psb.PlayerProfileCompleteListener;
import xyz.artuto.accountslink.waterland.IdentityManagementListener;

import java.io.IOException;
import java.util.logging.Level;

public class AccountsLinkBungee extends Plugin implements AccountsLink<Plugin>
{
    private BungeeConfig config;
    private Database db;
    private DataManager dataManager;

    @Override
    public void onEnable()
    {
        boolean waterLand = false;

		if(detectWaterLand())
        {
            waterLand = true;
            getLogger().info("Detected WaterLand - Using IdentityManagementEvent to rewrite UUIDs.");
        }
		else if(detectPSB())
            getLogger().info("Detected ProtocolSupportBungee - Using PlayerProfileCompleteEvent to rewrite UUIDs.");
		else
		{
			getLogger().severe("This server is not running a supported platform!");
			throw new IllegalStateException();
		}

        try {this.config = new BungeeConfig(this);}
        catch(IOException e)
        {
            getLogger().log(Level.SEVERE, "Error while loading the config file:", e);
			return;
        }

        initDatabase();
		
		if(waterLand)
			getProxy().getPluginManager().registerListener(this, new IdentityManagementListener(this));
		else
			getProxy().getPluginManager().registerListener(this, new PlayerProfileCompleteListener(this));

        getProxy().getPluginManager().registerListener(this, new UsernameUpdaterListener(this));
        getProxy().getPluginManager().registerCommand(this, new GameAccountsCommand(this));
    }

    @Override
    public void onDisable()
    {
        if(!(db == null))
            db.shutdown();
    }

    @Override
    public BungeeConfig getConfig()
    {
        return config;
    }

    @Override
    public DataManager getDataManager()
    {
        return dataManager;
    }

    @Override
    public AccountsLinkBungee getPlugin()
    {
        return this;
    }

    private boolean detectWaterLand()
    {
        try
        {
            Class.forName("xyz.liblnd.waterland.event.IdentityManagementEvent");
            return true;
        }
        catch(ClassNotFoundException ignored)
        {
            return false;
        }
    }

    private boolean detectPSB()
    {
        return !(getProxy().getPluginManager().getPlugin("ProtocolSupportBungee") == null);
    }

    private void initDatabase()
    {
        getLogger().info("Connecting to MySQL Database...");
        this.db = new Database(this, getLogger());
        this.dataManager = new DataManager(db);
    }
}
