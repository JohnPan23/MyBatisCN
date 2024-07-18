/**
 * annotations包和 lang包中存放的全是注解类，因此我们将其合并讲解。
 * 要阅读 annotations包和 lang包中的各个注解类，最重要的是弄清楚 Java中注解的声明方式。只要把 Java注解的语法和原理理解透彻，便无须再逐一阅读各个注解类的源码，只需在每个注解类使用的地方对它们稍加关注即可。
 * 因此，本节的重点在于学习 Java注解的相关知识，并在此基础上选取一个注解对其使用和生效原理进行介绍。
 * <p>
 * Java注解是一种标注。Java中的类、方法、变量、参数、包等均可以被注解标注从而添加额外的信息。相比于直接修改代码的硬编码方式，基于注解的这种松耦合的信息添加方式更受欢迎。
 * 本节中，我们将对注解的声明方式、注解的原理进行详细的介绍。
 * <p>
 * 随便打开一个注解类，会发现它们中也包含注解。例如，代码7-1所示的@Param注解中就存在@Documented、@Retention、@Target等注解。这些用来注解其他注解的注解，称为元注解。
 *              @Documented // 表明该注解会保留在API文档中
 *              @Retention(RetentionPolicy.RUNTIME) // 表明注解会保留到运行阶段
 *              @Target(ElementType.PARAMETER) // 表明注解可以应用在参数上
 *              public @interface Param {
 *                  String value(); // 整个注解只有一个属性，名为value
 *              }
 * 元注解一共有五个，分别是@Target、@Retention、@Documented、@Inherited、@Repeatable，下面分别进行介绍。
 * @Target 注解用来声明注解可以用在什么地方，它的值需要从枚举类 ElementType 中选取。ElementType的枚举值及其含义如下。
 * · TYPE：类、接口、注解、枚举；
 * · FIELD：字段；
 * · METHOD：方法；
 * · PARAMETER：参数；
 * · CONSTRUCTOR：构造方法；
 * · LOCAL_VARIABLE：本地变量；
 * · ANNOTATION_TYPE：注解；
 * · PACKAGE：包；
 * · TYPE_PARAMETER：类型参数；
 * · TYPE_USE：类型使用。
 * 例如，要声明一个注解只能声明在参数上，可以如代码7-2所示进行设置。
 *  @Target(ElementType.PARAMETER)
 * <p>
 * @Retention注解用来声明注解的生命周期，即表明注解会被保留到哪一阶段。它的值需要从枚举类 RetentionPolicy中选取。RetentionPolicy的枚举值如下。
 * · SOURCE：保留到源代码阶段。这一类注解一般留给编译器使用，在编译时会被擦除。
 * · CLASS：保留到类文件阶段。这是默认的生命周期，注解会保留到类文件阶段，但是 JVM运行时不包含这些信息。
 * · RUNTIME：保留到 JVM运行阶段。如果想在程序运行时获得注解，则需要保留在这一阶段。
 * @Documented 不需要设置具体的值。如果一个注解被@Documented 标注，则该注解会在 javadoc中生成。
 * @Inherited 不需要设置具体的值。如果一个注解被@Inherited 标注，表明允许子类继承父类的该注解（可以从父类继承该注解，但是不能从接口继承该注解）。
 * @Repeatable是 JDK 8中新加入的。如果一个注解被@Repeatable标注，则该注解可以在同一个地方被重复使用多次。用@Repeatable 来修饰注解时需要指明一个接受重复注解的容器。
 * <p>
 * 自定义一个注解非常简单，需要使用元注解进行一些声明，然后就可以定义注解中的属性。代码7-4给出了一个自定义注解的示例。
 * 在定义注解的属性时，是使用方法的形式来定义的，即属性名就是方法名。每个属性都可以定义默认值。如果不为属性指定默认值，则在使用时必须赋值。
 * 属性的类型很灵活，可以是字符串、基本类型、枚举类型、注解、Class类型，以及以上类型的一维数组。
 * 如果一个注解只有一个名为 value 的属性，则在使用过程中为该属性赋值时可以省略属性名。
 */
package org.apache.ibatis.annotations;

