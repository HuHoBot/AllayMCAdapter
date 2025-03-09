package cn.huohuas001.huHoBot;


import cn.huohuas001.huHoBot.Settings.PluginConfig;
import cn.huohuas001.huHoBot.Tools.PackId;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class WsClient extends WebSocketClient {
    private final Map<String, CompletableFuture<JSONObject>> responseFutureList = new HashMap<>();
    private HuHoBot plugin;
    private final WebsocketClientManager clientManager;


    public WsClient(URI serverUri,WebsocketClientManager clientManager) {
        super(serverUri);
        this.plugin = HuHoBot.getPlugin();

        this.clientManager = clientManager;
    }

    @Override
    public void onOpen(ServerHandshake _da) {
        log.info("服务端连接成功.");
        this.shakeHand();
    }

    @Override
    public void onMessage(String message) {
        //logger.info("Received: " + message);
        JSONObject jsonData = JSON.parseObject(message);
        JSONObject header = jsonData.getJSONObject("header");
        String packId = header.getString("id");

        if (responseFutureList.containsKey(packId)) {
            CompletableFuture<JSONObject> responseFuture = responseFutureList.get(packId);
            if (responseFuture != null && !responseFuture.isDone()) {
                responseFuture.complete(jsonData);
            }
            responseFutureList.remove(packId);
        } else {
            plugin.onWsMsg(jsonData);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.error("连接已断开,错误码:{} 错误信息:{}", code, reason);
        clientManager.clientReconnect();
    }

    @Override
    public void onError(Exception ex) {
        log.error("连接发生错误!错误信息:{}", ex.getMessage());
        clientManager.clientReconnect();
    }



    /**
     * 向服务端发送一条消息
     *
     * @param type 消息类型
     * @param body 消息数据
     */
    public void sendMessage(String type, JSONObject body) {
        String newPackId = PackId.getPackID();
        sendMessage(type, body, newPackId);
    }

    /**
     * 向服务端发送一条消息
     *
     * @param type   消息类型
     * @param body   消息数据
     * @param packId 消息Id
     */
    public void sendMessage(String type, JSONObject body, String packId) {
        JSONObject data = new JSONObject();
        JSONObject header = new JSONObject();
        header.put("type", type);
        header.put("id", packId);
        data.put("header", header);
        data.put("body", body);
        if (this.isOpen()) {
            this.send(data.toJSONString());
        }
    }

    /**
     * 向服务端发送一条消息并获取返回值
     *
     * @param type 消息类型
     * @param body 消息数据
     * @return 消息回报体
     */
    public CompletableFuture<JSONObject> sendRequestAndAwaitResponse(String type, JSONObject body) {
        String newPackId = PackId.getPackID();
        return sendRequestAndAwaitResponse(type, body, newPackId);
    }

    /**
     * 向服务端发送一条消息并获取返回值
     *
     * @param type   消息类型
     * @param body   消息数据
     * @param packId 消息Id
     * @return 消息回报体
     */
    public CompletableFuture<JSONObject> sendRequestAndAwaitResponse(String type, JSONObject body, String packId) {
        if (this.isOpen()) {
            //打包数据并发送
            JSONObject data = new JSONObject();
            JSONObject header = new JSONObject();
            header.put("type", type);
            header.put("id", packId);
            data.put("header", header);
            data.put("body", body);
            this.send(data.toJSONString());

            //存储回报
            CompletableFuture<JSONObject> responseFuture = new CompletableFuture<>();
            responseFutureList.put(packId, responseFuture);

            return responseFuture;
        } else {
            throw new IllegalStateException("WebSocket connection is not open.");
        }
    }

    /**
     * 向服务端发送一条回报
     *
     * @param msg  回报消息
     * @param type 回报类型：success|error
     */
    public void respone(String msg, String type) {
        String newPackId = PackId.getPackID();
        this.respone(msg, type, newPackId);
    }

    /**
     * 向服务端发送一条回报
     *
     * @param msg    回报消息
     * @param type   回报类型：success|error
     * @param packId 回报Id
     */
    public void respone(String msg, String type, String packId) {
        JSONObject body = new JSONObject();
        body.put("msg", msg);
        sendMessage(type, body, packId);
    }

    /**
     * 向服务端握手
     */
    private void shakeHand() {
        PluginConfig config = plugin.getConfig();
        JSONObject body = new JSONObject();
        body.put("serverId", config.getServerId());
        if (config.get("hashKey") == null) {
            body.put("hashKey", "");
        } else {
            body.put("hashKey", config.getHashKey());
        }
        body.put("name", config.getServerName());
        body.put("version", HuHoBot.PLUGIN_VERSION);
        body.put("platform", "allay");
        sendMessage("shakeHand", body);
    }
}