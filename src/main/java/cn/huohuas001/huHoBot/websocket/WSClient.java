package cn.huohuas001.huhobot.websocket;


import cn.huohuas001.huhobot.HuHoBot;
import cn.huohuas001.huhobot.HuHoBotConfig;
import cn.huohuas001.huhobot.utils.Utils;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.allaymc.api.scheduler.Scheduler;
import org.allaymc.api.server.Server;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class WSClient extends WebSocketClient {
    private final Map<String, CompletableFuture<JSONObject>> responseFutureList = new HashMap<>();
    private final HuHoBot plugin;
    private final WSClientManager clientManager;


    /*public WsClient(URI serverUri, WebsocketClientManager clientManager,
                    Map<String, String> headers, SSLContext sslContext) {
        super(serverUri, new Draft_6455(), headers, 10000); // 增加超时到10秒

        try {
            if (sslContext != null) {
                SSLSocketFactory factory = sslContext.getSocketFactory();
                SSLSocket socket = (SSLSocket) factory.createSocket();

                // 强制启用TLS 1.2/1.3
                socket.setEnabledProtocols(new String[]{"TLSv1.2", "TLSv1.3"});

                // 可选：设置支持的密码套件
                socket.setEnabledCipherSuites(new String[]{
                        "TLS_AES_128_GCM_SHA256",
                        "TLS_AES_256_GCM_SHA384",
                        "TLS_CHACHA20_POLY1305_SHA256",
                        "TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256",
                        "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256"
                });

                this.setSocket(socket);
            }
        } catch (Exception e) {
            throw new RuntimeException("创建SSL socket失败", e);
        }
        this.plugin = HuHoBot.getPlugin();

        this.clientManager = clientManager;
    }*/


    public WSClient(URI serverUri, WSClientManager clientManager) {
        super(serverUri);
        this.plugin = HuHoBot.getInstance();
        //this.logger = plugin.getLogger();
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
        String requestId = header.getString("id");

        if (responseFutureList.containsKey(requestId)) {
            CompletableFuture<JSONObject> responseFuture = responseFutureList.get(requestId);
            if (responseFuture != null && !responseFuture.isDone()) {
                responseFuture.complete(jsonData);
            }
            responseFutureList.remove(requestId);
        } else {
            Scheduler scheduler = Server.getInstance().getScheduler();
            scheduler.runLater(HuHoBot.getInstance(), () -> {
                plugin.onWsMsg(jsonData);
            });
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
        sendMessage(type, body, Utils.randomID());
    }

    /**
     * 向服务端发送一条消息
     *
     * @param type      消息类型
     * @param body      消息数据
     * @param requestId 消息Id
     */
    public void sendMessage(String type, JSONObject body, String requestId) {
        JSONObject data = new JSONObject();
        JSONObject header = new JSONObject();
        header.put("type", type);
        header.put("id", requestId);
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
        return sendRequestAndAwaitResponse(type, body, Utils.randomID());
    }

    /**
     * 向服务端发送一条消息并获取返回值
     *
     * @param type      消息类型
     * @param body      消息数据
     * @param requestId 消息Id
     * @return 消息回报体
     */
    public CompletableFuture<JSONObject> sendRequestAndAwaitResponse(String type, JSONObject body, String requestId) {
        if (this.isOpen()) {
            //打包数据并发送
            JSONObject data = new JSONObject();
            JSONObject header = new JSONObject();
            header.put("type", type);
            header.put("id", requestId);
            data.put("header", header);
            data.put("body", body);
            this.send(data.toJSONString());

            //存储回报
            CompletableFuture<JSONObject> responseFuture = new CompletableFuture<>();
            responseFutureList.put(requestId, responseFuture);

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
    public void response(String msg, String type) {
        this.response(msg, type, Utils.randomID());
    }

    /**
     * 向服务端发送一条回报
     *
     * @param msg       回报消息
     * @param type      回报类型：success|error
     * @param requestId 回报Id
     */
    public void response(String msg, String type, String requestId) {
        JSONObject body = new JSONObject();
        body.put("msg", msg);
        sendMessage(type, body, requestId);
    }

    /**
     * 向服务端握手
     */
    private void shakeHand() {
        HuHoBotConfig config = HuHoBot.getConfig();
        JSONObject body = new JSONObject();
        body.put("serverId", config.getServerId());
        if (config.get("hashKey") == null) {
            body.put("hashKey", "");
        } else {
            body.put("hashKey", config.getHashKey());
        }
        body.put("name", config.getServerName());
        body.put("version", HuHoBot.getInstance().getPluginContainer().descriptor().getVersion());
        body.put("platform", "allay");
        sendMessage("shakeHand", body);
    }
}