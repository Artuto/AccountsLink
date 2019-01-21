package support.protocol.accountslink;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class GameAccountsCommand extends Command
{
    private final AccountsLink plugin;
    private final Storage storage;

    private final HashMap<String, UUID> codeToUUID = new HashMap<>();
    private final HashMap<UUID, String> uuidToCode = new HashMap<>();

    GameAccountsCommand(AccountsLink plugin, Storage storage)
    {
        super("accountslink");
        this.plugin = plugin;
        this.storage = storage;
    }

    @Override
    public void execute(CommandSender sender, String[] args)
    {
        if(args.length < 1)
        {
            sender.sendMessage(new TextComponent(ChatColor.GREEN + "ProtocolSupportAccountsLinkBungee\n" +
                    ChatColor.YELLOW + "/accountslink code " + ChatColor.RESET + "- Get linking code for this account\n" +
                    ChatColor.YELLOW + "/accountslink link " + ChatColor.RESET + "- Link this account using a code."));

            return;
        }

        if(!(sender instanceof ProxiedPlayer))
        {
            sender.sendMessage(new TextComponent(ChatColor.DARK_RED + "This command is only available to players"));
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;

        switch(args[0].toLowerCase())
        {
            case "code":
            {
                player.sendMessage(new TextComponent("Use this code for linking alts to this account, code is valid for 2 minutes: "
                        + generateCode(player.getUniqueId())));
                break;
            }
            case "link":
            {
                if(args.length < 2)
                    player.sendMessage(new TextComponent(ChatColor.DARK_RED + "Code is needed to use this command"));
                else
                {
                    String code = args[1];
                    UUID mainAccountUUID = codeToUUID.get(code);

                    if(mainAccountUUID == null)
                        sender.sendMessage(new TextComponent(ChatColor.DARK_RED + "This code is invalid"));
                    else
                    {
                        try
                        {
                            storage.addAltAccount(mainAccountUUID, player.getUniqueId());
                            removeCode(mainAccountUUID);
                            sender.sendMessage(new TextComponent(ChatColor.GREEN + "Alt account added, you can now relog"));
                        }
                        catch(Exception e)
                        {
                            sender.sendMessage(new TextComponent(ChatColor.DARK_RED + "Unable to add alt account: " + e.getMessage()));
                        }
                    }
                }
                break;
            }
        }
    }

    private void removeCode(UUID uuid)
    {
        codeToUUID.remove(uuidToCode.remove(uuid));
    }

    private String generateCode(UUID uuid)
    {
        if (uuidToCode.containsKey(uuid))
            removeCode(uuid);

        String randomCode = Utils.generateRandomString(10);
        uuidToCode.put(uuid, randomCode);
        codeToUUID.put(randomCode, uuid);
        plugin.getProxy().getScheduler().schedule(plugin, () -> removeCode(uuid), TimeUnit.MINUTES.toSeconds(2) * 20, TimeUnit.SECONDS);
        return randomCode;
    }
}
