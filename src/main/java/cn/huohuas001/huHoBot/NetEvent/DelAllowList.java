package cn.huohuas001.huHoBot.NetEvent;

import cn.huohuas001.huHoBot.HuHoBot;
import cn.huohuas001.huHoBot.Settings.PluginConfig;
import org.allaymc.api.server.Server;

public class DelAllowList extends EventRunner {
    @Override
    public boolean run() {
        String XboxId = body.getString("xboxid");
        if(Server.getInstance().removeFromWhitelist(XboxId)){
            PluginConfig config = this.getConfig();
            String name = config.getServerName();
            respone(name + "�ѽ���ɾ����Ϊ" + XboxId + "�İ���������", "success");
        }
        return true;
    }
}
