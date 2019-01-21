package support.protocol.accountslink;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class Config
{
    private final Configuration config;

    Config(AccountsLink plugin) throws IOException
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

    public boolean autoReconnect()
    {
        return config.getBoolean("autoReconnect");
    }

    public boolean useSSL()
    {
        return config.getBoolean("useSSL");
    }

    public boolean verifyServerCertificate()
    {
        return config.getBoolean("verifyServerCertificate");
    }

    public int getPort()
    {
        return config.getInt("port");
    }

    public String getDatabaseHost()
    {
        return config.getString("host");
    }

    public String getDatabaseUser()
    {
        return config.getString("user");
    }

    public String getDatabasePassword()
    {
        return config.getString("password");
    }

    public String getDatabaseName()
    {
        return config.getString("database");
    }
}
