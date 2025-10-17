package cn.huohuas001.huhobot.websocket.handler;

import cn.huohuas001.huhobot.HuHoBot;
import org.slf4j.Logger;

public class ShutDown extends RequestHandler {

    private static final Logger log = HuHoBot.getInstance().getPluginLogger();

    @Override
    public boolean run() {
        log.error("服务端命令断开连接 原因:{}", body.getString("msg"));
        log.error("此错误具有不可容错性!请检查插件配置文件!");
        log.warn("正在断开连接...");
        HuHoBot.getClientManager().setShouldReconnect(false);
        HuHoBot.getClientManager().shutdownClient();
        return true;
    }
}