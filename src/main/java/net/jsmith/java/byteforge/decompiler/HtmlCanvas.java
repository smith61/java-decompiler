package net.jsmith.java.byteforge.decompiler;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Objects;

import org.apache.commons.text.StringEscapeUtils;

public class HtmlCanvas {

    private final Writer output;

    public HtmlCanvas() {
        this(new StringWriter());
    }

    public HtmlCanvas(Writer output) {
        this.output = Objects.requireNonNull(output, "output");
    }

    public String toHtml() {
        return this.output.toString();
    }

    public HtmlCanvas body() throws IOException {
        this.output.write("<body>");
        return this;
    }

    public HtmlCanvas _body() throws IOException {
        this.output.write("</body>");
        return this;
    }

    public HtmlCanvas content(String content) throws IOException {
        this.output.write(content);
        return this;
    }

    public HtmlCanvas html() throws IOException {
        this.output.write("<html>");
        return this;
    }

    public HtmlCanvas _html() throws IOException {
        this.output.write("</html>");
        return this;
    }

    public HtmlCanvas pre() throws IOException {
        this.output.write("<pre>");
        return this;
    }

    public HtmlCanvas _pre() throws IOException {
        this.output.write("</pre>");
        return this;
    }

    public HtmlCanvas span() throws IOException {
        this.output.write("<span>");
        return this;
    }

    public HtmlCanvas span(HtmlAttributes attributes) throws IOException {
        this.output.write("<span ");
        this.output.write(attributes.toAttributeString());
        this.output.write('>');
        return this;
    }

    public HtmlCanvas _span() throws IOException {
        this.output.write("</span>");
        return this;
    }

    public HtmlCanvas write(char c) throws IOException {
        this.output.write(c);
        return this;
    }

    public HtmlCanvas write(String unescapedString) throws IOException {
        this.output.write(StringEscapeUtils.escapeHtml4(unescapedString));
        return this;
    }

}
