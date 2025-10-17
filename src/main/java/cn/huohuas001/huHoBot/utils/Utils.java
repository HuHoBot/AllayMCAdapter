package cn.huohuas001.huhobot.utils;

import java.util.UUID;

public final class Utils {
    public static String randomID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
