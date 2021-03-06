// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.lang.method;

import java.lang.reflect.Array;
import webit.script.InternalContext;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.lang.MethodDeclare;
import webit.script.util.StringUtil;

/**
 *
 * @author zqq90
 */
public class NativeNewArrayDeclare implements MethodDeclare {

    private final Class componentType;

    public NativeNewArrayDeclare(Class componentType) {
        this.componentType = componentType;
    }

    @Override
    public Object invoke(final InternalContext context, final Object[] args) {
        final int len;
        if (args != null && args.length > 0) {
            Object lenObject;
            if ((lenObject = args[0]) instanceof Number) {
                if ((len = ((Number) lenObject).intValue()) < 0) {
                    throw new ScriptRuntimeException(StringUtil.concat("must given a nonnegative number as array's length: ", len));
                }
            } else {
                throw new ScriptRuntimeException(StringUtil.concatObjectClass("must given a number as array's length, but get a: ", lenObject));
            }
        } else {
            len = 0;
        }

        return Array.newInstance(componentType, len);
    }
}
