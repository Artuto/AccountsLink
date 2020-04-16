package xyz.artuto.accountslink;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import xyz.artuto.accountslink.common.Utils;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class GameAccountsCommand extends Command
{
    private final AccountsLinkBungee plugin;
    private final HashMap<String, UUID> codeToUUID = new HashMap<>();
    private final HashMap<UUID, String> uuidToCode = new HashMap<>();

    GameAccountsCommand(AccountsLinkBungee plugin)
    {
        super("accountlink");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args)
    {
        if(args.length < 1)
        {
            sender.sendMessage(new TextComponent(ChatColor.GREEN + "AccountsLink\n" +
                    ChatColor.YELLOW + "/accountlink code " + ChatColor.RESET + "- Get linking code for this account\n" +
                    ChatColor.YELLOW + "/accountlink link " + ChatColor.RESET + "- Link this account using a code."));

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
                player.sendMessage(new TextComponent("Use this code for linking a secondary account to this account, " +
                        "code is valid for 5 minutes: " + generateCode(player.getUniqueId())));
                break;
            }
            case "link":
            {
                link(player, args);
                break;
            }
            default:
                player.sendMessage(new TextComponent(ChatColor.RED + "Invalid sub-command!"));
        }
    }

    private void link(ProxiedPlayer player, String[] args)
    {
        if(args.length < 2)
        {
            player.sendMessage(new TextComponent(ChatColor.DARK_RED + "Code is required to use this command"));
            return;
        }

        String code = args[1];
        UUID mainAccountUUID = codeToUUID.get(code);

        if(mainAccountUUID == null)
        {
            player.sendMessage(new TextComponent(ChatColor.DARK_RED + "This code is invalid"));
            return;
        }

        if(!(plugin.getDataManager().getPrimary(mainAccountUUID) == null))
        {
            player.sendMessage(new TextComponent(ChatColor.RED + "This account is already linked!"));
            return;
        }

        boolean result = plugin.getDataManager().linkAccounts(mainAccountUUID, player.getUniqueId());
        if(result)
            player.sendMessage(new TextComponent(ChatColor.GREEN + "Alt account added, you can now relog"));
        else
            player.sendMessage(new TextComponent(ChatColor.RED + "Unknown error occurred, contact an Admin."));

        removeCode(mainAccountUUID);
    }

    private void removeCode(UUID uuid)
    {
        codeToUUID.remove(uuidToCode.remove(uuid));
    }

    private String generateCode(UUID uuid)
    {
        if(uuidToCode.containsKey(uuid))
            removeCode(uuid);

        String randomCode = Utils.generateRandomString(10);
        uuidToCode.put(uuid, randomCode);
        codeToUUID.put(randomCode, uuid);

        plugin.getProxy().getScheduler().schedule(plugin, () -> removeCode(uuid), TimeUnit.MINUTES.toSeconds(5),
                TimeUnit.SECONDS);
        return randomCode;
    }
}
