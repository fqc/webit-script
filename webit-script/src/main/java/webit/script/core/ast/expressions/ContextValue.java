// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.core.ast.expressions;

import webit.script.InternalContext;
import webit.script.core.ast.ResetableValueExpression;

/**
 *
 * @author zqq90
 */
public final class ContextValue extends ResetableValueExpression {

    private final int index;

    public ContextValue(int index, int line, int column) {
        super(line, column);
        this.index = index;
    }

    @Override
    public Object execute(final InternalContext context) {
        return context.vars[index];
    }

    @Override
    public Object setValue(final InternalContext context, final Object value) {
        return context.vars[index] = value;
    }
}
