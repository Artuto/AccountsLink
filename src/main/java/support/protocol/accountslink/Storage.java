package support.protocol.accountslink;

import net.md_5.bungee.config.Configuration;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Storage
{
    private final Map<UUID, Set<UUID>> altAccounts = new HashMap<>();
    private final Map<UUID, UUID> mainAccountByAltAccountUUID = new HashMap<>();
    private final Object lock = new Object();

    Optional<UUID> getMainAccount(UUID altAccountUUID)
    {
        synchronized(lock)
        {
            return Optional.ofNullable(mainAccountByAltAccountUUID.get(altAccountUUID));
        }
    }

    void addAltAccount(UUID mainAccountUUID, UUID altAccountUUID)
    {
        synchronized(lock)
        {
            if(mainAccountUUID.equals(altAccountUUID))
                throw new IllegalArgumentException("Main and alt accounts can't be the same");
            if(getMainAccount(mainAccountUUID).isPresent())
                throw new IllegalArgumentException("Provided main account is already an alt account");
            if(getMainAccount(altAccountUUID).isPresent())
                throw new IllegalArgumentException("Provided alt account is already an alt account");

            altAccounts.computeIfAbsent(mainAccountUUID, k -> Collections.newSetFromMap(new ConcurrentHashMap<>())).add(altAccountUUID);
            mainAccountByAltAccountUUID.put(altAccountUUID, mainAccountUUID);
        }
    }

    public void removeAltAccount(UUID mainAccountUUID, UUID altAccountUUID)
    {
        synchronized(lock)
        {
            Set<UUID> alts = altAccounts.get(mainAccountUUID);
            if (alts != null)
            {
                alts.remove(altAccountUUID);
                if(alts.isEmpty())
                    altAccounts.remove(mainAccountUUID);

                mainAccountByAltAccountUUID.remove(altAccountUUID);
            }
        }
    }

    public Set<UUID> getAltAccounts(UUID mainAccountUUID)
    {
        synchronized(lock)
        {
            return Collections.unmodifiableSet(altAccounts.getOrDefault(mainAccountUUID, Collections.emptySet()));
        }
    }

    void load(Configuration config)
    {
        synchronized(lock)
        {
            config.getKeys()
                    .forEach(mainAccountUUIDString -> {
                        UUID mainAccountUUID = UUID.fromString(mainAccountUUIDString);
                        config.getStringList(mainAccountUUIDString)
                                .forEach(altAccountUUIDStr -> addAltAccount(mainAccountUUID, UUID.fromString(altAccountUUIDStr)));
                    });
        }
    }

    void save(Configuration config)
    {
        altAccounts.entrySet()
                .forEach(entry -> config.set(entry.getKey().toString(), entry.getValue().stream().map(UUID::toString)
                        .collect(Collectors.toList())));
    }
}
