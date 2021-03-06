// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import webit.script.InternalContext;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Statement;
import webit.script.util.ALU;
import webit.script.util.StatementUtil;

/**
 *
 * @author zqq90
 */
public final class WhileNoLoops extends Statement {

    private final Expression whileExpr;
    private final int indexer;
    private final Statement[] statements;

    public WhileNoLoops(Expression whileExpr, int indexer, Statement[] statements, int line, int column) {
        super(line, column);
        this.whileExpr = whileExpr;
        this.indexer = indexer;
        this.statements = statements;
    }

    @Override
    public Object execute(final InternalContext context) {
        final int preIndex = context.indexer;
        context.indexer = indexer;
        final Statement[] stats = this.statements;
        while (ALU.isTrue(whileExpr.execute(context))) {
            StatementUtil.executeInverted(stats, context);
        }
        context.indexer = preIndex;
        return null;
    }
}
