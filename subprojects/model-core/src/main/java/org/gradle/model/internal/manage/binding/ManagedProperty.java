/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.model.internal.manage.binding;

import org.gradle.model.internal.manage.schema.extract.PropertyAccessorType;
import org.gradle.model.internal.method.WeaklyTypeReferencingMethod;
import org.gradle.model.internal.type.ModelType;

import java.util.Map;

/**
 * A managed property of a struct.
 */
public class ManagedProperty {
    private final String name;
    private final ModelType<?> type;
    private final boolean internal;
    private final Map<PropertyAccessorType, WeaklyTypeReferencingMethod<?, ?>> accessors;

    public ManagedProperty(String name, ModelType<?> type, boolean internal, Map<PropertyAccessorType, WeaklyTypeReferencingMethod<?, ?>> accessors) {
        this.name = name;
        this.type = type;
        this.internal = internal;
        this.accessors = accessors;
    }

    public String getName() {
        return name;
    }

    public ModelType<?> getType() {
        return type;
    }

    public boolean isInternal() {
        return internal;
    }

    public Map<PropertyAccessorType, WeaklyTypeReferencingMethod<?, ?>> getAccessors() {
        return accessors;
    }

    public boolean isWritable() {
        return accessors.containsKey(PropertyAccessorType.SETTER);
    }
}
