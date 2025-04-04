package cn.huohuas001.huHoBot.NetEvent;

import cn.huohuas001.huHoBot.Settings.PluginConfig;
import org.allaymc.api.server.Server;

public class Chat extends EventRunner {
    @Override
    public boolean run() {
        String nick = body.getString("nick");
        String msg = body.getString("msg");
        PluginConfig config = this.getConfig();
        String message = config.getChatFormatGroup().replace("{nick}", nick).replace("{msg}", msg);
        Server.getInstance().broadcastText(message);
        return true;
    }
}
