// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.operators;

import webit.script.Context;
import webit.script.core.ast.AbstractExpression;
import webit.script.core.ast.Expression;
import webit.script.util.ALU;
import webit.script.util.StatementUtil;

/**
 *
 * @author Zqq
 */
public final class IfOperator extends AbstractExpression {

    private final Expression ifExpr;
    private final Expression leftValueExpr;
    private final Expression rightValueExpr;

    public IfOperator(Expression ifExpr, Expression leftValueExpr, Expression rightValueExpr, int line, int column) {
        super(line, column);
        this.ifExpr = ifExpr;
        this.leftValueExpr = leftValueExpr;
        this.rightValueExpr = rightValueExpr;
    }

    public Object execute(final Context context) {
        return StatementUtil.execute(
                ALU.isTrue(StatementUtil.execute(ifExpr, context))
                ? leftValueExpr
                : rightValueExpr,
                context);

    }
}
