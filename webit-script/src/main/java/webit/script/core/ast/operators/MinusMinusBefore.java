// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.core.ast.operators;

import webit.script.InternalContext;
import webit.script.core.ast.Expression;
import webit.script.core.ast.ResetableValueExpression;
import webit.script.util.ALU;
import webit.script.util.StatementUtil;

/**
 *
 * @author zqq90
 */
public final class MinusMinusBefore extends Expression {

    private final ResetableValueExpression expr;

    public MinusMinusBefore(ResetableValueExpression expr, int line, int column) {
        super(line, column);
        this.expr = expr;
    }

    @Override
    public Object execute(final InternalContext context) {
        try {
            final ResetableValueExpression resetable = this.expr;
            return resetable.setValue(context, ALU.minusOne(
                    resetable.execute(context)));
        } catch (Exception e) {
            throw StatementUtil.castToScriptRuntimeException(e, this);
        }
    }
}
