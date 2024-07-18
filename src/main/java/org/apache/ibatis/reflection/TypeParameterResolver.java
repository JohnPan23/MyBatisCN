/**
 * Copyright 2009-2019 the original author or authors.
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

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;

/**
 * TypeParameterResolver是泛型参数解析器。在阅读它的源码之前我们先弄清一个问题：它的功能是什么？很多情况下，弄清一个类的功能对阅读其源码十分必要。
 * <p>
 * 假设有 User和 Student两个类，分别如代码6-31和代码6-32所示。
 * 请问：Student类中的 getInfo方法（继承自父类 User）的输出参数类型是什么？
 * 答案很简单，是“List＜Number＞”。但是得出这个答案的过程却涉及 User 和 Student两个类。首先通过 User 类确定 getInfo 方法的输出结果是“List＜T＞”，然后通过 Student类得知“T”被设置为“Number”。因此，Student 类中的 getInfo 方法的输出参数是“List＜Number＞”。
 * <p>
 * TypeParameterResolver 类的功能就是完成上述分析过程，帮助 MyBatis 推断出属性、返回值、输入参数中泛型的具体类型。例如，通过代码6-33 所示的调用，TypeParameterResolver便分析出 User类中的 getInfo方法的输出参数是“List＜Object＞”，Student类中的 getInfo方法的输出参数是“List＜Number＞”。
 * <p>
 * 了解了 TypeParameterResolver类的功能后，下面来查看它的源码。它对外提供以下三个方法。
 * · resolveFieldType：解析属性的泛型；
 * · resolveReturnType：解析方法返回值的泛型；
 * · resolveParamTypes：解析方法输入参数的泛型。
 * 上述这三个方法都只是将要解析的变量从属性、方法返回值、方法输入参数中找出来。变量的泛型解析才是最核心的工作。以代码6-34所示的 resolveParamTypes方法为例，该方法将变量从方法输入参数中找出后，对每个变量都调用了 resolveType 方法。因此，resolveType是最重要的方法。
 * <p>
 * resolveType方法根据目标类型的不同调用不同的子方法进行处理。
 * 在分析 resolveType方法的源码之前，有必要再强调一下 resolveType的输入参数，以防大家混淆。以上文中提到的“Student类中的 getInfo方法（继承自父类 User）的输出参数类型是什么？”这一问题为例，则：
 * · type：指要分析的字段或者参数的类型。这里我们要分析的是 getInfo的输出参数，即“List＜T＞”的类型。
 * · srcType：指要分析的字段或者参数所属的类。我们这里要分析的是 Student类中的getInfo方法，故所属的类是 Student类。
 * · declaringClass：指定义要分析的字段或者参数的类。getInfo 方法在 User 类中被定义，故这里是 User类。
 * resolveType方法的带注释源码如代码6-35所示。
 * <p>
 * resolveType 根据不同的参数类型调用了不同的子方法进行处理。我们直接以“List＜T＞”对应的 resolveParameterizedType子方法为例进行分析，而该子方法也是所有子方法中最为复杂的一个。
 * “List＜T＞”作为参数化类型会触发 resolveParameterizedType 方法进行处理。resolveParameterizedType方法的带注释源码如代码6-36所示。
 * <p>
 * 对于 resolveParameterizedType方法中的各种分支情况我们已经在代码6-36中通过注释进行了详细说明。在示例中，parameterizedType 为“List＜T＞”，因此会继续调用resolveTypeVar方法对泛型变量“T”进行进一步的解析。
 * resolveTypeVar方法的带注释源码如代码6-37所示。resolveTypeVar方法会尝试通过继承关系等确定泛型变量的具体结果。
 * <p>
 * 这样，我们以“Student类中的 getInfo方法（继承自父类 User）的输出参数类型是什么？”这一问题为主线，对 TypeParameterResolver的源码进行了阅读。
 * 在代码6-35 所示的 resolveType 方法中，会根据变量的类型调用 resolveTypeVar、resolveParameterizedType、resolveGenericArrayType三个方法进行解析。而在本节中，我们通过示例“List＜T＞”对 resolveTypeVar（代码6-37）、resolveParameterizedType（代码6-36）的源码进行了阅读。而 resolveGenericArrayType方法的带注释源码如代码6-38所示。
 * <p>
 * resolveGenericArrayType方法并不复杂，只是根据元素类型又调用了其他几个方法。
 * 这样，我们以断点调试法为基础，以“List＜T＞”类型的泛型变量为用例，通过以点带面的方式完成了 TypeParameterResolver类的源码阅读。这种以用例为主线的源码阅读方法能帮助我们排除很多干扰从而专注于一条逻辑主线。而等这条逻辑主线的源码被阅读清楚时，其他逻辑主线往往也会迎刃而解。
 */
public class TypeParameterResolver {

    /**
     * @return The field type as {@link Type}. If it has type parameters in the declaration,<br>
     * they will be resolved to the actual runtime {@link Type}s.
     */
    public static Type resolveFieldType(Field field, Type srcType) {
        // 返回属性的类型，如果是泛型，则为这次的实际类型
        Type fieldType = field.getGenericType();
        // 返回其声明类型
        Class<?> declaringClass = field.getDeclaringClass();
        return resolveType(fieldType, srcType, declaringClass);
    }

    /**
     * @return The return type of the method as {@link Type}. If it has type parameters in the declaration,<br>
     * they will be resolved to the actual runtime {@link Type}s.
     */
    public static Type resolveReturnType(Method method, Type srcType) {
        Type returnType = method.getGenericReturnType();
        Class<?> declaringClass = method.getDeclaringClass();
        return resolveType(returnType, srcType, declaringClass);
    }

    /**
     * @return The parameter types of the method as an array of {@link Type}s. If they have type parameters in the declaration,<br>
     *         they will be resolved to the actual runtime {@link Type}s.
     */

    /**
     * 解析方法入参
     *
     * @param method  目标方法
     * @param srcType 目标方法所属的类
     * @return 解析结果
     */
    public static Type[] resolveParamTypes(Method method, Type srcType) {
        // 取出方法的所有入参
        Type[] paramTypes = method.getGenericParameterTypes();
        // 定义目标方法的类或接口
        Class<?> declaringClass = method.getDeclaringClass();
        // 解析结果
        Type[] result = new Type[paramTypes.length];
        for (int i = 0; i < paramTypes.length; i++) {
            // 对每个入参依次调用resolveType方法
            result[i] = resolveType(paramTypes[i], srcType, declaringClass);
        }
        return result;
    }

    /**
     * 解析变量的实际类型
     *
     * @param type           变量的类型
     * @param srcType        变量所属于的类
     * @param declaringClass 定义变量的类
     * @return 解析结果
     */
    private static Type resolveType(Type type, Type srcType, Class<?> declaringClass) {
        if (type instanceof TypeVariable) { // 如果是类型变量，例如“Map<K，V>”中的“K”、“V”就是类型变量。
            return resolveTypeVar((TypeVariable<?>) type, srcType, declaringClass);
        } else if (type instanceof ParameterizedType) { // 如果是参数化类型，例如“Collection<String>”就是参数化的类型。
            return resolveParameterizedType((ParameterizedType) type, srcType, declaringClass);
        } else if (type instanceof GenericArrayType) { // 如果是包含ParameterizedType或者TypeVariable元素的列表
            return resolveGenericArrayType((GenericArrayType) type, srcType, declaringClass);
        } else {
            return type;
        }
    }

    /**
     * 解析泛型列表的实际类型
     *
     * @param genericArrayType 泛型列表变量类型
     * @param srcType          变量所属于的类
     * @param declaringClass   定义变量的类
     * @return 解析结果
     */
    private static Type resolveGenericArrayType(GenericArrayType genericArrayType, Type srcType, Class<?> declaringClass) {
        Type componentType = genericArrayType.getGenericComponentType();
        Type resolvedComponentType = null;
        if (componentType instanceof TypeVariable) { // 元素类型是类变量。例如genericArrayType为T[]则属于这种情况
            resolvedComponentType = resolveTypeVar((TypeVariable<?>) componentType, srcType, declaringClass);
        } else if (componentType instanceof GenericArrayType) { // 元素类型是泛型列表。例如genericArrayType为T[][]则属于这种情况
            resolvedComponentType = resolveGenericArrayType((GenericArrayType) componentType, srcType, declaringClass);
        } else if (componentType instanceof ParameterizedType) { // 元素类型是参数化类型。例如genericArrayType为Collection<T>[]则属于这种情况
            resolvedComponentType = resolveParameterizedType((ParameterizedType) componentType, srcType, declaringClass);
        }
        if (resolvedComponentType instanceof Class) {
            return Array.newInstance((Class<?>) resolvedComponentType, 0).getClass();
        } else {
            return new GenericArrayTypeImpl(resolvedComponentType);
        }
    }

    /**
     * 解析参数化类型的实际结果
     *
     * @param parameterizedType 参数化类型的变量
     * @param srcType           该变量所属于的类
     * @param declaringClass    定义该变量的类
     * @return 参数化类型的实际结果
     */
    private static ParameterizedType resolveParameterizedType(ParameterizedType parameterizedType, Type srcType, Class<?> declaringClass) {
        // 变量的原始类型。本示例中为List
        Class<?> rawType = (Class<?>) parameterizedType.getRawType();
        // 获取类型参数。本示例中只有一个类型参数T
        Type[] typeArgs = parameterizedType.getActualTypeArguments();
        // 类型参数的实际类型
        Type[] args = new Type[typeArgs.length];
        for (int i = 0; i < typeArgs.length; i++) { // 依次处理每一个类型参数
            if (typeArgs[i] instanceof TypeVariable) { // 类型参数是类型变量。例如parameterizedType为List<T>则属于这种情况
                args[i] = resolveTypeVar((TypeVariable<?>) typeArgs[i], srcType, declaringClass);
            } else if (typeArgs[i] instanceof ParameterizedType) { // 类型参数是参数化类型。例如parameterizedType为List<List<T>>则属于这种情况
                args[i] = resolveParameterizedType((ParameterizedType) typeArgs[i], srcType, declaringClass);
            } else if (typeArgs[i] instanceof WildcardType) { // 类型参数是通配符泛型。例如parameterizedType为List<? extends Number>则属于这种情况
                args[i] = resolveWildcardType((WildcardType) typeArgs[i], srcType, declaringClass);
            } else { // 类型参数是确定的类型。例如parameterizedType为List<String>则会进入这里
                args[i] = typeArgs[i];
            }
        }
        return new ParameterizedTypeImpl(rawType, null, args);
    }

    private static Type resolveWildcardType(WildcardType wildcardType, Type srcType, Class<?> declaringClass) {
        Type[] lowerBounds = resolveWildcardTypeBounds(wildcardType.getLowerBounds(), srcType, declaringClass);
        Type[] upperBounds = resolveWildcardTypeBounds(wildcardType.getUpperBounds(), srcType, declaringClass);
        return new WildcardTypeImpl(lowerBounds, upperBounds);
    }

    private static Type[] resolveWildcardTypeBounds(Type[] bounds, Type srcType, Class<?> declaringClass) {
        Type[] result = new Type[bounds.length];
        for (int i = 0; i < bounds.length; i++) {
            if (bounds[i] instanceof TypeVariable) {
                result[i] = resolveTypeVar((TypeVariable<?>) bounds[i], srcType, declaringClass);
            } else if (bounds[i] instanceof ParameterizedType) {
                result[i] = resolveParameterizedType((ParameterizedType) bounds[i], srcType, declaringClass);
            } else if (bounds[i] instanceof WildcardType) {
                result[i] = resolveWildcardType((WildcardType) bounds[i], srcType, declaringClass);
            } else {
                result[i] = bounds[i];
            }
        }
        return result;
    }

    /**
     * 解析泛型变量的实际结果
     *
     * @param typeVar        泛型变量
     * @param srcType        该变量所属于的类
     * @param declaringClass 定义该变量的类
     * @return 泛型变量的实际结果
     */
    private static Type resolveTypeVar(TypeVariable<?> typeVar, Type srcType, Class<?> declaringClass) {
        // 解析出的泛型变量的结果
        Type result;
        Class<?> clazz;
        if (srcType instanceof Class) { // 该变量属于确定的类。该示例中，变量T属于Student类，Student类是一个确定的类
            clazz = (Class<?>) srcType;
        } else if (srcType instanceof ParameterizedType) { // 该变量属于参数化类型
            ParameterizedType parameterizedType = (ParameterizedType) srcType;
            // 获取参数化类型的原始类型
            clazz = (Class<?>) parameterizedType.getRawType();
        } else {
            throw new IllegalArgumentException("The 2nd arg must be Class or ParameterizedType, but was: " + srcType.getClass());
        }

        if (clazz == declaringClass) { // 变量属于的类和定义变量的类一致。该示例中，变量T属于Student,定义于User
            // 确定泛型变量的上届
            Type[] bounds = typeVar.getBounds();
            if (bounds.length > 0) {
                return bounds[0];
            }
            // 泛型变量无上届，则上届为Object
            return Object.class;
        }

        // 获取变量属于的类的父类。在该示例中，变量属于Student类，其父类为User<Number>类
        Type superclass = clazz.getGenericSuperclass();
        // 扫描父类，查看能否确定边界。该示例中，能确定出边界为Number
        result = scanSuperTypes(typeVar, srcType, declaringClass, clazz, superclass);
        if (result != null) {
            return result;
        }

        // 获取变量属于的类的接口
        Type[] superInterfaces = clazz.getGenericInterfaces();
        // 依次扫描各个父接口，查看能否确定边界。该示例中，Student类无父接口
        for (Type superInterface : superInterfaces) {
            result = scanSuperTypes(typeVar, srcType, declaringClass, clazz, superInterface);
            if (result != null) {
                return result;
            }
        }
        // 如果始终找不到结果，则未定义。即为Object
        return Object.class;
    }

    private static Type scanSuperTypes(TypeVariable<?> typeVar, Type srcType, Class<?> declaringClass, Class<?> clazz, Type superclass) {
        if (superclass instanceof ParameterizedType) {
            ParameterizedType parentAsType = (ParameterizedType) superclass;
            Class<?> parentAsClass = (Class<?>) parentAsType.getRawType();
            TypeVariable<?>[] parentTypeVars = parentAsClass.getTypeParameters();
            if (srcType instanceof ParameterizedType) {
                parentAsType = translateParentTypeVars((ParameterizedType) srcType, clazz, parentAsType);
            }
            if (declaringClass == parentAsClass) {
                for (int i = 0; i < parentTypeVars.length; i++) {
                    if (typeVar == parentTypeVars[i]) {
                        return parentAsType.getActualTypeArguments()[i];
                    }
                }
            }
            if (declaringClass.isAssignableFrom(parentAsClass)) {
                return resolveTypeVar(typeVar, parentAsType, declaringClass);
            }
        } else if (superclass instanceof Class && declaringClass.isAssignableFrom((Class<?>) superclass)) {
            return resolveTypeVar(typeVar, superclass, declaringClass);
        }
        return null;
    }

    private static ParameterizedType translateParentTypeVars(ParameterizedType srcType, Class<?> srcClass, ParameterizedType parentType) {
        Type[] parentTypeArgs = parentType.getActualTypeArguments();
        Type[] srcTypeArgs = srcType.getActualTypeArguments();
        TypeVariable<?>[] srcTypeVars = srcClass.getTypeParameters();
        Type[] newParentArgs = new Type[parentTypeArgs.length];
        boolean noChange = true;
        for (int i = 0; i < parentTypeArgs.length; i++) {
            if (parentTypeArgs[i] instanceof TypeVariable) {
                for (int j = 0; j < srcTypeVars.length; j++) {
                    if (srcTypeVars[j] == parentTypeArgs[i]) {
                        noChange = false;
                        newParentArgs[i] = srcTypeArgs[j];
                    }
                }
            } else {
                newParentArgs[i] = parentTypeArgs[i];
            }
        }
        return noChange ? parentType : new ParameterizedTypeImpl((Class<?>) parentType.getRawType(), null, newParentArgs);
    }

    private TypeParameterResolver() {
        super();
    }

    /**
     * 泛型参数泛型
     * 以Map<String,Object为例>
     */
    static class ParameterizedTypeImpl implements ParameterizedType {
        // 所属对象
        private Class<?> rawType;
        // 泛型类型，例如Map
        private Type ownerType;
        // 内部类型，例如 Sting,Object
        private Type[] actualTypeArguments;

        public ParameterizedTypeImpl(Class<?> rawType, Type ownerType, Type[] actualTypeArguments) {
            super();
            this.rawType = rawType;
            this.ownerType = ownerType;
            this.actualTypeArguments = actualTypeArguments;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return actualTypeArguments;
        }

        @Override
        public Type getOwnerType() {
            return ownerType;
        }

        @Override
        public Type getRawType() {
            return rawType;
        }

        @Override
        public String toString() {
            return "ParameterizedTypeImpl [rawType=" + rawType + ", ownerType=" + ownerType + ", actualTypeArguments=" + Arrays.toString(actualTypeArguments) + "]";
        }
    }

    /**
     * 上下界的泛型
     */
    static class WildcardTypeImpl implements WildcardType {
        private Type[] lowerBounds;

        private Type[] upperBounds;

        WildcardTypeImpl(Type[] lowerBounds, Type[] upperBounds) {
            super();
            this.lowerBounds = lowerBounds;
            this.upperBounds = upperBounds;
        }

        @Override
        public Type[] getLowerBounds() {
            return lowerBounds;
        }

        @Override
        public Type[] getUpperBounds() {
            return upperBounds;
        }
    }

    /**
     * 列表类泛型
     */
    static class GenericArrayTypeImpl implements GenericArrayType {
        private Type genericComponentType;

        GenericArrayTypeImpl(Type genericComponentType) {
            super();
            this.genericComponentType = genericComponentType;
        }

        @Override
        public Type getGenericComponentType() {
            return genericComponentType;
        }
    }
}
