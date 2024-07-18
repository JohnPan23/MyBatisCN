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
 * <p>
 * Unused.
 */
/**
 * Unused.
 *
 * reflection 包下的 invoker 子包是执行器子包，该子包中的类能够基于反射实现对象方法的调用和对象属性的读写。
 * 学习了反射的基本概念之后，我们知道通过反射可以很方便地调用对象的方法和读写方法的属性。而 invoker子包则进一步封装和简化了这些操作。
 * invoker 子包有一个 Invoker 接口和三个实现，Invoker 接口及其实现类类图如图6-6所示。
 *
 *
 * Invoker接口的三个实现分别用来处理三种不同情况。
 * · GetFieldInvoker：负责对象属性的读操作；
 * · SetFieldInvoker：负责对象属性的写操作；
 * · MethodInvoker：负责对象其他方法的操作。
 */
package org.apache.ibatis.reflection.invoker;
