// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.operators;

import webit.script.Context;
import webit.script.core.ast.AbstractExpression;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Optimizable;
import webit.script.core.ast.expressions.DirectValue;
import webit.script.util.ALU;
import webit.script.util.StatementUtil;

/**
 *
 * @author Zqq
 */
public final class Not extends AbstractExpression implements Optimizable {

    private final Expression expr;

    public Not(Expression expr, int line, int column) {
        super(line, column);
        this.expr = expr;
    }

    public Object execute(final Context context) {
        return !ALU.isTrue(StatementUtil.execute(expr, context));
    }

    public Expression optimize() {
        return expr instanceof DirectValue
                ? new DirectValue(ALU.not(((DirectValue) expr).value), line, column)
                : this;
    }
}
