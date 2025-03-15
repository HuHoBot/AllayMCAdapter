package cn.huohuas001.huHoBot.Command;

import cn.huohuas001.huHoBot.HuHoBot;
import cn.huohuas001.huHoBot.NetEvent.bindRequest;
import org.allaymc.api.command.SimpleCommand;
import org.allaymc.api.command.tree.CommandContext;
import org.allaymc.api.command.tree.CommandTree;
import org.allaymc.api.permission.DefaultPermissions;
import org.allaymc.api.utils.TextFormat;

public class HuHoBotCommand extends SimpleCommand {
    public HuHoBotCommand() {
        super("huhobot", "HuHoBot's control command");
        getPermissions().forEach(DefaultPermissions.OPERATOR::addPermission);

    }

    private void onReconnect(CommandContext sender) {
        if (HuHoBot.getPlugin().reconnect()) {
            sender.addOutput(TextFormat.GOLD + "重连机器人成功.");
        } else {
            sender.addOutput(TextFormat.DARK_RED + "重连机器人失败：已在连接状态.");
        }

    }

    private void onDisconnect(CommandContext sender) {
        if (HuHoBot.getPlugin().disConnectServer()) {
            sender.addOutput(TextFormat.GOLD + "已断开机器人连接.");
        }
    }

    private void onBind(CommandContext sender, String args) {
        bindRequest obj = HuHoBot.getPlugin().bindRequestObj;
        if(obj.confirmBind(args)){
            sender.addOutput(TextFormat.GOLD + "已向服务器发送确认绑定请求，请等待服务端下发配置文件.");
        }else{
            sender.addOutput(TextFormat.DARK_RED + "绑定码错误，请重新输入.");
        }
    }

    private void onHelp(CommandContext sender) {
        sender.addOutput(TextFormat.AQUA + "HuHoBot 操作相关命令");
        sender.addOutput(TextFormat.GOLD + ">" + TextFormat.DARK_GRAY + "/huhobot reconnect - 重新连接服务器");
        sender.addOutput(TextFormat.GOLD + ">" + TextFormat.DARK_GRAY + "/huhobot disconnect - 断开服务器连接");
        sender.addOutput(TextFormat.GOLD + ">" + TextFormat.DARK_GRAY + "/huhobot bind <code:String> - 确认绑定");
    }

    @Override
    public void prepareCommandTree(CommandTree tree) {
        tree.getRoot()
                .key("reconnect")
                .exec(context -> {
                    onReconnect(context);
                    return context.success();
                })
                .root()
                .key("disconnect")
                .exec(context -> {
                    onDisconnect(context);
                    return context.success();
                })
                .root()
                .key("bind")
                .str("code")
                .exec(context -> {
                    String Code = context.getResult(1);
                    onBind(context, Code);
                    return context.success();
                })
                .root()
                .key("help")
                .exec(context -> {
                    onHelp(context);
                    return context.success();
                });

    }
}