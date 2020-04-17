package xyz.artuto.accountslink.common.database;

import co.aikar.idb.DbRow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class DataManager
{
    private final Database db;

    public DataManager(Database db)
    {
        this.db = db;
    }

    public void updateUsername(@NotNull String username, @NotNull UUID uuid)
    {
        db.doInsert("INSERT INTO accountslink (main_uuid, username) VALUES(?, ?) " +
                "ON DUPLICATE KEY UPDATE username = ?", uuid.toString(), username, username);
    }

    public void removeUser(@NotNull UUID secondary)
    {
        db.doUpdate("DELETE FROM accountslink WHERE main_uuid = ?", secondary.toString());
    }

    public boolean linkAccounts(@NotNull UUID primary, @NotNull UUID secondary)
    {
        return db.doInsert("INSERT INTO accountslink (main_uuid, secondary_uuid) VALUES(?, ?) ON DUPLICATE " +
                "KEY UPDATE secondary_uuid = ?", primary.toString(), secondary.toString(), secondary.toString());
    }

    @Nullable
    public UUID getPrimary(@NotNull UUID secondary)
    {
        Optional<DbRow> optRow = db.getRow("SELECT main_uuid FROM accountslink WHERE secondary_uuid = ?",
                secondary.toString());

        return optRow.map(dbRow -> UUID.fromString(dbRow.getString("main_uuid"))).orElse(null);
    }

    @Nullable
    public String getUsername(@NotNull UUID primary)
    {
        Optional<DbRow> optRow = db.getRow("SELECT username FROM accountslink WHERE main_uuid = ?", primary.toString());

        return optRow.map(dbRow -> dbRow.getString("username")).orElse(null);
    }
}
