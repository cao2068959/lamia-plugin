package org.chy.lamiaplugin.expression.components;

import com.chy.lamia.convert.core.components.NameHandler;
import org.apache.commons.lang3.StringUtils;

public class SimpleNameHandler implements NameHandler {

    String prefix = "lamia";
    String tempPrefix = "temp";

    @Override
    public String generateName(String type) {
        return prefix + StringUtils.capitalize(type);
    }

    @Override
    public String generateTempName(String name) {
        return tempPrefix + StringUtils.capitalize(name);
    }
}

