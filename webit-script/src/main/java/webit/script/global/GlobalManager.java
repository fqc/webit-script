// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.global;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import webit.script.Init;
import webit.script.lang.Bag;

/**
 *
 * @author zqq90
 */
public class GlobalManager {

    private final Map<String, Object> constMap;
    private final Map<String, Object> driftedGlobalMap;
    private final Map<String, Integer> globalIndexer;
    private Object[] globalContext;

    //settings
    private transient GlobalRegister[] registers;

    public GlobalManager() {
        this.constMap = new HashMap<>();
        this.driftedGlobalMap = new HashMap<>();
        this.globalIndexer = new HashMap<>();
    }

    @Init
    public void init() {
        if (registers != null) {
            try {
                for (GlobalRegister register : registers) {
                    register.regist(this);
                    this.commit();
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        commit();
    }

    public void clear() {
        this.driftedGlobalMap.clear();
        this.globalIndexer.clear();
        this.constMap.clear();
        Object[] myGlobalContext = this.globalContext;
        if (myGlobalContext != null) {
            Arrays.fill(myGlobalContext, null);
        }
        init();
    }

    public void commit() {
        if (this.driftedGlobalMap.isEmpty()) {
            return;
        }
        final int oldSize;
        final Object[] oldGlobalContext = this.globalContext;
        oldSize = oldGlobalContext != null ? oldGlobalContext.length : 0;

        final Object[] newGlobalContext = this.globalContext
                = new Object[oldSize + this.driftedGlobalMap.size()];

        if (oldSize > 0) {
            //Copy old data
            System.arraycopy(oldGlobalContext, 0, newGlobalContext, 0, oldSize);
        }

        int i = oldSize;
        for (Map.Entry<String, Object> entry : this.driftedGlobalMap.entrySet()) {
            newGlobalContext[i] = entry.getValue();
            this.globalIndexer.put(entry.getKey(), i);
            i++;
        }
        this.driftedGlobalMap.clear();
    }

    public void setConst(String key, Object value) {
        this.constMap.put(key, value);
    }

    public void setGlobal(String key, Object value) {
        int index;
        if ((index = this.getGlobalIndex(key)) >= 0) {
            this.setGlobal(index, value);
        } else {
            this.driftedGlobalMap.put(key, value);
        }
    }

    public int getGlobalIndex(String name) {
        Integer index;
        return (index = globalIndexer.get(name)) != null ? index : -1;
    }

    public Object getGlobal(String key) {
        int index;
        if ((index = this.getGlobalIndex(key)) >= 0) {
            return this.getGlobal(index);
        } else {
            return this.driftedGlobalMap.get(key);
        }
    }

    public Object getGlobal(int index) {
        return globalContext[index];
    }

    public void setGlobal(int index, Object value) {
        this.globalContext[index] = value;
    }

    public boolean hasConst(String name) {
        return this.constMap.containsKey(name);
    }

    public Object getConst(String name) {
        return this.constMap.get(name);
    }

    public Bag getConstBag() {
        return new ConstBag(this);
    }

    public Bag getGlobalBag() {
        return new GlobalBag(this);
    }

    private static class ConstBag implements Bag {

        final GlobalManager manager;

        ConstBag(GlobalManager manager) {
            this.manager = manager;
        }

        @Override
        public Object get(Object key) {
            return this.manager.getConst(String.valueOf(key));
        }

        @Override
        public void set(Object key, Object value) {
            this.manager.setConst(String.valueOf(key), value);
        }
    }

    private static class GlobalBag implements Bag {

        final GlobalManager manager;

        GlobalBag(GlobalManager manager) {
            this.manager = manager;
        }

        @Override
        public Object get(Object key) {
            return this.manager.getGlobal(String.valueOf(key));
        }

        @Override
        public void set(Object key, Object value) {
            this.manager.setGlobal(String.valueOf(key), value);
        }
    }
}
