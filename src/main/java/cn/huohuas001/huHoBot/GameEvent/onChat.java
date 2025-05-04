package cn.huohuas001.huHoBot.GameEvent;

import cn.huohuas001.huHoBot.HuHoBot;
import cn.huohuas001.huHoBot.Settings.PluginConfig;
import com.alibaba.fastjson2.JSONObject;
import org.allaymc.api.eventbus.EventHandler;
import org.allaymc.api.eventbus.event.player.PlayerChatEvent;

public class onChat {
    @EventHandler
    private void onChatEvent(PlayerChatEvent event){
        String message = event.getMessage();
        String playerName = event.getPlayer().getDisplayName();

        PluginConfig config = HuHoBot.getConfig();

        String format = config.getChatConfig().getFromGame();
        String prefix = config.getChatConfig().getPostPrefix();
        boolean isPostChat = config.getChatConfig().isPostChat();
        String serverId = config.getServerId();
        if (message.startsWith(prefix) && isPostChat) {
            JSONObject body = new JSONObject();
            body.put("serverId", serverId);
            String formated = format.replace("{name}", playerName).replace("{msg}", message.substring(prefix.length()));
            body.put("msg", formated);
            HuHoBot.getClientManager().getClient().sendMessage("chat", body);
        }
    }
}
