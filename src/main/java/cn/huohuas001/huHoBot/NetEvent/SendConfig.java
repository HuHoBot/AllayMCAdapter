package cn.huohuas001.huHoBot.NetEvent;

import cn.huohuas001.huHoBot.HuHoBot;
import cn.huohuas001.huHoBot.Settings.PluginConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SendConfig extends EventRunner {
    private final HuHoBot plugin = HuHoBot.getPlugin();

    @Override
    public boolean run() {
        PluginConfig config = this.getConfig();
        config.setServerId(body.getString("serverId"));
        config.setHashKey(body.getString("hashKey"));
        config.save();
        log.info("配置文件已接受.");
        log.info("自动断开连接以刷新配置文件...");
        HuHoBot.getClientManager().shutdownClient();
        return true;
    }
}
