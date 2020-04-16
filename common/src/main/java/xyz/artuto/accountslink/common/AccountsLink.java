package xyz.artuto.accountslink.common;

import xyz.artuto.accountslink.common.database.DataManager;

public interface AccountsLink<T>
{
    Config getConfig();

    DataManager getDataManager();

    T getPlugin();
}
