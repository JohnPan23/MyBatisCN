/**
 * reflection包中最为核心的类就是 Reflector类。图6-8给出了与 Reflector类最为密切的几个类的类图。
 *
 *                  ReflectorFactory
 *                          ↑
 *           DefaultReflectorFactory   生产   Reflector
 *
 * Reflector 类负责对一个类进行反射解析，并将解析后的结果在属性中存储起来。该类包含的属性如代码6-20所示，各个属性的含义已经通过注释进行了标注。
 * Reflector 类将一个类反射解析后，会将该类的属性、方法等一一归类放到以上的各个属性中。因此 Reflector类完成了主要的反射解析工作，这也是我们将其称为反射核心类的原因。
 *
 * reflection包中的其他类则多是在其反射结果的基础上进一步包装的，使整个反射功能更易用。
 *
 * Reflector类反射解析一个类的过程是由构造函数触发的，逻辑非常清晰。Reflector类的构造函数如代码6-21所示。
 * 具体到每个子方法，其逻辑比较简单。下面以其中的 addGetMethods 方法为例进行介绍。addGetMethods方法的功能是分析参数中传入的类，将类中的 get方法添加到 getMethods方法中。其带注释的源码如代码6-22所示。
 * 其中的 conflictingGetters变量是一个 Map，它的 key是属性名称，value是该属性可能的get方法的列表。但是，最终每个属性真正的 get方法应该只有一个。resolveGetterConflicts方法负责尝试找出该属性真正的 get方法，该方法源码并不复杂，读者可以自行阅读。
 *
 * ReflectorFactory是 Reflector的工厂接口，而 DefaultReflectorFactory是该工厂接口的默认实现。下面直接以 DefaultReflectorFactory为例，介绍 Reflector工厂。
 * DefaultReflectorFactory 中最核心的方法就是用来生成一个类的 Reflector 对象的findForClass方法，如代码6-23所示。
 *
 * reflector包中存在许多包装类，前面的wrapper子包就存在许多这种包装类。
 * 整个包装类中除了原始对象本身外，还包装了对象包装器、对象工厂、对象包装器工厂、反射工厂等。因此，只要使用 MetaObject对一个对象进行包装，包装类中就具有大量的辅助类，便于进行各种反射操作。
 */
package org.apache.ibatis.reflection;
