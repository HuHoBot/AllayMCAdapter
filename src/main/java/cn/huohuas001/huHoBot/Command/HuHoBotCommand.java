package cn.huohuas001.huHoBot.Command;

import cn.huohuas001.huHoBot.HuHoBot;
import cn.huohuas001.huHoBot.NetEvent.bindRequest;
import org.allaymc.api.command.CommandSender;
import org.allaymc.api.command.SimpleCommand;
import org.allaymc.api.command.tree.CommandContext;
import org.allaymc.api.command.tree.CommandTree;
import org.allaymc.api.permission.DefaultPermissions;
import org.allaymc.api.server.Server;
import org.allaymc.api.utils.TextFormat;

public class HuHoBotCommand extends SimpleCommand {
    public HuHoBotCommand() {
        super("huhobot", "HuHoBot's control command");
        getPermissions().forEach(DefaultPermissions.OPERATOR::addPermission);

    }

    private void onReconnect(CommandContext sender) {
        if (HuHoBot.getPlugin().reconnect()) {
            sender.addOutput(TextFormat.GOLD + "���������˳ɹ�.");
        } else {
            sender.addOutput(TextFormat.DARK_RED + "����������ʧ�ܣ���������״̬.");
        }

    }

    private void onDisconnect(CommandContext sender) {
        if (HuHoBot.getPlugin().disConnectServer()) {
            sender.addOutput(TextFormat.GOLD + "�ѶϿ�����������.");
        }
    }

    private void onBind(CommandContext sender, String args) {
        bindRequest obj = HuHoBot.getPlugin().bindRequestObj;
        if(obj.confirmBind(args)){
            sender.addOutput(TextFormat.GOLD + "�������������ȷ�ϰ�������ȴ�������·������ļ�.");
        }else{
            sender.addOutput(TextFormat.DARK_RED + "�����������������.");
        }
    }

    private void onHelp(CommandContext sender) {
        sender.addOutput(TextFormat.AQUA + "HuHoBot �����������");
        sender.addOutput(TextFormat.GOLD + ">" + TextFormat.DARK_GRAY + "/huhobot reconnect - �������ӷ�����");
        sender.addOutput(TextFormat.GOLD + ">" + TextFormat.DARK_GRAY + "/huhobot disconnect - �Ͽ�����������");
        sender.addOutput(TextFormat.GOLD + ">" + TextFormat.DARK_GRAY + "/huhobot bind <code:String> - ȷ�ϰ�");
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
                });

    }
}