// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.core.ast.expressions;

import webit.script.InternalContext;
import webit.script.core.ast.ResetableValueExpression;
import webit.script.global.GlobalManager;

/**
 *
 * @author zqq90
 */
public final class GlobalValue extends ResetableValueExpression {

    private final GlobalManager manager;
    private final int index;

    public GlobalValue(GlobalManager manager, int index, int line, int column) {
        super(line, column);
        this.index = index;
        this.manager = manager;
    }

    @Override
    public Object execute(final InternalContext context) {
        return this.manager.getGlobal(index);
    }

    @Override
    public Object setValue(final InternalContext context, final Object value) {
        this.manager.setGlobal(index, value);
        return value;
    }
}
