/**
 * Copyright 2009-2015 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ibatis.reflection.invoker;

import java.lang.reflect.InvocationTargetException;

/**
 * 我们先阅读 Invoker接口的源码，它只定义了以下两个抽象方法。
 * <p>
 * invoke方法，即执行方法。该方法负责完成对象方法的调用和对象属性的读写。在三个实现类中，分别是属性读取操作、属性赋值操作、方法触发操作。
 * <p>
 * getType方法，用来获取类型。它对于 GetFieldInvoker和 SetFieldInvoker的含义也是明确的，即获得目标属性的类型。可 MethodInvoker对应的是一个方法，getType方法对于 MethodInvoker类型而言的意义是什么呢？
 * 阅读 MethodInvoker中的 getType方法，我们发现该方法直接返回了 MethodInvoker中的 type属性。该 type属性的定义如代码6-13所示。
 */
public interface Invoker {
    /**
     *
     * 1. GetFieldInvoker的实现是，执行反射属性Field.get()
     * 2. SetFieldInvoker的实现是，执行反射属性Field.set()
     * 3. MethodInvoker的实现是，执行反射方法Method.invoke()
     */
    Object invoke(Object target, Object[] args)
            throws IllegalAccessException, InvocationTargetException;

    Class<?> getType();
}
