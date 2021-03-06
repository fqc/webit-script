// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.core.ast.operators;

import webit.script.InternalContext;
import webit.script.core.ast.Expression;
import webit.script.core.ast.ResetableValueExpression;
import webit.script.util.StatementUtil;

/**
 *
 * @author zqq90
 */
public final class PropertyOperator extends ResetableValueExpression {

    private final Expression expr;
    private final String property;

    public PropertyOperator(Expression expr, String property, int line, int column) {
        super(line, column);
        this.expr = expr;
        this.property = property;
    }

    @Override
    public Object execute(final InternalContext context) {
        try {
            return context.getBeanProperty(
                    expr.execute(context),
                    property);
        } catch (Exception e) {
            throw StatementUtil.castToScriptRuntimeException(e, this);
        }
    }

    @Override
    public Object setValue(final InternalContext context, final Object value) {
        try {
            context.setBeanProperty(
                    expr.execute(context),
                    property, value);
            return value;
        } catch (Exception e) {
            throw StatementUtil.castToScriptRuntimeException(e, this);
        }
    }
}
