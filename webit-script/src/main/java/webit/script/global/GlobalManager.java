// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.global;

/**
 *
 * @author zqq90 <zqq_90@163.com>
 */
public interface GlobalManager {

    int getVariantIndex(String name);

    Object getVariant(int index);

    void setVariant(int index, Object value);
    
    void commit();

    boolean hasConst(String name);

    Object getConst(String name);
}
