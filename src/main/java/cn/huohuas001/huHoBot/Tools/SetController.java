package cn.huohuas001.huHoBot.Tools;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SetController {
    /**
     * ��һ��Set����ת��ΪList��Ȼ��ָ�ɶ����List��
     *
     * @param set      Ҫ�ָ��Set���ϡ�
     * @param size     ÿ����List�����������
     * @param <String> Set������Ԫ�ص����͡�
     * @return �ָ���List�б�
     */
    public static <String> List<List<String>> chunkSet(Set<String> set, int size) {
        // ��Setת��ΪList
        List<String> list = set.stream().collect(Collectors.toList());
        // ʹ��chunkList�������з�Ƭ
        return chunkList(list, size);
    }

    private static <String> List<List<String>> chunkList(List<String> list, int size) {
        List<List<String>> chunks = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            List<String> chunk = list.subList(i, Math.min(i + size, list.size()));
            chunks.add(chunk);
        }
        return chunks;
    }


    public static List<String> searchInSet(Set<String> set, String keyword) {
        return set.stream()
                .filter(s -> s.contains(keyword))
                .collect(Collectors.toList());
    }

}
