package cn.huohuas001.huHoBot.Tools;

import org.allaymc.api.command.CommandResult;
import org.allaymc.api.command.CommandSender;
import org.allaymc.api.entity.Entity;
import org.allaymc.api.entity.interfaces.EntityPlayer;
import org.allaymc.api.i18n.TextReceiver;
import org.allaymc.api.i18n.TrContainer;
import org.allaymc.api.math.location.Location3dc;
import org.allaymc.api.permission.Permissible;
import org.allaymc.api.permission.tree.PermissionTree;
import org.allaymc.api.server.Server;
import org.allaymc.api.world.gamerule.GameRule;
import org.cloudburstmc.protocol.bedrock.data.command.CommandOriginData;
import org.jetbrains.annotations.ApiStatus.OverrideOnly;

import java.util.Arrays;

public class CustomCommandSender implements CommandSender {
    private final CommandSender original;
    private TrContainer[] output;

    public CustomCommandSender(CommandSender original) {
        this.original = original;
    }

    @Override
    public void handleResult(CommandResult result) {
        if (result.context() != null) {
            if ((Boolean)this.getCmdExecuteLocation().dimension().getWorld().getWorldData().getGameRuleValue(GameRule.SEND_COMMAND_FEEDBACK)) {
                int status = result.status();
                TrContainer[] outputs = (TrContainer[])result.context().getOutputs().toArray((x$0) -> {
                    return new TrContainer[x$0];
                });
                if (result.isSuccess()) {

                    Server.getInstance().broadcastCommandOutputs(result.context().getSender(), status, outputs);
                } else {
                    this.sendCommandOutputs(result.context().getSender(), status, outputs);
                }
                this.output = outputs;
            }
        }
    }

    public TrContainer[] getOutput(){
        return output;
    }

    // 保留原始方法委托
    @Override public String getCommandSenderName() { return original.getCommandSenderName(); }
    @Override public CommandOriginData getCommandOriginData() { return original.getCommandOriginData(); }
    @Override public Location3dc getCmdExecuteLocation() { return original.getCmdExecuteLocation(); }
    @Override public boolean isPlayer() { return original.isPlayer(); }
    @Override public boolean isEntity() { return original.isEntity(); }
    @Override public Entity asEntity() { return original.asEntity(); }
    @Override public EntityPlayer asPlayer() { return original.asPlayer(); }
    @Override public void sendText(String s) {original.sendText(s);}
    @Override public void sendTr(String s, boolean b, Object... objects) {original.sendTr(s, b, objects);}
    @Override public void sendCommandOutputs(CommandSender commandSender, int i, TrContainer... trContainers) {original.sendCommandOutputs(commandSender, i, trContainers);}
    @Override
    public PermissionTree getPermissionTree() {return original.getPermissionTree();}
}
