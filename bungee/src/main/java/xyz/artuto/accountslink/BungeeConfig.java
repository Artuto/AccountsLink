package xyz.artuto.accountslink;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import xyz.artuto.accountslink.common.Config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class BungeeConfig implements Config
{
    private final Configuration config;

    BungeeConfig(AccountsLinkBungee plugin) throws IOException
    {
        File dataFolder = plugin.getDataFolder();
        File file = new File(dataFolder, "config.yml");

        if(!(dataFolder.exists()))
            dataFolder.mkdir();
        if(!(file.exists()))
        {
            try(InputStream stream = plugin.getResourceAsStream("config.yml"))
            {
                Files.copy(stream, file.toPath());}
            catch(IOException e)
            {
                plugin.getLogger().severe("Could not load config file: " + e.getMessage());
                e.printStackTrace();
            }
        }

        this.config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
    }

    @Override
    public boolean autoReconnect()
    {
        return config.getBoolean("autoReconnect");
    }

    @Override
    public boolean useSSL()
    {
        return config.getBoolean("useSSL");
    }

    @Override
    public boolean verifyServerCertificate()
    {
        return config.getBoolean("verifyServerCertificate");
    }

    @Override
    public int getPort()
    {
        return config.getInt("port");
    }

    @Override
    public String getDatabaseHost()
    {
        return config.getString("host");
    }

    @Override
    public String getDatabaseUser()
    {
        return config.getString("user");
    }

    @Override
    public String getDatabasePassword()
    {
        return config.getString("password");
    }

    @Override
    public String getDatabase()
    {
        return config.getString("database");
    }
}
