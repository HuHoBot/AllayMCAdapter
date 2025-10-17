package cn.huohuas001.huhobot.websocket.handler;

import cn.huohuas001.huhobot.HuHoBot;
import org.allaymc.api.server.Server;
import org.allaymc.api.utils.TextFormat;
import org.slf4j.Logger;

public class Shaked extends RequestHandler {

    private static final Logger log = HuHoBot.getInstance().getPluginLogger();

    private void shakedProcess() {
        HuHoBot.getClientManager().setShouldReconnect(true);
        HuHoBot.getClientManager().cancelCurrentTask();
        HuHoBot.getClientManager().setAutoDisConnectTask();
        Server.getInstance().getScheduler().scheduleRepeating(HuHoBot.getInstance(), () -> {
            HuHoBot.getClientManager().sendHeart();
            return true;
        }, 5 * 20);
    }

    @Override
    public boolean run() {
        int code = body.getInteger("code");
        String msg = body.getString("msg");
        switch (code) {
            case 1:
                log.info("与服务端握手成功.");
                shakedProcess();
                break;
            case 2:
                log.info("握手完成!,附加消息:{}", msg);
                shakedProcess();
                break;
            case 3:
                log.error("{}握手失败，客户端密钥错误.", TextFormat.RED);
                HuHoBot.getClientManager().setShouldReconnect(false);
                break;
            case 6:
                log.info("与服务端握手成功，服务端等待绑定...");
                shakedProcess();
                HuHoBot.getInstance().sendBindMessage();
                break;
            default:
                log.error("{}握手失败，原因{}", TextFormat.RED, msg);
        }
        return true;
    }
}
