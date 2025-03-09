package cn.huohuas001.huHoBot;


import cn.huohuas001.huHoBot.Settings.PluginConfig;
import cn.huohuas001.huHoBot.Tools.PackId;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.allaymc.api.plugin.PluginDescriptor;
import org.allaymc.api.server.Server;
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
        log.info("��������ӳɹ�.");
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
        log.error("�����ѶϿ�,������:{} ������Ϣ:{}", code, reason);
        clientManager.clientReconnect();
    }

    @Override
    public void onError(Exception ex) {
        log.error("���ӷ�������!������Ϣ:{}", ex.getMessage());
        clientManager.clientReconnect();
    }



    /**
     * �����˷���һ����Ϣ
     *
     * @param type ��Ϣ����
     * @param body ��Ϣ����
     */
    public void sendMessage(String type, JSONObject body) {
        String newPackId = PackId.getPackID();
        sendMessage(type, body, newPackId);
    }

    /**
     * �����˷���һ����Ϣ
     *
     * @param type   ��Ϣ����
     * @param body   ��Ϣ����
     * @param packId ��ϢId
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
     * �����˷���һ����Ϣ����ȡ����ֵ
     *
     * @param type ��Ϣ����
     * @param body ��Ϣ����
     * @return ��Ϣ�ر���
     */
    public CompletableFuture<JSONObject> sendRequestAndAwaitResponse(String type, JSONObject body) {
        String newPackId = PackId.getPackID();
        return sendRequestAndAwaitResponse(type, body, newPackId);
    }

    /**
     * �����˷���һ����Ϣ����ȡ����ֵ
     *
     * @param type   ��Ϣ����
     * @param body   ��Ϣ����
     * @param packId ��ϢId
     * @return ��Ϣ�ر���
     */
    public CompletableFuture<JSONObject> sendRequestAndAwaitResponse(String type, JSONObject body, String packId) {
        if (this.isOpen()) {
            //������ݲ�����
            JSONObject data = new JSONObject();
            JSONObject header = new JSONObject();
            header.put("type", type);
            header.put("id", packId);
            data.put("header", header);
            data.put("body", body);
            this.send(data.toJSONString());

            //�洢�ر�
            CompletableFuture<JSONObject> responseFuture = new CompletableFuture<>();
            responseFutureList.put(packId, responseFuture);

            return responseFuture;
        } else {
            throw new IllegalStateException("WebSocket connection is not open.");
        }
    }

    /**
     * �����˷���һ���ر�
     *
     * @param msg  �ر���Ϣ
     * @param type �ر����ͣ�success|error
     */
    public void respone(String msg, String type) {
        String newPackId = PackId.getPackID();
        this.respone(msg, type, newPackId);
    }

    /**
     * �����˷���һ���ر�
     *
     * @param msg    �ر���Ϣ
     * @param type   �ر����ͣ�success|error
     * @param packId �ر�Id
     */
    public void respone(String msg, String type, String packId) {
        JSONObject body = new JSONObject();
        body.put("msg", msg);
        sendMessage(type, body, packId);
    }

    /**
     * ����������
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
        body.put("version", HuHoBot.getPlugin().getPluginContainer().descriptor().getVersion());
        body.put("platform", "allay");
        sendMessage("shakeHand", body);
    }
}