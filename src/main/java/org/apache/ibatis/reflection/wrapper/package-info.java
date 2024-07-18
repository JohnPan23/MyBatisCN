/**
 * reflection包下的 wrapper子包是对象包装器子包，该子包中的类使用装饰器模式对各种类型的对象（包括基本 Bean对象、集合对象、Map对象）进行进一步的封装，为其增加一些功能，使它们更易于使用。
 * <p>
 *        ObjectWrapperFactory
 *              ↑
 * DefaultObjectWrapperFactory     生产    ObjectWrapper
 *                                           ↗ ↖
 *                               BaseWrapper      CollectionWrapper
 *                                   ↗ ↖
 *                       BeanWrapper      MapWrapper
 * <p>
 * ObjectWrapperFactory 是对象包装器工厂的接口，DefaultObjectWrapperFactory 是它的默认实现。不过该默认实现中并没有实现任何功能。
 * MyBatis 也允许用户通过配置文件中的 objectWrapperFactory节点来注入新的 ObjectWrapperFactory。
 * <p>
 * ObjectWrapper接口是所有对象包装器的总接口。
 * <p>
 * 以 BeanWrapper为例，我们介绍一下包装器的实现。在介绍之前我们先了解 reflection包中的两个类：MetaObject类和 MetaClass类。
 * meta在中文中常译为元，在英文单词中作为开头有涵盖、超越、变换等多种含义。在这里，这三种含义都是存在的。例如MetaObject类中涵盖了对应的object类中的全部信息，并经过变化和拆解得到了一些更为细节的信息。
 * 因此，可以将MetaObject类理解为一个涵盖对象中更多细节信息和功能的类，称为元对象。同理，MetaClass类理解为元类。
 *
 *
 * <p>
 * BeanWrapper类有三个属性：
 * 1. metaClass是被包装对象所属类的元类。
 * 2. object是被包装对象。
 * 3. metaObject是被包装对象的元对象。（继承自父类BaseWrapper）
 * <p>
 * 通过对 BeanWrapper属性的了解，加上对 MetaObject类和 MetaClass类的简单介绍，可以得出结论：
 *          BeanWrapper中包含了一个 Bean的对象信息、类型信息，并提供了更多的一些功能。BeanWrapper中存在的方法有：
 *              · get：获得被包装对象某个属性的值；
 *              · set：设置被包装对象某个属性的值；
 *              · findProperty：找到对应的属性名称；
 *              · getGetterNames：获得所有的属性 get方法名称；
 *              · getSetterNames：获得所有的属性 set方法名称；
 *              · getSetterType：获得指定属性的 set方法的类型；
 *              · getGetterType：获得指定属性的 get方法的类型；
 *              · hasSetter：判断某个属性是否有对应的 set方法；
 *              · hasGetter：判断某个属性是否有对应的 get方法；
 *              · instantiatePropertyValue：实例化某个属性的值。
 * 因此，一个 Bean经过 BeanWrapper封装后，就可以暴露出大量的易用方法，从而可以简单地实现对其属性、方法的操作。
 * <p>
 * 同理，wrapper子包下的 CollectionWrapper、MapWrapper与 BeanWrapper一样，它们分别负责包装 Collection和 Map类型，从而使它们暴露出更多的易用方法。
 * BaseWrapper作为 BeanWrapper和 MapWrapper的父类，为这两个类提供一些共用的基础方法。
 * 源码阅读时，遇到同类型的类（一般具有类似的名称、功能），可以重点阅读其中的一个类。当这个类的源码阅读清楚时，同类型类的源码也就清晰了。
 */
package org.apache.ibatis.reflection.wrapper;
