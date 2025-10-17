package cn.huohuas001.huhobot.utils;

import cn.huohuas001.huhobot.HuHoBot;
import lombok.Getter;
import org.allaymc.api.command.CommandSender;
import org.allaymc.api.math.location.Location3dc;
import org.allaymc.api.message.I18n;
import org.allaymc.api.message.LangCode;
import org.allaymc.api.message.TrContainer;
import org.allaymc.api.permission.PermissionGroup;
import org.allaymc.api.permission.PermissionGroups;
import org.allaymc.api.server.Server;

public class HuHoBotCommandSender implements CommandSender {

    @Getter
    public final StringBuilder outputs = new StringBuilder();

    @Override
    public String getCommandSenderName() {
        return "HuHoBot";
    }

    @Override
    public Location3dc getCommandExecuteLocation() {
        return Server.getInstance().getWorldPool().getDefaultWorld().getSpawnPoint();
    }

    @Override
    public void sendMessage(String message) {
        for (var line : message.split("\n")) {
            this.outputs.append(line).append("\n");
            HuHoBot.getInstance().getPluginLogger().info(line);
        }
    }

    @Override
    public void sendTranslatable(String translatable, Object... args) {
        sendMessage(I18n.get().tr(LangCode.zh_CN, translatable, args));
    }

    @Override
    public void sendCommandOutputs(CommandSender sender, int status, TrContainer... outputs) {
        for (var output : outputs) {
            sendMessage(I18n.get().tr(LangCode.zh_CN, output.str(), output.args()));
        }
    }

    @Override
    public PermissionGroup getPermissionGroup() {
        return PermissionGroups.OPERATOR;
    }
}