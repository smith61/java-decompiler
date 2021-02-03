package net.jsmith.java.byteforge.decompiler;

import java.util.HashMap;
import java.util.Map;

public class HtmlAttributes {

    private final Map<String, String> attributes;

    public HtmlAttributes() {
        this.attributes = new HashMap<>();
    }

    public void add(String key, String value) {
        this.attributes.put(key, value);
    }

    public String toAttributeString() {
        StringBuilder builder = new StringBuilder();
        for (var entry : this.attributes.entrySet()) {
            if (builder.length() != 0) {
                builder.append(' ');
            }

            builder.append(entry.getKey());
            builder.append("=\"");
            builder.append(entry.getValue());
            builder.append('"');
        }

        return builder.toString();
    }

}
