// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import java.util.HashMap;
import java.util.Map;
import webit.script.Context;
import webit.script.Engine;
import webit.script.InternalContext;
import webit.script.Template;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Statement;
import webit.script.exceptions.ParseException;
import webit.script.exceptions.ResourceNotFoundException;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.lang.KeyValues;
import webit.script.util.KeyValuesUtil;

/**
 *
 * @author zqq90
 */
public abstract class AbstractInclude extends Statement {

    private final Expression templateNameExpr;
    private final Expression paramsExpr;
    private final String myTemplateName;
    private final Engine engine;

    public AbstractInclude(Expression templateNameExpr, Expression paramsExpr, Template template, int line, int column) {
        super(line, column);
        this.templateNameExpr = templateNameExpr;
        this.paramsExpr = paramsExpr;
        this.myTemplateName = template.name;
        this.engine = template.engine;
    }

    @SuppressWarnings("unchecked")
    protected Map<String, Object> mergeTemplate(final InternalContext context, boolean export) {
        final Object templateName;
        if ((templateName = templateNameExpr.execute(context)) != null) {
            final KeyValues params;
            final Object paramsObject;
            if (paramsExpr != null
                    && (paramsObject = paramsExpr.execute(context)) != null) {
                if (paramsObject instanceof Map) {
                    params = KeyValuesUtil.wrap((Map) paramsObject);
                } else {
                    throw new ScriptRuntimeException("Template param must be a Map.", paramsExpr);
                }
            } else {
                params = KeyValuesUtil.EMPTY_KEY_VALUES;
            }
            try {
                Template template = engine.getTemplate(myTemplateName, String.valueOf(templateName));
                KeyValues rootParams = engine.isShareRootData() ? KeyValuesUtil.wrap(context.rootParams, params) : params;
                Context newContext = template.mergeToContext(context, rootParams);

                if (export) {
                    Map<String, Object> result = new HashMap<>();
                    newContext.exportTo(result);
                    return result;
                }
                return null;
            } catch (ResourceNotFoundException | ScriptRuntimeException | ParseException e) {
                throw new ScriptRuntimeException(e, this);
            }
        } else {
            throw new ScriptRuntimeException("Template name should not be null.", templateNameExpr);
        }
    }
}
