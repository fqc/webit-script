// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.core.ast.operators;

import webit.script.InternalContext;
import webit.script.core.ast.BinaryOperator;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Optimizable;
import webit.script.core.ast.expressions.DirectValue;
import webit.script.util.ALU;

/**
 *
 * @author zqq90
 */
public final class NotEqual extends BinaryOperator implements Optimizable {

    public NotEqual(Expression leftExpr, Expression rightExpr, int line, int column) {
        super(leftExpr, rightExpr, line, column);
    }

    @Override
    public Object execute(final InternalContext context) {
        return !ALU.isEqual(leftExpr.execute(context), rightExpr.execute(context));
    }

    @Override
    public Expression optimize() {
        return (leftExpr instanceof DirectValue && rightExpr instanceof DirectValue)
                ? new DirectValue(!ALU.isEqual(((DirectValue) leftExpr).value, ((DirectValue) rightExpr).value), line, column)
                : this;
    }
}
