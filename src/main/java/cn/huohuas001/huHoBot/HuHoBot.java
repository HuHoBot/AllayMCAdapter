package cn.huohuas001.huhobot;

import cn.huohuas001.huhobot.command.HuHoBotCommand;
import cn.huohuas001.huhobot.utils.HuHoBotCommandSender;
import cn.huohuas001.huhobot.websocket.WSClientManager;
import cn.huohuas001.huhobot.websocket.handler.*;
import com.alibaba.fastjson2.JSONObject;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.yaml.snakeyaml.YamlSnakeYamlConfigurer;
import lombok.Getter;
import org.allaymc.api.eventbus.EventHandler;
import org.allaymc.api.eventbus.event.player.PlayerChatEvent;
import org.allaymc.api.plugin.Plugin;
import org.allaymc.api.registry.Registries;
import org.allaymc.api.server.Server;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class HuHoBot extends Plugin {

    private static final String CONFIG_FILE_NAME = "plugins/HuHoBot/config.yml";

    @Getter
    private static HuHoBot instance;
    @Getter
    private static HuHoBotConfig config;
    @Getter
    private static WSClientManager clientManager; //Websocket客户端

    private final Map<String, RequestHandler> requestHandlers; //事件处理器列表

    @Getter
    private BindRequest bindRequest;

    public HuHoBot() {
        this.requestHandlers = new HashMap<>();
    }

    public static void reloadConfig() {
        config.load();
        config.save();
    }

    @Override
    public void onLoad() {
        instance = this;

        // 创建配置管理器
        config = ConfigManager.create(
                HuHoBotConfig.class,
                it -> {
                    // Specify configurer implementation, optionally additional serdes packages
                    it.withConfigurer(new YamlSnakeYamlConfigurer());
                    // Specify Path, File or pathname
                    it.withBindFile(Path.of(CONFIG_FILE_NAME));
                    // Automatic removal of undeclared keys
                    it.withRemoveOrphans(true);
                    // Save the file if it does not exist
                    it.saveDefaults();
                    // Load and save to update comments/new fields
                    it.load(true);
                }
        );

        //注册命令
        Registries.COMMANDS.register(new HuHoBotCommand());

        //注册事件
        registerRequestHandlers();

        pluginLogger.info("HuHoBot Loaded. By HuoHuas001");
    }

    @Override
    public void onEnable() {
        //连接
        clientManager = new WSClientManager();
        clientManager.connectServer();

        Server.getInstance().getEventBus().registerListener(this);
    }

    /**
     * 注册Websocket请求处理器
     *
     * @param requestName 请求名称
     * @param handler     请求处理器
     */
    private void registerRequestHandler(String requestName, RequestHandler handler) {
        requestHandlers.put(requestName, handler);
    }

    /**
     * 统一请求处理器注册
     */
    private void registerRequestHandlers() {
        registerRequestHandler("sendConfig", new SendConfig());
        registerRequestHandler("shaked", new Shaked());
        registerRequestHandler("chat", new Chat());
        registerRequestHandler("add", new EditAllowList(true));
        registerRequestHandler("delete", new EditAllowList(false));
        registerRequestHandler("cmd", new RunCommand());
        registerRequestHandler("queryList", new QueryAllowList());
        registerRequestHandler("queryOnline", new QueryOnline());
        registerRequestHandler("shutdown", new ShutDown());
        registerRequestHandler("run", new CustomRun(false));
        registerRequestHandler("runAdmin", new CustomRun(true));
        registerRequestHandler("heart", new Heart());
        bindRequest = new BindRequest();
        registerRequestHandler("bindRequest", bindRequest);
    }

    public void onWsMsg(JSONObject data) {
        JSONObject header = data.getJSONObject("header");
        JSONObject body = data.getJSONObject("body");

        String type = header.getString("type");
        String id = header.getString("id");

        RequestHandler handler = requestHandlers.get(type);
        if (handler != null) {
            handler.handle(id, body);
        } else {
            pluginLogger.error("在处理消息是遇到错误: 未知的消息类型{}", type);
            pluginLogger.error("此错误具有不可容错性!请检查插件是否为最新!");
            pluginLogger.info("正在断开连接...");
            clientManager.shutdownClient();
        }
    }

    public void runCommand(String command, String requestId) {
        var sender = new HuHoBotCommandSender();
        var result = Registries.COMMANDS.execute(sender, command);
        var message = sender.getOutputs().toString();
        if (result.isSuccess()) {
            clientManager.getClient().response("已执行,命令回调如下:\n" + message, "success", requestId);
        } else {
            clientManager.getClient().response("已执行,命令回调如下:\n" + message, "error", requestId);
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
            pluginLogger.warn("服务器尚未在机器人进行绑定，请在群内输入\"/绑定 " + config.getServerId() + "\"");
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

    @EventHandler
    private void onPlayerChat(PlayerChatEvent event) {
        String message = event.getMessage();
        String playerName = event.getPlayer().getDisplayName();

        HuHoBotConfig config = HuHoBot.getConfig();

        String format = config.getChatConfig().getFromGame();
        String prefix = config.getChatConfig().getPostPrefix();
        boolean isPostChat = config.getChatConfig().isPostChat();
        String serverId = config.getServerId();
        if (message.startsWith(prefix) && isPostChat) {
            JSONObject body = new JSONObject();
            body.put("serverId", serverId);
            String formated = format.replace("{name}", playerName).replace("{msg}", message.substring(prefix.length()));
            body.put("msg", formated);
            HuHoBot.getClientManager().getClient().sendMessage("chat", body);
        }
    }
}