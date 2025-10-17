package cn.huohuas001.huhobot.websocket.handler;

import cn.huohuas001.huhobot.HuHoBot;
import com.alibaba.fastjson2.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BindRequest extends RequestHandler {
    private final Map<String, String> bindMap = new HashMap<>();

    @Override
    public boolean run() {
        String bindCode = body.getString("bindCode");
        HuHoBot.getInstance().getPluginLogger().info("收到一个新的绑定请求，如确认绑定，请输入\"/huhobot bind {}\"来进行确认", bindCode);
        bindMap.put(bindCode, id);
        return true;
    }

    public boolean confirmBind(String bindCode) {
        if (bindMap.containsKey(bindCode)) {
            sendMessage("bindConfirm", new JSONObject());
            bindMap.remove(bindCode);
            return true;
        }
        return false;
    }
}
