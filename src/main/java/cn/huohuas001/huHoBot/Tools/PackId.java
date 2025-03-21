package cn.huohuas001.huHoBot.Tools;

import java.util.UUID;


public class PackId {
    public static String getPackID() {
        UUID guid = UUID.randomUUID();
        return guid.toString().replace("-", "");
    }
}
