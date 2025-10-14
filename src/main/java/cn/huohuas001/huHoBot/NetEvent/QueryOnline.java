package cn.huohuas001.huHoBot.NetEvent;

import cn.huohuas001.huHoBot.HuHoBot;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.allaymc.api.entity.interfaces.EntityPlayer;
import org.allaymc.api.server.Server;

import java.util.Map;
import java.util.UUID;

@Slf4j
public class QueryOnline extends EventRunner {
    @Override
    public boolean run() {
        Map<UUID, EntityPlayer> onlinePlayers = Server.getInstance().getPlayerManager().getPlayers();

        //获取motd Config
        String server_ip = getConfig().getMotd().getServerIp();
        int server_port = getConfig().getMotd().getServerPort();
        String api = getConfig().getMotd().getApi();
        String text = getConfig().getMotd().getText();
        boolean output_online_list = getConfig().getMotd().isOutputOnlineList();
        boolean post_img = getConfig().getMotd().isPostImg();

        StringBuilder onlineNameString = new StringBuilder();

        int onlineSize = onlinePlayers.size();
        if (output_online_list && !onlinePlayers.isEmpty()) {
            onlinePlayers.values().forEach(player -> {
                onlineNameString.append(player.getNameTag()).append("\n");
            });
        } else if (output_online_list) {
            onlineNameString.append("\n当前没有在线玩家\n");
        }

        onlineNameString.append(text.replace("{online}", String.valueOf(onlineSize)));

        // 构造JSON对象
        JSONObject list = new JSONObject();
        list.put("msg", onlineNameString);
        list.put("url", server_ip + ":" + server_port);
        list.put("imgUrl", api.replace("{server_ip}", server_ip).replace("{server_port}", String.valueOf(server_port)));
        list.put("post_img", post_img);
        list.put("serverType", "bedrock");
        JSONObject rBody = new JSONObject();
        rBody.put("list", list);

        sendMessage("queryOnline", rBody);
        return true;
    }
}
