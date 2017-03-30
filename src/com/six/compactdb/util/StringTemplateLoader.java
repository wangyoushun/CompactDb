package com.six.compactdb.util;

import freemarker.cache.TemplateLoader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class StringTemplateLoader implements TemplateLoader {
    private String template;

    public StringTemplateLoader(String template) {
        this.template = template;
        if(this.template == null) {
            this.template = "";
        }

    }

    public void closeTemplateSource(Object templateSource) throws IOException {
        ((StringReader)templateSource).close();
    }

    public Object findTemplateSource(String arg0) throws IOException {
        return new StringReader(this.template);
    }

    public long getLastModified(Object arg0) {
        return 0L;
    }

    public Reader getReader(Object templateSource, String encoding) throws IOException {
        return (Reader)templateSource;
    }
}
