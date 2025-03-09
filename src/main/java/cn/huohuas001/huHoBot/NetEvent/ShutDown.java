package cn.huohuas001.huHoBot.NetEvent;

import cn.huohuas001.huHoBot.HuHoBot;
import lombok.extern.slf4j.Slf4j;
import java.util.logging.Logger;

@Slf4j
public class ShutDown extends EventRunner {
    private HuHoBot plugin = HuHoBot.getPlugin();

    @Override
    public boolean run() {
        log.error("���������Ͽ����� ԭ��:{}", body.getString("msg"));
        log.error("�˴�����в����ݴ���!�����������ļ�!");
        log.warn("���ڶϿ�����...");
        plugin.getClientManager().setShouldReconnect(false);
        plugin.getClientManager().shutdownClient();
        return true;
    }
}