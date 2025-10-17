package cn.huohuas001.huhobot.websocket.handler;

import cn.huohuas001.huhobot.HuHoBot;
import cn.huohuas001.huhobot.HuHoBotConfig;
import org.allaymc.api.server.Server;

public class Chat extends RequestHandler {
    @Override
    public boolean run() {
        String nick = body.getString("nick");
        String msg = body.getString("msg");
        HuHoBotConfig config = HuHoBot.getConfig();
        String message = config.getChatConfig().getFromGroup().replace("{nick}", nick).replace("{msg}", msg);
        if (config.getChatConfig().isPostChat()) {
            Server.getInstance().getMessageChannel().broadcastMessage(message);
        }
        return true;
    }
}
