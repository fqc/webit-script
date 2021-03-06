// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.core.text.impl;

import java.io.IOException;
import webit.script.Engine;
import webit.script.Init;
import webit.script.Template;
import webit.script.core.ast.Statement;
import webit.script.core.text.TextStatementFactory;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.io.charset.CoderFactory;
import webit.script.io.charset.Encoder;
import webit.script.util.ByteArrayOutputStream;

/**
 *
 * @author zqq90
 */
public class ByteArrayTextStatementFactory implements TextStatementFactory {

    protected String encoding;
    protected CoderFactory coderFactory;
    protected final ThreadLocal<Encoder> encoders = new ThreadLocal<>();
    protected final ThreadLocal<ByteArrayOutputStream> outputs = new ThreadLocal<>();

    @Init
    public void init(Engine engine) {
        encoding = engine.getEncoding();
    }

    @Override
    public void startTemplateParser(Template template) {
        encoders.set(coderFactory.newEncoder(encoding));
        outputs.set(new ByteArrayOutputStream(512));
    }

    @Override
    public void finishTemplateParser(Template template) {
        encoders.remove();
        outputs.remove();
    }

    protected byte[] getBytes(char[] text) {
        try {
            final ByteArrayOutputStream out = outputs.get();
            encoders.get().write(text, 0, text.length, out);
            final byte[] bytes = out.toArray();
            out.reset();
            return bytes;
        } catch (IOException ex) {
            throw new ScriptRuntimeException(ex);
        }
    }

    @Override
    public Statement getTextStatement(Template template, char[] text, int line, int column) {
        return new ByteArrayTextStatement(getBytes(text), line, column);
    }
}
