package cn.huohuas001.huHoBot.Api;
import cn.huohuas001.huHoBot.HuHoBot;
import cn.huohuas001.huHoBot.WsClient;
import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;
import org.allaymc.api.eventbus.event.CancellableEvent;
import org.allaymc.api.eventbus.event.Event;

import java.util.List;

public class BotCustomCommand extends Event implements CancellableEvent {
    @Getter
    private final String command;
    @Getter
    private final JSONObject data;
    @Getter
    private final List<String> param;
    private final String packId;
    @Getter
    private final boolean runByAdmin;
    private boolean cancelled = false;

    public BotCustomCommand(String command, JSONObject data, String packId, boolean runByAdmin) {
        this.command = command;
        this.data = data;
        this.param = data.getList("runParams", String.class);
        this.packId = packId;
        this.runByAdmin = runByAdmin;
    }

    public void respone(String msg, String type) {
        WsClient client = HuHoBot.getClientManager().getClient();
        client.respone(msg, type, packId);
    }

    public void respone(JSONObject msg, String type) {
        WsClient client = HuHoBot.getClientManager().getClient();
        client.respone(msg.toJSONString(), type, packId);
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }

    @Override
    public void cancel() {
        setCancelled(true);
    }
}
