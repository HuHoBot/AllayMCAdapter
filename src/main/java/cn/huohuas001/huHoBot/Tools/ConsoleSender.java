package cn.huohuas001.huHoBot.Tools;

import org.allaymc.api.command.CommandResult;
import org.allaymc.api.command.CommandSender;
import org.allaymc.api.entity.Entity;
import org.allaymc.api.entity.interfaces.EntityPlayer;
import org.allaymc.api.i18n.TrContainer;
import org.allaymc.api.math.location.Location3dc;
import org.allaymc.api.permission.tree.PermissionTree;
import org.allaymc.api.server.Server;
import org.allaymc.api.world.gamerule.GameRule;
import org.cloudburstmc.protocol.bedrock.data.command.CommandOriginData;

public class ConsoleSender implements CommandSender {

    @Override
    public String getCommandSenderName() {
        return "";
    }

    @Override
    public CommandOriginData getCommandOriginData() {
        return null;
    }

    @Override
    public Location3dc getCmdExecuteLocation() {
        return null;
    }

    @Override
    public void sendText(String s) {

    }

    @Override
    public void sendTr(String s, boolean b, Object... objects) {

    }

    @Override
    public void sendCommandOutputs(CommandSender commandSender, int i, TrContainer... trContainers) {

    }

    @Override
    public PermissionTree getPermissionTree() {
        return null;
    }
}
