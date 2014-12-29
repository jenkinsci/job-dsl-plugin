/*
 * Copyright 2003-2012 the original author or authors.
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
package groovy.lang;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>This annotation can be used by API or DSL writers to document parameters which accept a closure.
 * In that case, using this annotation, you can specify what the delegate type of the closure will
 * be. This is important for IDE support.</p>
 * <p/>
 * <p>Example:</p>
 * <pre><code>
 *     // Document the fact that the delegate of the closure will be an ExecSpec
 *     ExecResult exec(@DelegatesTo(ExecSpec) Closure closure) { ... }
 * </code></pre>
 *
 * @author Cedric Champeau
 * @author Peter Niderwieser
 * @since 2.1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface DelegatesTo {
    Class value();

    int strategy() default Closure.OWNER_FIRST;

    String target() default "";

    @Retention(RetentionPolicy.RUNTIME)
    @java.lang.annotation.Target({ElementType.PARAMETER})
    public static @interface Target {
        String value() default ""; // optional id
    }
}
