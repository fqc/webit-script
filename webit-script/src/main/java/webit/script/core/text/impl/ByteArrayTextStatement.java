// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.core.text.impl;

import webit.script.InternalContext;
import webit.script.core.ast.Statement;

/**
 *
 * @author zqq90
 */
public final class ByteArrayTextStatement extends Statement {

    private final byte[] bytes;

    public ByteArrayTextStatement(byte[] bytes, int line, int column) {
        super(line, column);
        this.bytes = bytes;
    }

    @Override
    public Object execute(final InternalContext context) {
        context.outNotNull(bytes);
        return null;
    }
}
