// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.resolvers.impl;

import webit.script.exceptions.ScriptRuntimeException;
import webit.script.io.Out;
import webit.script.lang.InternalVoid;
import webit.script.resolvers.GetResolver;
import webit.script.resolvers.OutResolver;
import webit.script.resolvers.SetResolver;

/**
 *
 * @author zqq90
 */
public class InternalVoidResolver implements GetResolver, SetResolver, OutResolver {

    @Override
    public Object get(Object object, Object property) {
        throw new ScriptRuntimeException("'Void' type has no property.");
    }

    @Override
    public Class getMatchClass() {
        return InternalVoid.class;
    }

    @Override
    public void set(Object object, Object property, Object value) {
        throw new ScriptRuntimeException("'Void' type has no property.");
    }

    @Override
    public void render(Out out, Object bean) {
        //render nothing
    }

}
