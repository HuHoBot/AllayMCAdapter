package cn.huohuas001.huHoBot.Tools;

import java.util.UUID;

/**
 * ��ID������
 */
public class PackId {
    //����һ�������UUID
    public static String getPackID() {
        UUID guid = UUID.randomUUID();
        return guid.toString().replace("-", "");
    }
}
