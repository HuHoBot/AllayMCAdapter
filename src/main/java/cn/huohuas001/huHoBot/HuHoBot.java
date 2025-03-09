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
    private static WebsocketClientManager clientManager; //Websocket�ͻ���
    private Map<String, EventRunner> eventList = new HashMap<>(); //�¼��б�

    public bindRequest bindRequestObj;

    @Override
    public void onLoad() {
        instance = this;

        // �������ù�����
        config = ConfigManager.create(
                PluginConfig.class,
                Utils.createConfigInitializer(Path.of(CONFIG_FILE_NAME)
                )
        );

        // ��ʼ��Ĭ��ֵ
        config.initializeDefaults();
        config.save();

        //ע������
        Registries.COMMANDS.register(new HuHoBotCommand());

        //ע���¼�
        totalRegEvent();

        //����
        clientManager = new WebsocketClientManager();
        clientManager.connectServer();

        log.info("HuHoBot Loaded. By HuoHuas001");
    }

    /**
     * ע��Websocket�¼�
     *
     * @param eventName �¼�����
     * @param event     �¼�����
     */
    private void registerEvent(String eventName, EventRunner event) {
        eventList.put(eventName, event);
    }


    /**
     * ͳһ�¼�ע��
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
            log.error("�ڴ�����Ϣ����������: δ֪����Ϣ����{}", type);
            log.error("�˴�����в����ݴ���!�������Ƿ�Ϊ����!");
            log.info("���ڶϿ�����...");
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
        String resultTextBuilder = "�ݲ�֧�ַ���ֵ.";
        if(result.isSuccess()){
            clientManager.getClient().respone("��ִ��,����ص�����:\n" + resultTextBuilder, "success", packId);
        }else{
            clientManager.getClient().respone("��ִ��,����ص�����:\n" + resultTextBuilder, "error", packId);
        }

    }

    /**
     * �����Ƿ��Ѿ����󶨳ɹ�
     *
     * @return �Ƿ�󶨳ɹ�
     */
    public boolean isBind() {
        return config.getHashKey() != null;
    }

    /**
     * �ڿ���̨�����ID
     */
    public void sendBindMessage() {
        if (!isBind()) {
            String serverId = config.getServerId();
            String message = "��������δ�ڻ����˽��а󶨣�����Ⱥ������\"/�� " + serverId + "\"";
            log.warn(message);
        }

    }

    /**
     * ����HuHoBot������
     *
     * @return �Ƿ����ӳɹ�
     */
    public boolean reconnect() {
        if (clientManager.isOpen()) {
            return false;
        }
        clientManager.connectServer();
        return true;
    }

    /**
     * ����HuHoBot������
     *
     * @return �Ƿ�����ɹ�
     */
    public boolean disConnectServer() {
        clientManager.setShouldReconnect(false);
        return clientManager.shutdownClient();
    }
}