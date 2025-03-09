package cn.huohuas001.huHoBot.NetEvent;

import cn.huohuas001.huHoBot.HuHoBot;
import lombok.extern.slf4j.Slf4j;
import org.allaymc.api.server.Server;

@Slf4j
public class Shaked extends EventRunner {
    private HuHoBot plugin = HuHoBot.getPlugin();

    private void shakedProcess(){
        plugin.getClientManager().setShouldReconnect(true);
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
                log.info("���������ֳɹ�.");
                shakedProcess();
                break;
            case 2:
                log.info("�������!,������Ϣ:{}", msg);
                shakedProcess();
                break;
            case 3:
                log.error("����ʧ�ܣ��ͻ�����Կ����.");
                plugin.getClientManager().setShouldReconnect(false);
                break;
            case 6:
                log.info("���������ֳɹ�������˵ȴ���...");
                shakedProcess();
                break;
            default:
                log.error("����ʧ�ܣ�ԭ��{}", msg);
        }
        return true;
    }
}
