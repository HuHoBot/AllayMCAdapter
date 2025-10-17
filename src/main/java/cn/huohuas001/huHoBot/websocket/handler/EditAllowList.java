package cn.huohuas001.huhobot.websocket.handler;

import cn.huohuas001.huhobot.HuHoBot;
import lombok.AllArgsConstructor;
import org.allaymc.api.server.Server;

@AllArgsConstructor
public class EditAllowList extends RequestHandler {

    private final boolean add;

    @Override
    public boolean run() {
        String xboxId = body.getString("xboxid");
        if (add) {
            if (Server.getInstance().getPlayerManager().addToWhitelist(xboxId)) {
                String name = HuHoBot.getConfig().getServerName();
                response(name + "已接受添加名为" + xboxId + "的白名单请求", "success");
            }
        } else {
            if (Server.getInstance().getPlayerManager().removeFromWhitelist(xboxId)) {
                String name = HuHoBot.getConfig().getServerName();
                response(name + "已接受删除名为" + xboxId + "的白名单请求", "success");
            }
        }
        return true;
    }
}
