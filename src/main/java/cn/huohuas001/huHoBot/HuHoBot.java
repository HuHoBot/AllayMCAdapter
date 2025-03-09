package cn.huohuas001.huHoBot;

import cn.huohuas001.huHoBot.Command.HuHoBotCommand;
import cn.huohuas001.huHoBot.NetEvent.*;
import cn.huohuas001.huHoBot.Settings.PluginConfig;
import cn.huohuas001.huHoBot.Tools.ConsoleSender;
import com.alibaba.fastjson2.JSONObject;
import eu.okaeri.configs.ConfigManager;
import lombok.extern.slf4j.Slf4j;
import org.allaymc.api.command.CommandResult;
import org.allaymc.api.command.CommandSender;
import org.allaymc.api.i18n.I18n;
import org.allaymc.api.plugin.Plugin;
import org.allaymc.api.registry.Registries;
import org.allaymc.api.server.Server;
import org.allaymc.api.utils.Utils;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class HuHoBot extends Plugin {
    private static HuHoBot instance;
    private static final String CONFIG_FILE_NAME = "plugins/HuHoBot/config.yml";
    private static PluginConfig config;
    private static WebsocketClientManager clientManager; //Websocket客户端
    private Map<String, EventRunner> eventList = new HashMap<>(); //事件列表

    public bindRequest bindRequestObj;

    @Override
    public void onLoad() {
        instance = this;

        // 创建配置管理器
        config = ConfigManager.create(
                PluginConfig.class,
                Utils.createConfigInitializer(Path.of(CONFIG_FILE_NAME)
                )
        );

        // 初始化默认值
        config.initializeDefaults();
        config.save();

        //注册命令
        Registries.COMMANDS.register(new HuHoBotCommand());

        //注册事件
        totalRegEvent();

        //连接
        clientManager = new WebsocketClientManager();
        clientManager.connectServer();

        log.info("HuHoBot Loaded. By HuoHuas001");
    }

    /**
     * 注册Websocket事件
     *
     * @param eventName 事件名称
     * @param event     事件对象
     */
    private void registerEvent(String eventName, EventRunner event) {
        eventList.put(eventName, event);
    }


    /**
     * 统一事件注册
     */
    private void totalRegEvent() {
        registerEvent("sendConfig", new SendConfig());
        registerEvent("shaked", new Shaked());
        registerEvent("chat", new Chat());
        registerEvent("add", new AddAllowList());
        registerEvent("delete", new DelAllowList());
        registerEvent("cmd", new RunCommand());
        registerEvent("queryList", new QueryAllowList());
        registerEvent("queryOnline", new QueryOnline());
        registerEvent("shutdown", new ShutDown());
        registerEvent("run", new CustomRun());
        registerEvent("runAdmin", new CustomRunAdmin());
        registerEvent("heart", new Heart());
        bindRequestObj = new bindRequest();
        registerEvent("bindRequest", bindRequestObj);
    }

    public void onWsMsg(JSONObject data){
        JSONObject header = data.getJSONObject("header");
        JSONObject body = data.getJSONObject("body");

        String type = header.getString("type");
        String packId = header.getString("id");

        EventRunner event = eventList.get(type);
        if (event != null) {
            event.EventCall(packId, body);
        }else{
            log.error("在处理消息是遇到错误: 未知的消息类型{}", type);
            log.error("此错误具有不可容错性!请检查插件是否为最新!");
            log.info("正在断开连接...");
            clientManager.shutdownClient();
        }
    }

    public static HuHoBot getPlugin(){
        return instance;
    }

    public static PluginConfig getConfig(){
        return config;
    }

    public static WebsocketClientManager getClientManager() {
        return clientManager;
    }

    public void runCommand(String command, String packId) {
        CommandSender orginalSender = Server.getInstance();
        CommandResult result = Registries.COMMANDS.execute(orginalSender, command);
        String resultTextBuilder = "暂不支持返回值.";
        if(result.isSuccess()){
            clientManager.getClient().respone("已执行,命令回调如下:\n" + resultTextBuilder, "success", packId);
        }else{
            clientManager.getClient().respone("已执行,命令回调如下:\n" + resultTextBuilder, "error", packId);
        }

    }

    /**
     * 返回是否已经被绑定成功
     *
     * @return 是否绑定成功
     */
    public boolean isBind() {
        return config.getHashKey() != null;
    }

    /**
     * 在控制台输出绑定ID
     */
    public void sendBindMessage() {
        if (!isBind()) {
            String serverId = config.getServerId();
            String message = "服务器尚未在机器人进行绑定，请在群内输入\"/绑定 " + serverId + "\"";
            log.warn(message);
        }

    }

    /**
     * 重连HuHoBot服务器
     *
     * @return 是否连接成功
     */
    public boolean reconnect() {
        if (clientManager.isOpen()) {
            return false;
        }
        clientManager.connectServer();
        return true;
    }

    /**
     * 断连HuHoBot服务器
     *
     * @return 是否断连成功
     */
    public boolean disConnectServer() {
        clientManager.setShouldReconnect(false);
        return clientManager.shutdownClient();
    }
}