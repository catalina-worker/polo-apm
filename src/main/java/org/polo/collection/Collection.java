package org.polo.collection;

import java.lang.instrument.Instrumentation;

/**
 * 组件接口
 */
public interface Collection {

    void register(Instrumentation instrumentation);
}
