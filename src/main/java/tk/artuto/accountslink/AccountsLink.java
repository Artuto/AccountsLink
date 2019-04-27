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

    @Override
    public void onEnable()
    {
        this.LOG = getLogger();
        this.pluginTag = getDescription().getName() + " " + getDescription().getVersion();

        LOG.info("Enabling " + pluginTag + "...");

        try {this.config = new Config(this);}
        catch(IOException e)
        {
            LOG.severe(ChatColor.RED + "Error while loading the config file: " + e.getMessage());
            e.printStackTrace();
        }

        initDatabase();

        try
        {
            getProxy().getPluginManager().registerListener(this, new ProfileEventListener(this));
        }
        catch(NoSuchFieldException e)
        {
            onDisable();
            e.printStackTrace();
        }

        getProxy().getPluginManager().registerCommand(this, new GameAccountsCommand(this));

        LOG.info(ChatColor.GREEN + "Enabled " + pluginTag);
    }

    @Override
    public void onDisable()
    {
        LOG.info("Disabling " + pluginTag + "...");

        if(!(getDatabase()==null))
            getDatabase().shutdown();
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
