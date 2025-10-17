package cn.huohuas001.huhobot.websocket;

import cn.huohuas001.huhobot.HuHoBot;
import com.alibaba.fastjson2.JSONObject;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.allaymc.api.scheduler.Scheduler;
import org.allaymc.api.server.Server;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

@Slf4j
public class WSClientManager {

    private static final int RECONNECT_DELAY = 5; // 重连延迟时间，单位为秒
    private static final int MAX_RECONNECT_ATTEMPTS = 5; // 最大重连尝试次数

    private static WSClient client; //Websocket客户端

    private final Scheduler scheduler;

    private int reconnectAttempts;
    @Setter
    private boolean shouldReconnect; // 控制是否重连的变量

    private int currentTask, autoDisConnectTask;


    public WSClientManager() {
        this.scheduler = Server.getInstance().getScheduler();
        this.shouldReconnect = true;
    }

    /**
     * 客户端自动重连循环
     */
    private boolean autoReconnect() {
        synchronized (this) {
            if (currentTask == 0) {
                return false;
            }
            reconnectAttempts++;
            if (reconnectAttempts >= MAX_RECONNECT_ATTEMPTS) {
                log.warn(" 重连尝试已达到最大次数，将不再尝试重新连接。");
                cancelCurrentTask();
                return false;
            }
            if (!shouldReconnect) {
                cancelCurrentTask();
                return false;
            }
            log.info(" 正在尝试重新连接,这是第({}/" + MAX_RECONNECT_ATTEMPTS + ")次连接", reconnectAttempts);
            this.connectServer();
            return true;
        }
    }

    public void cancelCurrentTask() {
        if (currentTask != 0) {
            currentTask = 0;
            reconnectAttempts = 0;
        }
    }

    public WSClient getClient() {
        return client;
    }

    public boolean shutdownClient() {
        if (client != null && client.isOpen()) {
            client.close(1000);
            return true;
        }
        return false;
    }

    public void autoDisConnectClient() {
        log.info("连接超时，已自动重连");
        shutdownClient();
    }

    public void setAutoDisConnectTask() {
        if (autoDisConnectTask == 0) {
            scheduler.scheduleDelayed(HuHoBot.getInstance(), () -> {
                autoDisConnectClient();
                return true;
            }, 6 * 60 * 60 * 20);
            autoDisConnectTask = 1;
        }
    }

    /**
     * 连接HuHoBot服务器
     */
    public boolean connectServer() {
        log.info(" 正在连接服务端...");
        try {
            URI uri = new URI(ServerConfig.WS_SERVER_URL);
            if (client == null || !client.isOpen()) {
                client = new WSClient(uri, this);
                setShouldReconnect(true); // 设置是否重连
                client.connect();
            }
            return true;
        } catch (URISyntaxException e) {
            log.error("连接URL格式错误", e);
        } catch (Exception e) {
            log.error("连接HuHoBot失败", e);
        }
        return false;
    }

    private SSLContext createCloudflareSSLContext() throws Exception {
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, new TrustManager[]{
                new X509TrustManager() {
                    public void checkClientTrusted(X509Certificate[] chain, String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] chain, String authType) {
                        log.debug("接受服务器证书: {}", chain != null && chain.length > 0 ? chain[0].getSubjectX500Principal() : "无证书");
                    }

                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }
        }, new SecureRandom());

        // 设置协议版本
        context.getDefaultSSLParameters().setProtocols(new String[]{"TLSv1.2", "TLSv1.3"});
        return context;
    }

    public boolean isOpen() {
        return client.isOpen();
    }

    public void sendHeart() {
        client.sendMessage("heart", new JSONObject());
    }

    public void clientReconnect() {
        if (shouldReconnect && currentTask == 0) {
            scheduler.scheduleRepeating(HuHoBot.getInstance(), this::autoReconnect, RECONNECT_DELAY * 20);
            currentTask = 1;
        }
    }
}
