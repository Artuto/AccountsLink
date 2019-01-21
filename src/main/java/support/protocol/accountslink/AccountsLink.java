package support.protocol.accountslink;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class AccountsLink extends Plugin
{
    private static final String storage_file = "storage.yml";

    private final Storage storage = new Storage();

    @Override
    public void onEnable()
    {
        storage.load(getStorage());
        getProxy().getScheduler().schedule(this, this::saveStorage, 0, TimeUnit.MINUTES.toSeconds(5) * 20, TimeUnit.SECONDS);
        getProxy().getPluginManager().registerListener(this, new ProfileEventListener(storage));
        getProxy().getPluginManager().registerCommand(this, new GameAccountsCommand(this, storage));
    }

    @Override
    public void onDisable()
    {
        saveStorage();
    }

    private void saveStorage()
    {
        Configuration config = getStorage();
        ConfigurationProvider yaml = ConfigurationProvider.getProvider(YamlConfiguration.class);
        storage.save(config);

        try
        {
            yaml.save(config, new File(getDataFolder(), storage_file));
        }
        catch(IOException e)
        {
            getLogger().severe("Unable to save storage");
            e.printStackTrace();
        }
    }

    private Configuration getStorage()
    {
        Configuration config = null;
        File dataFolder = getDataFolder();
        File file = new File(dataFolder, storage_file);

        if(!(dataFolder.exists()))
            dataFolder.mkdir();
        if(!(file.exists()))
        {
            try
            {
                file.createNewFile();
            }
            catch(IOException e)
            {
                getLogger().severe("Could not create storage file");
                e.printStackTrace();
            }
        }

        try
        {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        }
        catch(IOException e)
        {
            getLogger().severe("Could not load storage file");
            e.printStackTrace();
        }

        return config;
    }
}
