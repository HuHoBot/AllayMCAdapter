package cn.huohuas001.huHoBot.NetEvent;

import cn.huohuas001.huHoBot.HuHoBot;
import cn.huohuas001.huHoBot.Settings.PluginConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SendConfig extends EventRunner {
    private HuHoBot plugin = HuHoBot.getPlugin();

    @Override
    public boolean run() {
        PluginConfig config = this.getConfig();
        config.setServerId(body.getString("serverId"));
        config.setHashKey(body.getString("hashKey"));
        config.save();
        log.info("�����ļ��ѽ���.");
        log.info("�Զ��Ͽ�������ˢ�������ļ�...");
        HuHoBot.getClientManager().shutdownClient();
        return true;
    }
}
