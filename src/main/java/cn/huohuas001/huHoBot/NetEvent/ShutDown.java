package cn.huohuas001.huHoBot.NetEvent;

import cn.huohuas001.huHoBot.HuHoBot;
import lombok.extern.slf4j.Slf4j;
import java.util.logging.Logger;

@Slf4j
public class ShutDown extends EventRunner {
    private HuHoBot plugin = HuHoBot.getPlugin();

    @Override
    public boolean run() {
        log.error("服务端命令断开连接 原因:{}", body.getString("msg"));
        log.error("此错误具有不可容错性!请检查插件配置文件!");
        log.warn("正在断开连接...");
        plugin.getClientManager().setShouldReconnect(false);
        plugin.getClientManager().shutdownClient();
        return true;
    }
}