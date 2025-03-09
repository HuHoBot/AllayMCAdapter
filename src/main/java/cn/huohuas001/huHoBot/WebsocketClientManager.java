package cn.huohuas001.huHoBot;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.allaymc.api.scheduler.Scheduler;
import org.allaymc.api.server.Server;

import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
public class WebsocketClientManager {
    private final int RECONNECT_DELAY = 5; // 重连延迟时间，单位为秒
    private final int MAX_RECONNECT_ATTEMPTS = 5; // 最大重连尝试次数
    private int ReconnectAttempts = 0;
    private boolean shouldReconnect = true; // 控制是否重连的变量
    private static WsClient client; //Websocket客户端
    private static final String websocketUrl = "ws://119.91.100.129:8888"; //Websocket地址
    //private static String websocketUrl = "ws://127.0.0.1:8888"; //Websocket地址
    private final HuHoBot plugin;
    private final Scheduler scheduler = Server.getInstance().getScheduler();
    private int currentTask = 0;
    private int autoDisConnectTask = 0;


    public WebsocketClientManager() {
        plugin = HuHoBot.getPlugin();
    }

    /**
     * 设置是否应该重连
     *
     * @param shouldReconnect 是否应该重连
     */
    public void setShouldReconnect(boolean shouldReconnect) {
        this.shouldReconnect  = shouldReconnect;
    }

    /**
     * 客户端自动重连循环
     */
    private boolean autoReconnect() {
        synchronized (this){
            if(currentTask == 0){
                return false;
            }
            ReconnectAttempts++;
            if (ReconnectAttempts >= MAX_RECONNECT_ATTEMPTS) {
                log.warn(" 重连尝试已达到最大次数，将不再尝试重新连接。");
                cancelCurrentTask();
                return false;
            }
            if(!shouldReconnect){
                cancelCurrentTask();
                return false;
            }
            log.info(" 正在尝试重新连接,这是第({}/" + MAX_RECONNECT_ATTEMPTS + ")次连接", ReconnectAttempts);
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
        log.info("连接超时，已自动重连");
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
     * 连接HuHoBot服务器
     */
    public boolean connectServer() {
        log.info(" 正在连接服务端...");
        try {
            URI uri = new URI(websocketUrl);
            if (client == null || !client.isOpen())  {
                client = new WsClient(uri, this);
                setShouldReconnect(true); // 设置是否重连
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
