package support.protocol.accountslink.database;

import net.md_5.bungee.api.ChatColor;
import support.protocol.accountslink.AccountsLink;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

public class Database
{
    private final AccountsLink plugin;
    private final ConnectionPoolManager pool;
    private final Logger LOG;

    public Database(AccountsLink plugin)
    {
        this.plugin = plugin;
        this.pool = new ConnectionPoolManager(plugin);
        plugin.LOG.info(ChatColor.GREEN + "Successfully connected to Database.");
        this.LOG = plugin.LOG;

        setupTables();
        makeInitialCache();
    }

    public boolean isLinked(String uuid)
    {
        Connection conn = null;

        try
        {
            conn = pool.getConnection();
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM accountslink WHERE main_uuid = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setString(1, uuid);
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery())
            {
                return results.next();
            }
        }
        catch(SQLException e)
        {
            LOG.severe("Error while checking if the specified UUID (" + uuid + ") is linked: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
        finally {pool.close(conn);}
    }

    public int linkAccounts(UUID main, UUID secondary)
    {
        Connection conn = null;

        try
        {
            conn = pool.getConnection();
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM accountslink WHERE main_uuid = ? AND secondary_uuid = ?",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.setString(1, main.toString());
            statement.setString(2, secondary.toString());
            statement.closeOnCompletion();

            try(ResultSet results = statement.executeQuery())
            {
                if(!(results.next()))
                {
                    results.moveToInsertRow();
                    results.updateString("main_uuid", main.toString());
                    results.updateString("secondary_uuid", secondary.toString());
                    results.insertRow();

                    plugin.accounts.put(secondary, main);
                    return 1;
                }

                return 0;
            }
        }
        catch(SQLException e)
        {
            LOG.severe("Error while setting up database tables: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
        finally {pool.close(conn);}
    }

    public Optional<UUID> getMainAccount(UUID uuid)
    {
        return Optional.ofNullable(plugin.accounts.get(uuid));
    }

    public void shutdown()
    {
        LOG.info("Closing Database connection...");
        pool.shutdown();
        plugin.accounts.clear();
    }

    private void makeInitialCache()
    {
        plugin.getProxy().getScheduler().runAsync(plugin, () -> {
            Connection conn = null;

            try
            {
                conn = pool.getConnection();
                PreparedStatement statement = conn.prepareStatement("SELECT * FROM accountslink",
                        ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                statement.closeOnCompletion();

                try(ResultSet results = statement.executeQuery())
                {
                    while(results.next())
                    {
                        UUID main = UUID.fromString(results.getString("main_uuid"));
                        UUID secondary = UUID.fromString(results.getString("secondary_uuid"));

                        plugin.accounts.put(secondary, main);
                    }
                }
            }
            catch(SQLException e)
            {
                LOG.severe("Error while caching accounts: " + e.getMessage());
                e.printStackTrace();
            }
            finally {pool.close(conn);}
        });
    }

    private void setupTables()
    {
        Connection conn = null;

        try
        {
            conn = pool.getConnection();
            PreparedStatement statement = conn.prepareStatement("CREATE TABLE IF NOT EXISTS `accountslink`" +
                            "(java_uuid varchar(50) NOT NULL UNIQUE PRIMARY KEY, bedrock_uuid varchar(50) NOT NULL UNIQUE)",
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

            statement.closeOnCompletion();
            statement.execute();
        }
        catch(SQLException e)
        {
            LOG.severe("Error while setting up database tables: " + e.getMessage());
            e.printStackTrace();
        }
        finally {pool.close(conn);}
    }
}
