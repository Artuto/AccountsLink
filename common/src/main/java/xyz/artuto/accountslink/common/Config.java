package xyz.artuto.accountslink.common;

public interface Config
{
    boolean autoReconnect();

    boolean useSSL();

    boolean verifyServerCertificate();

    int getPort();

    String getDatabaseHost();

    String getDatabaseUser();

    String getDatabasePassword();

    String getDatabase();
}
