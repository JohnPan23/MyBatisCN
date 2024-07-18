/**
 * Copyright 2009-2016 the original author or authors.
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
package org.apache.ibatis.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 通过对注解背景知识的讲解，我们对于注解类中的含义已经有了清晰的了解。接下来以 MyBatis中最常用的 Param注解为例，介绍它如何发挥作用。
 * 使用时，只需在相关参数上使用该注解对参数进行命名，即可在映射文件中引用该参数。如代码7-6所示，我们使用该注解将 userId属性命名为 id。
 * 这样，我们便可以在 Mapper中引用 id所指代的变量，如代码7-7所示。
 * <p>
 * 接下来我们着重分析该注解是如何生效的。
 * 借助于开发工具，我们在 ParamNameResolver的构造方法中找到了 MyBatis对 Param的引用。相关源码如代码7-8所示。
 * 在代码7-8 中，首先使用语句“final Annotation[][] paramAnnotations=method.getParameterAnnotations（）”得到了目标方法的所有参数的注解。假设我们定义如代码7-9所示的方法，则会在 paramAnnotations中得到如图7-1所示的结果。
 * 然后在分析每个参数时，循环遍历它的每个注解并使用“annotation instanceof Param”判断当前注解是否为“Param”注解。如果当前注解为“Param”注解，则会使用“（（Param） annotation）.value（）”操作获取该注解的 value值作为参数的名称。
 * 这样，我们对 Integer userId参数使用了@Param（"id"）后，“id”便成了实参的名称，因此能够使用“id”索引到对应的实参。
 */
@Documented // 表明该注解会保留在API文档中
@Retention(RetentionPolicy.RUNTIME) // 表明注解会保留到运行阶段
@Target(ElementType.PARAMETER) // 表明注解可以应用在参数上
public @interface Param {
    String value(); // 整个注解只有一个属性，名为value
}
