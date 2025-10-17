package cn.huohuas001.huhobot.websocket.handler;


import cn.huohuas001.huhobot.HuHoBot;
import cn.huohuas001.huhobot.event.BotCustomCommandEvent;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class CustomRun extends RequestHandler {

    private final boolean runByAdmin;

    @Override
    public boolean run() {
        String keyWord = body.getString("key");
        List<String> param = body.getList("runParams", String.class);

        var commandMap = HuHoBot.getConfig().getCustomCommandMap();
        // 测试查找功能
        var result = commandMap.get(keyWord);
        if (result == null) {
            var event = new BotCustomCommandEvent(keyWord, body, id, false);
            event.call();
            if (!event.isCancelled()) {
                response("无效的关键字", "error");
            }

            return true;
        }

        String command = result.getCommand();
        for (int i = 0; i < param.size(); i++) {
            int replaceNum = i + 1;
            command = command.replace("&" + replaceNum, param.get(i));
        }
        if (!runByAdmin && result.getPermission() > 0) {
            response("权限不足，若您为管理员，请使用/管理员执行", "error");
            return false;
        }

        runCommand(command);
        return true;
    }
}
