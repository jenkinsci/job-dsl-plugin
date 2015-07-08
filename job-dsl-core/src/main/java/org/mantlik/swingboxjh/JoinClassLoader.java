/*
 * #%L
 * swingbox-javahelp-viewer
 * %%
 * Copyright (C) 2012 RNDr. Frantisek Mantlik <frantisek at mantlik.cz>
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package org.mantlik.swingboxjh;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

/**
 * A class loader that combines multiple class loaders into one.<br>
 * The classes loaded by this class loader are associated with this class loader,
 * i.e. Class.getClassLoader() points to this class loader.
 * <p>
 * Original code author: uthor Christian d'Heureuse, Inventec Informatik AG, Zurich, Switzerland, www.source-code.biz<br>
 * http://www.source-code.biz/snippets/java/12.htm
 *
 * Sometimes it's necessary to combine multiple class loaders into a new one.
 *
 * For example, when using custom form components in NetBeans with custom property 
 * editors, the property editor class cannot access the NetBeans specific classes, 
 * because NetBeans loads the bean and the property editor through a separate 
 * isolated class loader. There are two possible solutions for this problem:
 *
 * Use a NetBeans module (plugin) and declare module dependencies in the JAR manifest.
 * Combine the class loader for the JAR file with the NetBeans system class loader 
 * and use the new class loader to load the property editor class.
 * 
 * The JoinClassLoader class below may be used to combine class loaders.
 *
 * License: LGPL, http://www.gnu.org/licenses/lgpl.html<br>
 * Please contact the author if you need another license.
 */
public class JoinClassLoader extends ClassLoader {

    private ClassLoader[] delegateClassLoaders;
    ArrayList <String> registeredClasses = new ArrayList<String>();

    public JoinClassLoader(ClassLoader parent, ClassLoader... delegateClassLoaders) {
        super(parent);
        this.delegateClassLoaders = delegateClassLoaders;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        // It would be easier to call the loadClass() methods of the delegateClassLoaders
        // here, but we have to load the class from the byte code ourselves, because we
        // need it to be associated with our class loader.
        String path = name.replace('.', '/') + ".class";
        URL url = findResource(path);
        if (url == null) {
            throw new ClassNotFoundException(name);
        }
        ByteBuffer byteCode;
        try {
            byteCode = loadResource(url);
        } catch (IOException e) {
            throw new ClassNotFoundException(name, e);
        }
        Class c = defineClass(name, byteCode, null);
        registeredClasses.add(name);
        return c;
    }
    
    public Class<?> forceLoader(String name) throws ClassNotFoundException {
        if (registeredClasses.contains(name)) {
            return loadClass(name);
        }
        return findClass(name);
    }

    private ByteBuffer loadResource(URL url) throws IOException {
        InputStream stream = null;
        try {
            stream = url.openStream();
            int initialBufferCapacity = Math.min(0x40000, stream.available() + 1);
            if (initialBufferCapacity <= 2) {
                initialBufferCapacity = 0x10000;
            } else {
                initialBufferCapacity = Math.max(initialBufferCapacity, 0x200);
            }
            ByteBuffer buf = ByteBuffer.allocate(initialBufferCapacity);
            while (true) {
                if (!buf.hasRemaining()) {
                    ByteBuffer newBuf = ByteBuffer.allocate(2 * buf.capacity());
                    buf.flip();
                    newBuf.put(buf);
                    buf = newBuf;
                }
                int len = stream.read(buf.array(), buf.position(), buf.remaining());
                if (len <= 0) {
                    break;
                }
                buf.position(buf.position() + len);
            }
            buf.flip();
            return buf;
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    @Override
    protected URL findResource(String name) {
        for (ClassLoader delegate : delegateClassLoaders) {
            URL resource = delegate.getResource(name);
            if (resource != null) {
                return resource;
            }
        }
        return null;
    }

    @Override
    protected Enumeration<URL> findResources(String name) throws IOException {
        ArrayList<URL> vector = new ArrayList<URL>();
        for (ClassLoader delegate : delegateClassLoaders) {
            Enumeration<URL> enumeration = delegate.getResources(name);
            while (enumeration.hasMoreElements()) {
                vector.add(enumeration.nextElement());
            }
        }
        return Collections.enumeration(vector);
    }

} // end class JoinClassLoader

