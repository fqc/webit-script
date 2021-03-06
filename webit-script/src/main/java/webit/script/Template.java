// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script;

import java.io.OutputStream;
import java.io.Writer;
import java.util.Map;
import webit.script.core.Parser;
import webit.script.core.ast.TemplateAST;
import webit.script.debug.BreakPointListener;
import webit.script.exceptions.ParseException;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.exceptions.TemplateException;
import webit.script.io.Out;
import webit.script.io.impl.OutputStreamOut;
import webit.script.io.impl.WriterOut;
import webit.script.lang.KeyValues;
import webit.script.loaders.Resource;
import webit.script.util.KeyValuesUtil;

/**
 *
 * @author zqq90
 */
public final class Template {

    public final Engine engine;
    public final String name;
    public final Resource resource;

    private TemplateAST ast;
    private long lastModified;

    Template(Engine engine, String name, Resource resource) {
        this.engine = engine;
        this.name = name;
        this.resource = resource;
    }

    /**
     * Reload this template.
     *
     * @since 1.4.0
     * @throws ParseException
     */
    public void reload() throws ParseException {
        parse(true);
    }

    private TemplateAST parse(boolean force) throws ParseException {
        TemplateAST myAst = this.ast;
        synchronized (this) {
            if (force || myAst == null || this.resource.isModified()) {
                myAst = new Parser().parse(this);
                this.ast = myAst;
                this.lastModified = System.currentTimeMillis();
            }
        }
        return myAst;
    }

    /**
     * Merge this template.
     *
     * @param outputStream
     * @return Context
     * @throws ScriptRuntimeException
     * @throws ParseException
     */
    public Context merge(final OutputStream outputStream) throws ScriptRuntimeException, ParseException {
        return merge(KeyValuesUtil.EMPTY_KEY_VALUES, new OutputStreamOut(outputStream, engine));
    }

    /**
     * Merge this template.
     *
     * @param out
     * @param encoding
     * @return Context
     * @throws ScriptRuntimeException
     * @throws ParseException
     */
    public Context merge(final OutputStream out, final String encoding) throws ScriptRuntimeException, ParseException {
        return merge(KeyValuesUtil.EMPTY_KEY_VALUES, new OutputStreamOut(out, encoding, engine));
    }

    /**
     * Merge this template.
     *
     * @param writer
     * @return Context
     * @throws ScriptRuntimeException
     * @throws ParseException
     */
    public Context merge(final Writer writer) throws ScriptRuntimeException, ParseException {
        return merge(KeyValuesUtil.EMPTY_KEY_VALUES, new WriterOut(writer, engine));
    }

    /**
     * Merge this template.
     *
     * @param root
     * @param outputStream
     * @return Context
     * @throws ScriptRuntimeException
     * @throws ParseException
     */
    public Context merge(final Map<String, Object> root, final OutputStream outputStream) throws ScriptRuntimeException, ParseException {
        return merge(KeyValuesUtil.wrap(root), new OutputStreamOut(outputStream, engine));
    }

    /**
     * Merge this template.
     *
     * @param root
     * @param out
     * @param encoding
     * @return Context
     * @throws ScriptRuntimeException
     * @throws ParseException
     */
    public Context merge(final Map<String, Object> root, final OutputStream out, final String encoding) throws ScriptRuntimeException, ParseException {
        return merge(KeyValuesUtil.wrap(root), new OutputStreamOut(out, encoding, engine));
    }

    /**
     * Merge this template.
     *
     * @param root
     * @param writer
     * @return Context
     * @throws ScriptRuntimeException
     * @throws ParseException
     */
    public Context merge(final Map<String, Object> root, final Writer writer) throws ScriptRuntimeException, ParseException {
        return merge(KeyValuesUtil.wrap(root), new WriterOut(writer, engine));
    }

    /**
     * Merge this template.
     *
     * @param root
     * @param out
     * @return Context
     * @throws ScriptRuntimeException
     * @throws ParseException
     */
    public Context merge(final KeyValues root, final OutputStream out) throws ScriptRuntimeException, ParseException {
        return merge(root, new OutputStreamOut(out, engine));
    }

    /**
     * Merge this template.
     *
     * @param root
     * @param out
     * @param encoding
     * @return Context
     * @throws ScriptRuntimeException
     * @throws ParseException
     */
    public Context merge(final KeyValues root, final OutputStream out, final String encoding) throws ScriptRuntimeException, ParseException {
        return merge(root, new OutputStreamOut(out, encoding, engine));
    }

    /**
     * Merge this template.
     *
     * @param root
     * @param writer
     * @return Context
     * @throws ScriptRuntimeException
     * @throws ParseException
     */
    public Context merge(final KeyValues root, final Writer writer) throws ScriptRuntimeException, ParseException {
        return merge(root, new WriterOut(writer, engine));
    }

    /**
     * Merge this template.
     *
     * @since 1.4.0
     * @param out
     * @return Context
     * @throws ScriptRuntimeException
     * @throws ParseException
     */
    public Context merge(final Out out) throws ScriptRuntimeException, ParseException {
        return merge(KeyValuesUtil.EMPTY_KEY_VALUES, out);
    }

    /**
     * Merge this template.
     *
     * @param root
     * @param out
     * @return Context
     * @throws ScriptRuntimeException
     * @throws ParseException
     */
    public Context merge(final KeyValues root, final Out out) throws ScriptRuntimeException, ParseException {
        try {
            final TemplateAST myAst;
            return (((myAst = this.ast) == null || this.resource.isModified())
                    ? parse(false)
                    : myAst)
                    .execute(this, out, root);
        } catch (Exception e) {
            throw completeException(e);
        }
    }

    public Context mergeToContext(final InternalContext context, final KeyValues root) throws ScriptRuntimeException, ParseException {
        try {
            final TemplateAST myAst;
            return (((myAst = this.ast) == null || this.resource.isModified())
                    ? parse(false)
                    : myAst)
                    .execute(this, context, root);
        } catch (Exception e) {
            throw completeException(e);
        }
    }

    public Context debug(final KeyValues root, final Out out, final BreakPointListener listener) throws ScriptRuntimeException, ParseException {
        try {
            return new Parser().parse(this, listener)
                    .execute(this, out, root);
        } catch (Exception e) {
            throw completeException(e);
        }
    }

    public void reset() {
        this.ast = null;
        this.lastModified = 0;
    }

    public long getLastModified() {
        return this.lastModified;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || !(obj instanceof Template)) {
            return false;
        }
        Template other = (Template) obj;
        return this.engine == other.engine
                && this.name.equals(other.name);
    }

    private TemplateException completeException(final Exception exception) {
        return ((exception instanceof TemplateException)
                ? ((TemplateException) exception)
                : new ScriptRuntimeException(exception)).setTemplate(this);
    }
}
