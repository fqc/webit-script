// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.servlet.resolvers;

import webit.script.resolvers.GetResolver;
import webit.script.servlet.HttpServletRequestHeaders;

/**
 *
 * @author zqq90
 */
public class HttpServletRequestHeadersResolver implements GetResolver{

    @Override
    public Object get(Object bean, Object property) {
        return ((HttpServletRequestHeaders) bean).get(property.toString());
    }

    @Override
    public Class<?> getMatchClass() {
        return HttpServletRequestHeaders.class;
    }
}
