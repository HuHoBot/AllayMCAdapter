package cn.huohuas001.huHoBot.NetEvent;


import cn.huohuas001.huHoBot.Api.BotCustomCommand;
import cn.huohuas001.huHoBot.HuHoBot;
import cn.huohuas001.huHoBot.Settings.PluginConfig;


import java.util.List;
import java.util.Map;

public class CustomRun extends EventRunner {
    private final HuHoBot plugin = HuHoBot.getPlugin();

    @Override public boolean run() {
        String keyWord = body.getString("key");
        List<String> param = body.getList("runParams", String.class);

        Map<String, PluginConfig.CustomCommand> commandMap = getConfig().getCustomCommandMap();
        // 测试查找功能
        PluginConfig.CustomCommand result = commandMap.get(keyWord);
        if (result == null) {
            BotCustomCommand event = new BotCustomCommand(keyWord, body, packId, false);
            event.call();

            if(!event.isCancelled()){
                respone("无效的关键字", "error");
            }
            return true;
        }

        String command = result.getCommand();
        for (int i = 0; i < param.size(); i++) {
            int replaceNum = i + 1;
            command = command.replace("&" + String.valueOf(replaceNum), param.get(i));
        }
        if(result.getPermission() > 0){
            respone("权限不足，若您为管理员，请使用/管理员执行", "error");
            return false;
        }
        runCommand(command);
        return true;
    }
}
