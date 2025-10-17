package cn.huohuas001.huhobot.websocket.handler;

import cn.huohuas001.huhobot.HuHoBot;
import cn.huohuas001.huhobot.websocket.WSClient;
import com.alibaba.fastjson2.JSONObject;

public class RequestHandler {

    protected String id;
    protected JSONObject body;

    public boolean handle(String id, JSONObject body) {
        this.id = id;
        this.body = body;
        return run();
    }

    protected boolean run() {
        return true;
    }

    protected void response(String msg, String type) {
        WSClient client = HuHoBot.getClientManager().getClient();
        client.response(msg, type, id);
    }

    protected void sendMessage(String type, JSONObject body) {
        WSClient client = HuHoBot.getClientManager().getClient();
        client.sendMessage(type, body, id);
    }

    protected void runCommand(String command) {
        HuHoBot.getInstance().runCommand(command, id);
    }
}
