// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import java.util.Map;
import webit.script.Context;
import webit.script.core.VariantIndexer;
import webit.script.core.ast.AbstractStatement;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Statement;
import webit.script.core.runtime.VariantStack;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.util.CollectionUtil;
import webit.script.util.StatementUtil;
import webit.script.util.collection.Iter;

/**
 *
 * @author Zqq
 */
public final class ForMapNoLoops extends AbstractStatement {

    private final Expression mapExpr;
    private final VariantIndexer varIndexer;
    private final Statement[] statements;
    private final Statement elseStatement;

    public ForMapNoLoops( Expression mapExpr, VariantIndexer varIndexer, Statement[] statements, Statement elseStatement, int line, int column) {
        super(line, column);
        this.mapExpr = mapExpr;
        this.varIndexer = varIndexer;
        this.statements = statements;
        this.elseStatement = elseStatement;
    }

    @SuppressWarnings("unchecked")
    public Object execute(final Context context) {
        final Object object = StatementUtil.execute(mapExpr, context);
        final Iter<Map.Entry> iter;
        if (object != null) {
            if (object instanceof Map) {
                iter = CollectionUtil.toIter(((Map) object).entrySet());
            } else {
                throw new ScriptRuntimeException("Not a instance of java.util.Map");
            }
        } else {
            iter = null;
        }
        if (iter != null && iter.hasNext()) {
            Map.Entry entry;
            final Statement[] statements = this.statements;
            final VariantStack vars;
            (vars = context.vars).push(varIndexer);
            vars.set(0, iter);
            do {
                entry = iter.next();
                vars.resetForForMap(entry.getKey(), entry.getValue());
                StatementUtil.executeInverted(statements, context);
            } while (iter.hasNext());
            vars.pop();
            return null;
        } else if (elseStatement != null) {
            StatementUtil.execute(elseStatement, context);
        }
        return null;
    }
}
