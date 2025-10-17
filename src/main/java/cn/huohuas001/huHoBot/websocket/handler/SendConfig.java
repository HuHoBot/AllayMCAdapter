package cn.huohuas001.huhobot.websocket.handler;

import cn.huohuas001.huhobot.HuHoBot;
import cn.huohuas001.huhobot.HuHoBotConfig;
import org.slf4j.Logger;

public class SendConfig extends RequestHandler {

    private static final Logger log = HuHoBot.getInstance().getPluginLogger();

    @Override
    public boolean run() {
        HuHoBotConfig config = HuHoBot.getConfig();
        config.setServerId(body.getString("serverId"));
        config.setHashKey(body.getString("hashKey"));
        config.save();
        log.info("配置文件已接受.");
        log.info("自动断开连接以刷新配置文件...");
        HuHoBot.getClientManager().shutdownClient();
        return true;
    }
}
