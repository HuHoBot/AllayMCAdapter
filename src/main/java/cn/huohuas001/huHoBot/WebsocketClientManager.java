package cn.huohuas001.huHoBot;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.allaymc.api.scheduler.Scheduler;
import org.allaymc.api.server.Server;

import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
public class WebsocketClientManager {
    private final int RECONNECT_DELAY = 5; // �����ӳ�ʱ�䣬��λΪ��
    private final int MAX_RECONNECT_ATTEMPTS = 5; // ����������Դ���
    private int ReconnectAttempts = 0;
    private boolean shouldReconnect = true; // �����Ƿ������ı���
    private static WsClient client; //Websocket�ͻ���
    private static final String websocketUrl = "ws://119.91.100.129:8888"; //Websocket��ַ
    //private static String websocketUrl = "ws://127.0.0.1:8888"; //Websocket��ַ
    private final HuHoBot plugin;
    private final Scheduler scheduler = Server.getInstance().getScheduler();
    private int currentTask = 0;
    private int autoDisConnectTask = 0;


    public WebsocketClientManager() {
        plugin = HuHoBot.getPlugin();
    }

    /**
     * �����Ƿ�Ӧ������
     *
     * @param shouldReconnect �Ƿ�Ӧ������
     */
    public void setShouldReconnect(boolean shouldReconnect) {
        this.shouldReconnect  = shouldReconnect;
    }

    /**
     * �ͻ����Զ�����ѭ��
     */
    private boolean autoReconnect() {
        synchronized (this){
            if(currentTask == 0){
                return false;
            }
            ReconnectAttempts++;
            if (ReconnectAttempts >= MAX_RECONNECT_ATTEMPTS) {
                log.warn(" ���������Ѵﵽ�������������ٳ����������ӡ�");
                cancelCurrentTask();
                return false;
            }
            if(!shouldReconnect){
                cancelCurrentTask();
                return false;
            }
            log.info(" ���ڳ�����������,���ǵ�({}/" + MAX_RECONNECT_ATTEMPTS + ")������", ReconnectAttempts);
            this.connectServer();
            return true;
        }
    }

    public void cancelCurrentTask() {
        if (currentTask != 0) {
            currentTask = 0;
            ReconnectAttempts = 0;
        }
    }

    public WsClient getClient() {
        return client;
    }

    public boolean shutdownClient(){
        if (client != null && client.isOpen())  {
            client.close(1000);
            return true;
        }
        return false;
    }

    public void autoDisConnectClient(){
        log.info("���ӳ�ʱ�����Զ�����");
        shutdownClient();
    }

    public void setAutoDisConnectTask(){
        if(autoDisConnectTask == 0){
            scheduler.scheduleDelayed(plugin,()->{
                autoDisConnectClient();
                return true;
            }, 6*60*60*20);
            autoDisConnectTask = 1;
        }
    }

    /**
     * ����HuHoBot������
     */
    public boolean connectServer() {
        log.info(" �������ӷ����...");
        try {
            URI uri = new URI(websocketUrl);
            if (client == null || !client.isOpen())  {
                client = new WsClient(uri, this);
                setShouldReconnect(true); // �����Ƿ�����
                client.connect();
                plugin.sendBindMessage();
            }
            return true;
        } catch (URISyntaxException e) {
            log.error(e.getStackTrace().toString());
        }
        return false;
    }

    public boolean isOpen(){
        return client.isOpen();
    }

    public void sendHeart(){
        client.sendMessage("heart",  new JSONObject());
    }

    public void clientReconnect(){
        if (shouldReconnect && currentTask == 0) {
            scheduler.scheduleRepeating(plugin, this::autoReconnect, this.RECONNECT_DELAY * 20);
            currentTask = 1;
        }
    }
}
