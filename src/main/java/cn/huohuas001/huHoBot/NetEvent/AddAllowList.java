package cn.huohuas001.huHoBot.NetEvent;

import cn.huohuas001.huHoBot.Settings.PluginConfig;
import org.allaymc.api.server.Server;

public class AddAllowList extends EventRunner {
    @Override
    public boolean run() {
        String XboxId = body.getString("xboxid");
        if(Server.getInstance().addToWhitelist(XboxId)){
            PluginConfig config = this.getConfig();
            String name = config.getServerName();
            respone(name + "�ѽ��������Ϊ" + XboxId + "�İ���������", "success");
        }

        return true;
    }
}
