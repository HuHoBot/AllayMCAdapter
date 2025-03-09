package cn.huohuas001.huHoBot.NetEvent;

import cn.huohuas001.huHoBot.HuHoBot;
import cn.huohuas001.huHoBot.Tools.SetController;
import com.alibaba.fastjson2.JSONObject;
import org.allaymc.api.server.Server;


import java.util.List;
import java.util.Set;

public class QueryAllowList extends EventRunner {
    @Override
    public boolean run() {
        Set<String> whiteList = Server.getInstance().getWhitelistedPlayers();
        StringBuilder whitelistNameString = new StringBuilder();
        JSONObject rBody = new JSONObject();
        if (body.containsKey("key")) {
            String key = body.getString("key");
            if (key.length() < 2) {
                whitelistNameString.append("��ѯ�������ؼ���:").append(key).append("�������:\n");
                whitelistNameString.append("��ʹ��������ĸ�����ϵĹؼ��ʽ��в�ѯ!");
                rBody.put("list", whitelistNameString);
                sendMessage("queryWl", rBody);
                return true;
            }
            whitelistNameString.append("��ѯ�������ؼ���:").append(key).append("�������:\n");
            List<String> filterList = SetController.searchInSet(whiteList, key);
            if (filterList.isEmpty()) {
                whitelistNameString.append("�޽��\n");
            } else {
                for (String plName : filterList) {
                    whitelistNameString.append(plName).append("\n");
                }
                whitelistNameString.append("����").append(filterList.size()).append("�����");
            }
            rBody.put("list", whitelistNameString);
            sendMessage("queryWl", rBody);
        } else if (body.containsKey("page")) {
            int page = body.getInteger("page");
            whitelistNameString.append("���ڰ���������:\n");
            List<List<String>> splitedNameList = SetController.chunkSet(whiteList, 10);
            List<String> currentNameList = splitedNameList.get(page - 1);
            if (page - 1 > splitedNameList.size()) {
                whitelistNameString.append("û�и�ҳ��\n");
                whitelistNameString.append("����").append(splitedNameList.size()).append("ҳ\n��ʹ��/������� {ҳ��}����ҳ");
            } else {
                for (String plName : currentNameList) {
                    whitelistNameString.append(plName).append("\n");
                }
                whitelistNameString.append("����").append(splitedNameList.size()).append("ҳ����ǰΪ��").append(page).append("ҳ\n��ʹ��/������� {ҳ��}����ҳ");
            }
            rBody.put("list", whitelistNameString);
            sendMessage("queryWl", rBody);
        } else {
            whitelistNameString.append("���ڰ���������:\n");
            List<List<String>> splitedNameList = SetController.chunkSet(whiteList, 10);
            if (splitedNameList.isEmpty()) {
                whitelistNameString.append("�޽��\n");
            } else {
                List<String> currentNameList = splitedNameList.get(0);
                for (String plName : currentNameList) {
                    whitelistNameString.append(plName).append("\n");
                }
            }
            whitelistNameString.append("����").append(splitedNameList.size()).append("ҳ����ǰΪ��1ҳ\n��ʹ��/������� {ҳ��}����ҳ");
            rBody.put("list", whitelistNameString);
            sendMessage("queryWl", rBody);
        }
        return true;
    }
}
