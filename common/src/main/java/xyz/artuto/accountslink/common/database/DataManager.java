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

    public boolean linkAccounts(@NotNull UUID primary, @NotNull UUID secondary)
    {
        return db.doInsert("INSERT INTO accountslink VALUES(?, ?)", primary.toString(), secondary.toString());
    }

    @Nullable
    public UUID getPrimary(@NotNull UUID secondary)
    {
        Optional<DbRow> optRow = db.getRow("SELECT main_uuid WHERE secondary_uuid = ?", secondary.toString());

        return optRow.map(dbRow -> UUID.fromString(dbRow.getString("main_uuid"))).orElse(null);
    }

    @Nullable
    public String getUsername(UUID primary)
    {
        Optional<DbRow> optRow = db.getRow("SELECT username FROM accountslink WHERE main_uuid = ?", primary.toString());

        return optRow.map(dbRow -> dbRow.getString("username")).orElse(null);
    }
}
