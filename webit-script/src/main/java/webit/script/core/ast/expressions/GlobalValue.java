// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.expressions;

import webit.script.Context;
import webit.script.core.ast.AbstractExpression;
import webit.script.core.ast.ResetableValueExpression;
import webit.script.global.GlobalManager;

/**
 *
 * @author Zqq
 */
public final class GlobalValue extends AbstractExpression implements ResetableValueExpression {

    private final GlobalManager manager;
    private final int index;

    public GlobalValue(GlobalManager manager, int index, int line, int column) {
        super(line, column);
        this.index = index;
        this.manager = manager;
    }

    public Object execute(final Context context) {
        return this.manager.getVariant(index);
    }

    public Object setValue(final Context context, final Object value) {
        this.manager.setVariant(index, value);
        return value;
    }
}
