package cn.huohuas001.huhobot.websocket.handler;


public class RunCommand extends RequestHandler {
    @Override
    public boolean run() {
        String command = body.getString("cmd");
        runCommand(command);
        return true;
    }
}
