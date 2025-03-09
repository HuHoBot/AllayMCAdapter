package cn.huohuas001.huHoBot.Settings;

import cn.huohuas001.huHoBot.Tools.PackId;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// 主配置类
@Names(strategy = NameStrategy.IDENTITY) // 保持字段名原样输出
public class PluginConfig extends OkaeriConfig {

    // region 核心配置
    @Comment({
            "服务器唯一ID (启动时自动生成)",
            "! 请勿手动修改，留空即可"
    })
    private String serverId = null;

    @Comment({
            "通信加密密钥 (绑定后自动获取)",
            "! 请勿手动修改，留空即可"
    })
    private String hashKey = null;
    // endregion

    // region 聊天配置
    @Comment("群聊消息格式 (可用变量: {nick}, {msg})")
    @CustomKey("chatFormatGroup")
    private String chatFormatGroup = "群:<{nick}> {msg}";
    // endregion

    // region 服务器配置
    @Comment({
            "MOTD服务器地址",
            "格式: 地址:端口 (示例: play.easecation.net:19132)"
    })
    @CustomKey("motdUrl")
    private String motdUrl = "play.easecation.net:19132";
    @Comment({
            "服务器显示名称"
    })
    @CustomKey("serverName")
    private String serverName = "AllayMC";
    // endregion

    // region 自定义命令
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
    // endregion

    // region Getters & Setters
    public String getServerId() { return serverId; }
    public void setServerId(String serverId) { this.serverId = serverId; }

    public String getHashKey() { return hashKey; }
    public void setHashKey(String hashKey) { this.hashKey = hashKey; }

    public String getChatFormatGroup() { return chatFormatGroup; }
    public String getMotdUrl() { return motdUrl; }
    public String getServerName() { return serverName; }
    public List<CustomCommand> getCustomCommand() { return customCommand; }
    public Map<String, CustomCommand> getCustomCommandMap() {
        return customCommand.stream()
                .collect(Collectors.toMap(CustomCommand::getKey, customCommand -> customCommand,(existing, replacement) -> existing));
    }
    // endregion

    // 初始化方法示例
    public void initializeDefaults() {
        if (this.serverId == null) {
            String uuidString = PackId.getPackID();
            this.serverId = uuidString;
        }
    }
}