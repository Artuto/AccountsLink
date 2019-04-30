package tk.artuto.accountslink;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;
import tk.artuto.accountslink.database.Database;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class AccountsLink extends Plugin
{
    public Logger LOG;
    public Map<UUID, UUID> accounts = new HashMap<>();

    private Config config;
    private Database db;
    private String pluginTag;
	
	private boolean waterdog;
	private boolean psb;

    @Override
    public void onEnable()
    {
        this.LOG = getLogger();
        this.pluginTag = getDescription().getName() + " " + getDescription().getVersion();

        LOG.info("Enabling " + pluginTag + "...");
		
		if(detectWaterdog())
		{
			this.waterdog = true;
			getLogger().info("Detected Waterdog - Using IdentityManagementEvent to rewrite UUIDs.");
		}
		else if(detectPSB())
		{
			this.psb = true;
			getLogger().info("Detected ProtocolSupportBungee - Using PlayerProfileCompleteEvent to rewrite UUIDs.");
			getLogger().warning("WARNING Waterdog is recommended over ProtocolSupportBungee as it is more stable for PE Protocols WARNING");
		}
		else
		{
			getLogger().severe("This server is not running a supported platform!");
			throw new IllegalStateException();
		}

        try {this.config = new Config(this);}
        catch(IOException e)
        {
            LOG.severe(ChatColor.RED + "Error while loading the config file: " + e.getMessage());
            e.printStackTrace();
			onDisable();
			return;
        }

        initDatabase();
		
		if(waterdog)
			getProxy().getPluginManager().registerListener(this, new IdentityManagementListener(this));
		else if(psb)
			getProxy().getPluginManager().registerListener(this, new PlayerProfileCompleteListener(this));			
		
		getProxy().getPluginManager().registerCommand(this, new GameAccountsCommand(this));

        LOG.info(ChatColor.GREEN + "Enabled " + pluginTag);
    }

    @Override
    public void onDisable()
    {
        LOG.info("Disabling " + pluginTag + "...");

        if(!(getDatabase() == null))
            getDatabase().shutdown();
    }
	
	private boolean detectWaterdog()
	{
		try
        {
            Class.forName("network.ycc.waterdog.api.event.IdentityManagementEvent");
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
        LOG.info("Connecting to MySQL Database...");
        db = new Database(this);
    }

    public Config getConfig()
    {
        return config;
    }

    Database getDatabase()
    {
        return db;
    }
}
