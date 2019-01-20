package support.protocol.accountslink;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import protocolsupport.api.ProtocolSupportAPI;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

public class AccountsLink extends Plugin
{
    protected static final String storage_file = "storage.yml";

    private final Storage storage = new Storage();

    @Override
    public void onEnable()
    {
        if(ProtocolSupportAPI.getAPIVersion().compareTo(BigInteger.valueOf(5)) < 0)
            throw new IllegalStateException("ProtocolSupport api version is less than required");

        // TODO: add stuff here
    }

    @Override
    public void onDisable()
    {
        // Plugin shutdown logic
    }

    protected void saveStorage()
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
                config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
            }
            catch(IOException e)
            {
                getLogger().severe("Could not create storage file");
                e.printStackTrace();
                onDisable();
            }
        }

        return config;
    }
}
