// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.

package webit.script.test.tmpls;

import java.util.HashSet;
import java.util.Set;
import static org.junit.Assert.*;
import org.junit.Test;
import webit.script.EngineManager;
import webit.script.InternalContext;
import webit.script.Template;
import webit.script.core.ast.Statement;
import webit.script.debug.BreakPointListener;
import webit.script.exceptions.ResourceNotFoundException;
import webit.script.io.impl.DiscardOut;
import webit.script.util.KeyValuesUtil;

/**
 *
 * @author zqq90
 */
public class DebugTest {
    
    private final Set<Object> labelCache = new HashSet<>();
    private int pointCount = 0;
    
    @Test
    public void test() throws ResourceNotFoundException {
        Template template = EngineManager.getTemplate("/debug.wit");
        
        template.merge(new DiscardOut());
        
        labelCache.clear();
        pointCount = 0;
        template.debug(KeyValuesUtil.EMPTY_KEY_VALUES, new DiscardOut(), new BreakPointListener() {

            @Override
            public void onBreak(Object label, InternalContext context, Statement statement, Object result) {
                labelCache.add(label);
                pointCount ++;
            }
        });
        
        assertEquals(18, pointCount);
        assertTrue(labelCache.contains(null));
        assertTrue(labelCache.contains("p1"));
        assertTrue(labelCache.contains("p2"));
        assertTrue(labelCache.contains("p3"));
        assertTrue(labelCache.contains("p4"));
        
    }
    
}
