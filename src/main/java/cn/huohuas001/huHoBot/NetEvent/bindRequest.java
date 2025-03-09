package cn.huohuas001.huHoBot.NetEvent;

import cn.huohuas001.huHoBot.HuHoBot;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Slf4j
public class bindRequest extends EventRunner {
    private final HuHoBot plugin = HuHoBot.getPlugin();
    private final Map<String,String> bindMap = new HashMap<>();

    @Override
    public boolean run() {
        String bindCode = body.getString("bindCode");
        log.info("�յ�һ���µİ�������ȷ�ϰ󶨣�������\"/huhobot bind {}\"������ȷ��", bindCode);
        bindMap.put(bindCode,packId);
        return true;
    }

    public boolean confirmBind(String bindCode){
        if(bindMap.containsKey(bindCode)){
            sendMessage("bindConfirm",new JSONObject());
            bindMap.remove(bindCode);
            return true;
        }
        return false;
    }
}
