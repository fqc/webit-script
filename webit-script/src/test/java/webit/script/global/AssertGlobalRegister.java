// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.global;

/**
 *
 * @author zqq90 <zqq_90@163.com>
 */
public class AssertGlobalRegister implements GlobalRegister {

    public static AssertGlobalRegister instance;

    public AssertGlobalRegister() {
        instance = this;
    }

    private DefaultGlobalManager manager;
    private final static String ASSERT_COUNT = "assertCount";

    public void regist(DefaultGlobalManager manager) {
        this.manager = manager;
        manager.setGlobal(ASSERT_COUNT, 0);
        manager.setGlobal("assertTrue", null);
        manager.setGlobal("assertFalse", null);
        manager.setGlobal("assertEquals", null);
        manager.setGlobal("assertNotEquals", null);
        manager.setGlobal("assertNull", null);
        manager.setGlobal("assertNotNull", null);
        manager.setGlobal("assertSame", null);
        manager.setGlobal("assertNotSame", null);
        manager.setGlobal("assertArrayEquals", null);
        manager.setGlobal("assertArrayEquals_chars", null);
        
    }

    public void resetAssertCount() {
        this.manager.setGlobal(ASSERT_COUNT, 0);
    }

    public int getAssertCount() {
        return (Integer) this.manager.getGlobal(ASSERT_COUNT);
    }
}