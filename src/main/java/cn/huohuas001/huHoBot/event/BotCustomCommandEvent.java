package cn.huohuas001.huhobot.event;

import cn.huohuas001.huhobot.HuHoBot;
import cn.huohuas001.huhobot.websocket.WSClient;
import com.alibaba.fastjson2.JSONObject;
import lombok.AccessLevel;
import lombok.Getter;
import org.allaymc.api.eventbus.event.CancellableEvent;
import org.allaymc.api.eventbus.event.Event;

import java.util.List;

@Getter
public class BotCustomCommandEvent extends Event implements CancellableEvent {

    private final String command;
    private final JSONObject data;
    private final List<String> param;
    @Getter(AccessLevel.NONE)
    private final String requestId;
    private final boolean runByAdmin;

    public BotCustomCommandEvent(String command, JSONObject data, String requestId, boolean runByAdmin) {
        this.command = command;
        this.data = data;
        this.param = data.getList("runParams", String.class);
        this.requestId = requestId;
        this.runByAdmin = runByAdmin;
    }

    public void response(String msg, String type) {
        WSClient client = HuHoBot.getClientManager().getClient();
        client.response(msg, type, requestId);
    }

    public void response(JSONObject msg, String type) {
        WSClient client = HuHoBot.getClientManager().getClient();
        client.response(msg.toJSONString(), type, requestId);
    }
}
