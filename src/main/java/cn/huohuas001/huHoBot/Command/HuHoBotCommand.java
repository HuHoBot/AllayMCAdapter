package cn.huohuas001.huHoBot.Command;

import cn.huohuas001.huHoBot.HuHoBot;
import cn.huohuas001.huHoBot.NetEvent.bindRequest;
import org.allaymc.api.command.Command;
import org.allaymc.api.command.tree.CommandTree;
import org.allaymc.api.permission.PermissionGroups;
import org.allaymc.api.utils.TextFormat;

public class HuHoBotCommand extends Command {
    public HuHoBotCommand() {
        super("huhobot", "HuHoBot's control command");
        getPermissions().forEach(PermissionGroups.OPERATOR::addPermission);
    }

    @Override
    public void prepareCommandTree(CommandTree tree) {
        tree.getRoot()
                .key("reconnect")
                .exec(context -> {
                    if (HuHoBot.getPlugin().reconnect()) {
                        context.addOutput(TextFormat.GOLD + "重连机器人成功.");
                    } else {
                        context.addOutput(TextFormat.DARK_RED + "重连机器人失败：已在连接状态.");
                    }

                    return context.success();
                })
                .root()
                .key("disconnect")
                .exec(context -> {
                    if (HuHoBot.getPlugin().disConnectServer()) {
                        context.addOutput(TextFormat.GOLD + "已断开机器人连接.");
                    }
                    return context.success();
                })
                .root()
                .key("bind")
                .str("code")
                .exec(context -> {
                    String Code = context.getResult(1);
                    bindRequest obj = HuHoBot.getPlugin().bindRequestObj;
                    if(obj.confirmBind(Code)){
                        context.addOutput(TextFormat.GOLD + "已向服务器发送确认绑定请求，请等待服务端下发配置文件.");
                    }else{
                        context.addOutput(TextFormat.DARK_RED + "绑定码错误，请重新输入.");
                    }
                    return context.success();
                })
                .root()
                .key("help")
                .exec(context -> {
                    context.addOutput(TextFormat.AQUA + "HuHoBot 操作相关命令");
                    context.addOutput(TextFormat.GOLD + ">" + TextFormat.DARK_GRAY + "/huhobot reconnect - 重新连接服务器");
                    context.addOutput(TextFormat.GOLD + ">" + TextFormat.DARK_GRAY + "/huhobot disconnect - 断开服务器连接");
                    context.addOutput(TextFormat.GOLD + ">" + TextFormat.DARK_GRAY + "/huhobot reload - 重载配置文件");
                    context.addOutput(TextFormat.GOLD + ">" + TextFormat.DARK_GRAY + "/huhobot bind <code:String> - 确认绑定");
                    return context.success();
                })
                .root()
                .key("reload")
                .exec(context -> {
                    HuHoBot.reloadConfig();
                    context.addOutput(TextFormat.GOLD + "已重载配置文件.");
                    return context.success();
                });

    }
}