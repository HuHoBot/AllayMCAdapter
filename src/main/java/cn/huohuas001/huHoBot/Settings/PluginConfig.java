package cn.huohuas001.huHoBot.Settings;

import cn.huohuas001.huHoBot.Tools.PackId;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// 主配置类
@Names(strategy = NameStrategy.IDENTITY)
public class PluginConfig extends OkaeriConfig {

    // region Getters & Setters
    // region 核心配置
    @Getter
    @Setter
    @Comment({
            "服务器唯一ID (启动时自动生成)",
            "! 请勿手动修改，留空即可"
    })
    private String serverId = null;

    @Getter
    @Setter
    @Comment({
            "通信加密密钥 (绑定后自动获取)",
            "! 请勿手动修改，留空即可"
    })
    private String hashKey = null;
    // endregion

    // region 聊天配置
    @Getter
    @Comment("群聊消息格式 (可用变量: {nick}, {msg})")
    @CustomKey("chatFormatGroup")
    private String chatFormatGroup = "群:<{nick}> {msg}";
    // endregion

    // region 新的MOTD配置结构
    @Comment({
            "MOTD服务器配置",
            "api: 状态查询API地址，使用{server_ip}和{server_port}作为占位符",
            "text: 显示文本格式，使用{online}作为在线人数占位符"
    })
    @CustomKey("motd")
    private MotdConfig motd = new MotdConfig();

    @Names(strategy = NameStrategy.IDENTITY)
    public static class MotdConfig extends OkaeriConfig {
        @Comment("服务器IP地址")
        @CustomKey("server_ip")
        private String serverIp = "play.easecation.net";

        @Comment("服务器端口")
        @CustomKey("server_port")
        private int serverPort = 19132;

        @Comment("状态查询API地址")
        private String api = "https://motdbe.blackbe.work/status_img?host={server_ip}:{server_port}";

        @Comment("显示文本格式")
        private String text = "共{online}人在线";

        @Comment("是否输出在线列表")
        @CustomKey("output_online_list")
        private boolean outputOnlineList = true;

        @Comment("是否发布状态图片")
        @CustomKey("post_img")
        private boolean postImg = true;

        // Getters
        public String getServerIp() { return serverIp; }
        public int getServerPort() { return serverPort; }
        public String getApi() { return api; }
        public String getText() { return text; }
        public boolean isOutputOnlineList() { return outputOnlineList; }
        public boolean isPostImg() { return postImg; }
    }
    // endregion


    @Getter
    @Comment({
            "服务器显示名称"
    })
    @CustomKey("serverName")
    private String serverName = "AllayMC";
    // endregion

    // region 自定义命令
    @Getter
    @Comment("自定义命令列表")
    @CustomKey("customCommand")
    private List<CustomCommand> customCommand = Arrays.asList(
            new CustomCommand("加白名", "whitelist add &1", 0),
            new CustomCommand("管理加白名", "whitelist add &1", 1)
    );

    @Names(strategy = NameStrategy.IDENTITY)
    public static class CustomCommand extends OkaeriConfig {
        @Comment("触发指令 (支持中文)")
        private String key;

        @Comment("实际执行的命令 (&1=第一个参数)")
        private String command;

        @Comment("权限等级 0=玩家 1=管理")
        private int permission;

        // 需要无参构造器
        public CustomCommand() {
        }

        public CustomCommand(String key, String command, int permission) {
            this.key = key;
            this.command = command;
            this.permission = permission;
        }

        // Getters 必须存在
        public String getKey() { return key; }
        public String getCommand() { return command; }
        public int getPermission() { return permission; }
    }

    // 在PluginConfig类顶部添加版本字段
    @Comment("配置版本 (检测到版本小于1时会自动迁移旧配置)")
    @CustomKey("version")
    private int configVersion = 1;

    // endregion

    public MotdConfig getMotd() { return motd; }


    @Deprecated
    public String getMotdUrl() {
        return motd.getServerIp() + ":" + motd.getServerPort();
    }

    public Map<String, CustomCommand> getCustomCommandMap() {
        return customCommand.stream()
                .collect(Collectors.toMap(CustomCommand::getKey, customCommand -> customCommand,(existing, replacement) -> existing));
    }
    // endregion

    // 废弃原有motdUrl字段
    @Deprecated
    @CustomKey("motdUrl")
    private transient String motdUrl;

    // 初始化方法示例
    public void initializeDefaults() {
        if (this.serverId == null) {
            this.serverId = PackId.getPackID();
        }

        // 自动迁移逻辑（当版本号不存在时）
        if (this.configVersion < 1) {
            performConfigMigration();
        }
    }


    private void performConfigMigration() {
        // 如果存在旧版 motdUrl 配置
        if (this.motdUrl != null && !this.motdUrl.isEmpty()) {
            String[] parts = this.motdUrl.split(":");
            this.motd.serverIp = parts[0];
            this.motd.serverPort = (parts.length > 1) ?
                    Integer.parseInt(parts[1]) : 19132;
        }

        // 标记旧字段为已弃用（不再持久化）
        this.motdUrl = null;
        this.configVersion = 1;
    }
}