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
package org.apache.ibatis.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;

/**
 * ExceptionUtil 是一个异常工具类，它提供一个拆包异常的工具方法 unwrapThrowable。
 * 该方法将 InvocationTargetException 和 UndeclaredThrowableException 这两类异常进行拆包，得到其中包含的真正的异常。
 * <p>
 * unwrapThrowable 方法的结构非常简单，但是我们需要思考它存在的意义：为什么需要给 InvocationTargetException和 UndeclaredThrowableException这两个类拆包？这两个类为什么要把其他异常包装起来？
 * 很多时候读懂源码的实现并不难，但是一定要多思考源码为什么这么写。只有这样，才能在源码阅读的过程中有更多的收获。
 * 接下来通过了解 InvocationTargetException和 UndeclaredThrowableException这两个类来解答上述疑问。
 * InvocationTargetException为必检异常，UndeclaredThrowableException为免检的运行时异常。它们都不属于 MyBatis，而是来自 java.lang.reflect包。
 * 反射操作中，代理类通过反射调用目标类的方法时，目标类的方法可能抛出异常。反射可以调用各种目标方法，因此目标方法抛出的异常是多种多样无法确定的。这意味着反射操作可能抛出一个任意类型的异常。
 * 可以用 Throwable 去接收这个异常，但这无疑太过宽泛。
 * <p>
 * InvocationTargetException就是为解决这个问题而设计的，当反射操作的目标方法中出现异常时，都统一包装成一个必检异常 InvocationTargetException。
 * InvocationTargetException内部的 target 属性则保存了原始的异常。这样一来，便使得反射操作中的异常更易管理。
 * <p>
 * 讲完了 InvocationTargetException，下面再讲讲 UndeclaredThrowableException。
 * 根据 Java的继承原则，我们知道：如果子类中要重写父类中的方法，那么子类方法中抛出的必检异常必须是父类方法中声明过的类型。
 * 在建立目标类的代理类时，通常是建立了目标类接口的子类或者目标类的子类（10.1.3节和 22.1.1节会详细介绍动态代理）。因此，将 Java的继承原则放在代理类和被代理类上可以演化为：
 * · 如果代理类和被代理类实现了共同的接口，则代理类方法中抛出的必检异常必须是在共同接口中声明过的；
 * · 如果代理类是被代理类的子类，则代理类方法中抛出的必检异常必须是在被代理类的方法中声明过的。
 * 可是在代理类中难免会在执行某些方法时抛出一些共同接口或者父类方法中没有声明的必检异常，那这个问题该怎么解决呢？如果不抛出，则它是必检异常，必须抛出；如果抛出，则父接口或父类中没有声明该必检异常，不能抛出。
 * 答案就是这些必检异常会被包装为免检异常 UndeclaredThrowableException 后抛出。所以说 UndeclaredThrowableException 也是一个包装了其他异常的异常，UndeclaredThrowableException 类带注释的源码如代码6-27 所示，其包装的异常在undeclaredThrowable属性中。
 * <p>
 * 有一个简单的例子可以恰好同时涉及 InvocationTargetException 和 Undeclared-hrowablexception 这两个异常。就是代理类在进行反射操作时发生异常，于是异常被包装成 InvocationTargetException。InvocationTargetException显然没有在共同接口或者父类方法中声明过，于是又被包装成了UndeclaredThrowableException。这样，真正的异常就被包装了两层。这也是为什么在ExceptionUtil的unwrapThrowable方法中存在一个“while （true）”死循环，用来持续拆包。
 * 总之，InvocationTargetException 和 UndeclaredThrowableException 这两个类都是异常包装类，需要拆包后才能得到真正的异常类。而 ExceptionUtil的 unwrapThrowable方法就可以完成该拆包工作。
 */
public class ExceptionUtil {

    private ExceptionUtil() {
        // Prevent Instantiation
    }

    /**
     * 拆解InvocationTargetException和UndeclaredThrowableException异常的包装，从而得到被包装的真正异常
     * @param wrapped 包装后的异常
     * @return 拆解出的被包装异常
     */
    public static Throwable unwrapThrowable(Throwable wrapped) {
        // 该变量用以存放拆包得到的异常
        Throwable unwrapped = wrapped;
        while (true) {
            if (unwrapped instanceof InvocationTargetException) {
                // 拆包获得内部异常
                unwrapped = ((InvocationTargetException) unwrapped).getTargetException();
            } else if (unwrapped instanceof UndeclaredThrowableException) {
                // 拆包获得内部异常
                unwrapped = ((UndeclaredThrowableException) unwrapped).getUndeclaredThrowable();
            } else {
                // 该异常无需拆包
                return unwrapped;
            }
        }
    }

}
