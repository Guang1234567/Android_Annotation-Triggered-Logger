/*
 * Copyright (C) 2015 The Android Open Source Project
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
package eu.f3rog.log;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Denotes that the annotated method should only be called on a worker thread.
 * If the annotated element is a class, then all methods in the class should be called
 * on a worker thread.
 * <p>
 * Example:
 * <pre><code>
 *  &#64;WorkerThread
 *  protected abstract FilterResults performFiltering(CharSequence constraint);
 * </code></pre>
 */
@Documented
@Retention(RUNTIME)
@Target({METHOD,CONSTRUCTOR,TYPE})
public @interface WorkerThread {
}