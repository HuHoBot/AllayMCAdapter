package cn.huohuas001.huHoBot.NetEvent;

import cn.huohuas001.huHoBot.Settings.PluginConfig;
import org.allaymc.api.server.Server;

public class AddAllowList extends EventRunner {
    @Override
    public boolean run() {
        String XboxId = body.getString("xboxid");
        if(Server.getInstance().getPlayerService().addToWhitelist(XboxId)){
            PluginConfig config = this.getConfig();
            String name = config.getServerName();
            respone(name + "已接受添加名为" + XboxId + "的白名单请求", "success");
        }

        return true;
    }
}
