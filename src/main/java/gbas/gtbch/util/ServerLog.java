package gbas.gtbch.util;

import java.util.LinkedList;

/**
 * size limited (only for add method) linked list
 */
public class ServerLog extends LinkedList<String> {

    private final int maxSize;

    public ServerLog(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public boolean add(String s) {
        while (size() >= maxSize) {
            remove(0);
        }
        return super.add(s);
    }
}
