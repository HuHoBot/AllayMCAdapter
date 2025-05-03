package cn.huohuas001.huHoBot.NetEvent;

import cn.huohuas001.huHoBot.HuHoBot;
import cn.huohuas001.huHoBot.Settings.PluginConfig;
import cn.huohuas001.huHoBot.WsClient;
import com.alibaba.fastjson2.JSONObject;

public class EventRunner {
    String packId;
    JSONObject body;

    void respone(String msg, String type) {
        WsClient client = HuHoBot.getClientManager().getClient();
        client.respone(msg, type, packId);
    }

    void sendMessage(String type, JSONObject body) {
        WsClient client = HuHoBot.getClientManager().getClient();
        client.sendMessage(type, body, packId);
    }

    PluginConfig getConfig() {
        return HuHoBot.getConfig();
    }

    void runCommand(String command) {
        HuHoBot.getPlugin().runCommand(command, packId);
    }

    public boolean EventCall(String packId, JSONObject body) {
        this.packId = packId;
        this.body = body;
        return run();
    }

    boolean run() {
        return true;
    }

    ;
}
