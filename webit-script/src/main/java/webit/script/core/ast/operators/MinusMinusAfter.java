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
public final class MinusMinusAfter extends Expression {

    private final ResetableValueExpression expr;

    public MinusMinusAfter(ResetableValueExpression expr, int line, int column) {
        super(line, column);
        this.expr = expr;
    }

    @Override
    public Object execute(final InternalContext context) {
        final Object value;
        final ResetableValueExpression resetable = this.expr;
        try {
            resetable.setValue(context, ALU.minusOne(
                    value = resetable.execute(context)));
            return value;
        } catch (Exception e) {
            throw StatementUtil.castToScriptRuntimeException(e, this);
        }
    }
}
