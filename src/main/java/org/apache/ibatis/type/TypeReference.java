/*
 *    Copyright 2009-2023 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.type;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * References a generic type.
 *
 * @param <T>
 *          the referenced type
 *
 * @since 3.1.0
 *
 * @author Simone Tripodi
 */
public abstract class TypeReference<T> {

  private final Type rawType;

  protected TypeReference() {
    rawType = getSuperclassTypeParameter(getClass());
  }

  Type getSuperclassTypeParameter(Class<?> clazz) {
    Type genericSuperclass = clazz.getGenericSuperclass(); // 子类向上查找
    if (genericSuperclass instanceof Class) { // 如果是普通类型
      // try to climb up the hierarchy until meet something useful
      if (TypeReference.class != genericSuperclass) {
        return getSuperclassTypeParameter(clazz.getSuperclass()); // 继续向上查找
      }
      // 如果找到TypeReference的时候还是只有普通类, 说明子类没有显示声明泛型T, 抛出异常
      throw new TypeException("'" + getClass() + "' extends TypeReference but misses the type parameter. "
          + "Remove the extension or add a type parameter to it.");
    }

    Type rawType = ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0]; // 找到了泛型声明
    // TODO remove this when Reflector is fixed to return Types
    if (rawType instanceof ParameterizedType) { // 如果泛型T自身具有一个泛型, 则视之为包装类型, 以其内部的T为准
      rawType = ((ParameterizedType) rawType).getRawType();
    }

    return rawType;
  }

  public final Type getRawType() {
    return rawType;
  }

  @Override
  public String toString() {
    return rawType.toString();
  }

}
