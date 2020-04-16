package xyz.artuto.accountslink.common.database;

import co.aikar.idb.DB;
import co.aikar.idb.DatabaseOptions;
import co.aikar.idb.DbRow;
import co.aikar.idb.PooledDatabaseOptions;
import org.intellij.lang.annotations.Language;
import xyz.artuto.accountslink.common.AccountsLink;
import xyz.artuto.accountslink.common.Config;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import static java.util.logging.Level.SEVERE;

@SuppressWarnings("SameParameterValue")
public class Database
{
    private final Logger logger;

    public Database(AccountsLink<?> plugin, Logger logger)
    {
        this.logger = logger;

        init(plugin.getConfig());
        setupTables();
    }

    boolean doInsert(@Language("MySQL") String query, Object... params)
    {
        try
        {
            DB.executeInsert(query, params);
            return true;
        }
        catch(Exception e)
        {
            logger.log(SEVERE, "Exception while inserting into database:", e);
            return false;
        }
    }

    Optional<DbRow> getRow(@Language("MySQL") String query, Object... params)
    {
        DbRow row;

        try {row = DB.getFirstRow(query, params);}
        catch(Exception e)
        {
            logger.log(SEVERE, "Exception while getting info from the database:", e);
            row = null;
        }

        return Optional.ofNullable(row);
    }

    public void shutdown()
    {
        logger.info("Closing Database connection...");
        DB.close();
    }

    private void init(Config config)
    {
        DatabaseOptions options = DatabaseOptions.builder()
                .mysql(config.getDatabaseUser(), config.getDatabasePassword(), config.getDatabase(), config.getDatabaseHost())
                .build();

        Map<String, Object> props = new HashMap<String, Object>()
        {{
            put("useSSL", config.useSSL());
            put("verifyServerCertificate", config.verifyServerCertificate());
            put("autoReconnect", config.autoReconnect());
            put("serverTimezone", "CST"); // Doesn't really matter
            put("characterEncoding", "UTF-8");
        }};

        co.aikar.idb.Database db = PooledDatabaseOptions.builder()
                .dataSourceProperties(props)
                .options(options)
                .createHikariDatabase();

        DB.setGlobalDatabase(db);
        logger.info("Successfully connected to Database.");
    }

    private void setupTables()
    {
        try
        {
            DB.executeUpdate("CREATE TABLE IF NOT EXISTS `accountslink` (main_uuid varchar(50) NOT NULL UNIQUE " +
                    "PRIMARY KEY, secondary_uuid varchar(50) NOT NULL UNIQUE, username NOT NULL)");
        }
        catch(SQLException e) {throw new RuntimeException("Error while setting up database tables:", e);}
    }
}
