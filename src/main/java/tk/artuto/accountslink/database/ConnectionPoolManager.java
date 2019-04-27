package tk.artuto.accountslink.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.md_5.bungee.api.ChatColor;
import tk.artuto.accountslink.AccountsLink;

import java.sql.Connection;
import java.sql.SQLException;

class ConnectionPoolManager
{
    private final AccountsLink plugin;

    private HikariDataSource hds;

    private boolean autoReconnect, useSSL, verifyServerCertificate;
    private int port;
    private String host, user, password, database;

    ConnectionPoolManager(AccountsLink plugin)
    {
        this.plugin = plugin;

        // Assign values
        init();

        // Check for default values
        if(isConfigInvalid())
        {
            plugin.LOG.severe(ChatColor.RED + "You haven't configured Database details in your config!");
            return;
        }

        // Actually Connect
        setupPool();
    }

    private boolean isConfigInvalid()
    {
        return host.isEmpty() || user.isEmpty() || password.isEmpty() || database.isEmpty();
    }

    private void init()
    {
        autoReconnect = plugin.getConfig().autoReconnect();
        useSSL = plugin.getConfig().useSSL();
        verifyServerCertificate = plugin.getConfig().verifyServerCertificate();
        port = plugin.getConfig().getPort();
        host = plugin.getConfig().getDatabaseHost();
        user = plugin.getConfig().getDatabaseUser();
        password = plugin.getConfig().getDatabasePassword();
        database = plugin.getConfig().getDatabaseName();
    }

    private void setupPool()
    {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database +
                "?autoReconnect=" + autoReconnect +
                "&useSSL=" + useSSL +
                "&verifyServerCertificate=" + verifyServerCertificate);
        config.setDriverClassName("com.mysql.jdbc.Driver");
        config.setUsername(user);
        config.setPassword(password);
        config.setPoolName("ALB-Pool");
        setupDefaults(config);

        hds = new HikariDataSource(config);
    }

    private void setupDefaults(HikariConfig dbConfig)
    {
        dbConfig.addDataSourceProperty("cachePrepStmts", true);
        dbConfig.addDataSourceProperty("prepStmtCacheSize", 250);
        dbConfig.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        dbConfig.addDataSourceProperty("useServerPrepStmts", true);
        dbConfig.addDataSourceProperty("useLocalSessionState", true);
        dbConfig.addDataSourceProperty("rewriteBatchedStatements", true);
        dbConfig.addDataSourceProperty("elideSetAutoCommits", true);
        dbConfig.addDataSourceProperty("maintainTimeStats", false);
        dbConfig.addDataSourceProperty("maximumPoolSize", 20);
        dbConfig.setInitializationFailTimeout(10000);
        dbConfig.setValidationTimeout(15000);
    }

    Connection getConnection() throws SQLException
    {
        return hds.getConnection();
    }

    void close(Connection conn)
    {
        if(!(conn==null))
        {
            try {conn.close();}
            catch(SQLException ignored) {}
        }
    }

    void shutdown()
    {
        if(!(hds==null) && !(hds.isClosed()))
            hds.close();

        plugin.LOG.info("Successfully closed Database connection.");
    }
}
