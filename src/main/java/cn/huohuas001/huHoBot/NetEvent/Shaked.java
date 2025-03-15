package cn.huohuas001.huHoBot.NetEvent;

import cn.huohuas001.huHoBot.HuHoBot;
import lombok.extern.slf4j.Slf4j;
import org.allaymc.api.server.Server;

@Slf4j
public class Shaked extends EventRunner {
    private final HuHoBot plugin = HuHoBot.getPlugin();

    private void shakedProcess(){
        HuHoBot.getClientManager().setShouldReconnect(true);
        HuHoBot.getClientManager().cancelCurrentTask();
        HuHoBot.getClientManager().setAutoDisConnectTask();
        Server.getInstance().getScheduler().scheduleRepeating(plugin, ()->{
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
                log.error("握手失败，客户端密钥错误.");
                HuHoBot.getClientManager().setShouldReconnect(false);
                break;
            case 6:
                log.info("与服务端握手成功，服务端等待绑定...");
                shakedProcess();
                break;
            default:
                log.error("握手失败，原因{}", msg);
        }
        return true;
    }
}
