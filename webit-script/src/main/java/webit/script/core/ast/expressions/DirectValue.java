// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.core.ast.expressions;

import webit.script.Context;
import webit.script.core.ast.Constable;
import webit.script.core.ast.Expression;

/**
 *
 * @author zqq90
 */
public final class DirectValue extends Expression implements Constable {

    public final Object value;

    public DirectValue(Object value, int line, int column) {
        super(line, column);
        this.value = value;
    }

    @Override
    public Object execute(final Context context) {
        return value;
    }

    @Override
    public Object getConstValue() {
        return value;
    }
}
