package org.chy.lamiaplugin.utlis;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class Sets {

    static final Set<String> EMPTY_SET = new HashSet<>();

    public static Set<String> emptySet() {
        return EMPTY_SET;
    }

    public static <T> void compare(Set<T> s1, Set<T> s2, Consumer<T> add, Consumer<T> remove) {
        if (s1 == null && s2 == null) {
            return;
        }
        if (s1 == null) {
            for (T t : s2) {
                add.accept(t);
            }
            return;
        }

        if (s2 == null) {
            for (T t : s1) {
                remove.accept(t);
            }
            return;
        }

        boolean change = false;
        for (T t : s1) {
            if (!s2.contains(t)) {
                add.accept(t);
                change = true;
            }
        }
        // 如果两个集合的大小相等，且没有变化，那么就不需要删除了
        if (s1.size() == s2.size() && !change) {
            return;
        }

        for (T t : s2) {
            if (!s1.contains(t)) {
                remove.accept(t);
            }
        }
    }

}
