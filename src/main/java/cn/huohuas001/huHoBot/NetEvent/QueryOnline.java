package cn.huohuas001.huHoBot.NetEvent;

import com.alibaba.fastjson2.JSONObject;
import org.allaymc.api.entity.interfaces.EntityPlayer;
import org.allaymc.api.server.Server;

import java.util.Map;
import java.util.UUID;

public class QueryOnline extends EventRunner {
    @Override
    public boolean run() {
        Map<UUID, EntityPlayer> onlineList = Server.getInstance().getOnlinePlayers();
        StringBuilder onlineNameString = new StringBuilder(); // 使用StringBuilder来累积玩家名称
        for (EntityPlayer pl : onlineList.values()) {
            String playerName = pl.getDisplayName();
            onlineNameString.append(playerName).append("\n");
        }
        onlineNameString.append("共").append(onlineList.size()).append("人在线");
        JSONObject list = new JSONObject();
        list.put("msg", onlineNameString);
        list.put("url", getConfig().getMotdUrl());
        list.put("serverType", "bedrock");
        JSONObject rBody = new JSONObject();
        rBody.put("list", list);
        sendMessage("queryOnline", rBody);
        return true;
    }
}
