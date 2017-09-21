package au.com.addstar.bc.commands;

import au.com.addstar.bc.BungeeChat;
import au.com.addstar.bc.ChatChannelManager;
import au.com.addstar.bc.sync.IMethodCallback;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.*;

/**
 * Created for the Ark: Survival Evolved.
 * Created by Narimm on 17/08/2017.
 */
public class ChannelListCommand implements CommandExecutor {


    private static BungeeChat instance;
    public ChannelListCommand(BungeeChat plugin) {
        instance = plugin;
    }

    @Override
    public boolean onCommand(final CommandSender commandSender, Command command, String s, String[] args) {
        if(commandSender.hasPermission("bungeechat.chatlist")) {
            boolean subnf = true;
            if(args.length > 0 && args[0].equals("all") && commandSender.hasPermission("bungeechat.chatlist.all")){
                subnf = false;
            }
            final boolean sub = subnf;
            BungeeChat.getSyncManager().callSyncMethod("bchat:getSubscribed",new Subscribed(commandSender,sub));
                       return true;
        }else{
            commandSender.sendMessage("No Permission");
        }
        return false;
    }

    private class Subscribed  implements IMethodCallback<HashMap<UUID,String>> {

        private CommandSender sender;
        private boolean sub;

        public Subscribed(CommandSender sender, boolean sub) {
            this.sender = sender;
            this.sub = sub;
        }

        public void onFinished(HashMap<UUID, String> data) {
            HashMap<String, Integer> result = new HashMap<>();
            ChatChannelManager manager = instance.getChatChannelsManager();
            List<String> existChannels = manager.getChannelNames(sub);
            for (String c: existChannels){
                result.put(c,0);
            }
            for(Map.Entry<UUID, String> entry: data.entrySet()){
                if(result.containsKey(entry.getValue())){
                    Integer current = result.get(entry.getValue());
                    current++;
                    result.put(entry.getValue(),current);
                }
            }
            List<String> message = new ArrayList<>();
            if(!sub){
                message.add(ChatColor.translateAlternateColorCodes('&',"&6***List of Channels**"));
            }else {
                message.add(ChatColor.translateAlternateColorCodes('&',"&6***List of Subscribable Channels**"));
            }
            message.add(ChatColor.translateAlternateColorCodes('&',"&6-Channel Name-:-Total Subscribed-&f"));
            for(Map.Entry<String, Integer> res: result.entrySet()){
                message.add(ChatColor.translateAlternateColorCodes('&',res.getKey() + "&6 : &f " + res.getValue()));
            }
            message.add(ChatColor.translateAlternateColorCodes('&',"&6-----------------------&f"));
            String[] messages = new String[message.size()];
            message.toArray(messages);
            sender.sendMessage(messages);
        }

        @Override
        public void onError(String type, String message) {
            ChatChannelManager manager = instance.getChatChannelsManager();
            List<String> failure = new ArrayList<>();
            sender.sendMessage("Error: " + type +" : " + message);
            if(!sub){
                failure.add(ChatColor.translateAlternateColorCodes('&',"&6***List of Channels**"));
            }else {
                failure.add(ChatColor.translateAlternateColorCodes('&',"&6***List of Subscribable Channels**"));
            }
            failure.add(ChatColor.translateAlternateColorCodes('&',"-----------------------"));
            failure.addAll(manager.getChannelNames(sub));
            failure.add(ChatColor.translateAlternateColorCodes('&',"&6-----------------------&f"));
            String[] messages = new String[failure.size()];
            failure.toArray(messages);
            sender.sendMessage(messages);
        }
    }
}
