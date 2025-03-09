package cn.huohuas001.huHoBot.Settings;

import cn.huohuas001.huHoBot.Tools.PackId;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// ��������
@Names(strategy = NameStrategy.IDENTITY) // �����ֶ���ԭ�����
public class PluginConfig extends OkaeriConfig {

    // region ��������
    @Comment({
            "������ΨһID (����ʱ�Զ�����)",
            "! �����ֶ��޸ģ����ռ���"
    })
    private String serverId = null;

    @Comment({
            "ͨ�ż�����Կ (�󶨺��Զ���ȡ)",
            "! �����ֶ��޸ģ����ռ���"
    })
    private String hashKey = null;
    // endregion

    // region ��������
    @Comment("Ⱥ����Ϣ��ʽ (���ñ���: {nick}, {msg})")
    @CustomKey("chatFormatGroup")
    private String chatFormatGroup = "Ⱥ:<{nick}> {msg}";
    // endregion

    // region ����������
    @Comment({
            "MOTD��������ַ",
            "��ʽ: ��ַ:�˿� (ʾ��: play.easecation.net:19132)"
    })
    @CustomKey("motdUrl")
    private String motdUrl = "play.easecation.net:19132";
    @Comment({
            "��������ʾ����"
    })
    @CustomKey("serverName")
    private String serverName = "AllayMC";
    // endregion

    // region �Զ�������
    @Comment("�Զ��������б�")
    @CustomKey("customCommand")
    private List<CustomCommand> customCommand = Arrays.asList(
            new CustomCommand("�Ӱ���", "whitelist add &1", 0),
            new CustomCommand("����Ӱ���", "whitelist add &1", 1)
    );

    @Names(strategy = NameStrategy.IDENTITY)
    public static class CustomCommand extends OkaeriConfig {
        @Comment("����ָ�� (֧������)")
        private String key;

        @Comment("ʵ��ִ�е����� (&1=��һ������)")
        private String command;

        @Comment("Ȩ�޵ȼ� 0=��� 1=����")
        private int permission;

        // ��Ҫ�޲ι�����
        public CustomCommand() {
        }

        public CustomCommand(String key, String command, int permission) {
            this.key = key;
            this.command = command;
            this.permission = permission;
        }

        // Getters �������
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

    // ��ʼ������ʾ��
    public void initializeDefaults() {
        if (this.serverId == null) {
            String uuidString = PackId.getPackID();
            this.serverId = uuidString;
        }
    }
}