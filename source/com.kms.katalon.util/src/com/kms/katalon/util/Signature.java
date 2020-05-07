package com.kms.katalon.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class Signature {
    private final Method[] methods;

    public Signature(Class<?> cls) {
        this.methods = cls.getDeclaredMethods();
    }

    public Signature(Method... methods) {
        this.methods = methods;
    }

    /**
     * Returns the index of the first method with the specified name.
     * 
     * @throws NoSuchMethodException
     */
    public int getIndex(String methodName) throws NoSuchMethodException {
        Method[] methods = this.methods;
        String internedName = methodName.intern();
        for (int i = 0, n = methods.length; i < n; i++)
            if (methods[i].getName() == internedName)
                return i;
        throw new NoSuchMethodException(methodName);
    }

    public Method getMethod(String methodName) throws NoSuchMethodException {
        return methods[getIndex(methodName)];
    }

    public String getSignature(String methodName) throws NoSuchMethodException {
        return getSignature(getMethod(methodName));
    }

    public static String getConstructorSignature(Class<?> cls, Class<?>... parameterTypes)
            throws NoSuchMethodException, SecurityException {
        return getSignature(cls.getConstructor(parameterTypes));
    }

    public static String getSignature(Method m) {
        return getSignature(m.getReturnType(), m.getParameterTypes());
    }

    /**
     * Returns the type signature corresponding to the given constructor.
     * 
     * @param ctor a {@link Constructor Constructor} object.
     * @return the type signature of the given constructor.
     */
    public static String getSignature(Constructor<?> ctor) {
        return getSignature("V", ctor.getParameterTypes());
    }

    /**
     * Returns the type signature of a java method corresponding to the given
     * parameter and return types.
     * 
     * @param returnType the return type for the method.
     * @param parameterTypes the parameter types for the method.
     * @return the type signature corresponding to the given parameter and
     *         return types.
     */
    public static String getSignature(Class<?> returnType, Class<?>... parameterTypes) {
        return getSignature(getSignature(returnType), parameterTypes);
    }

    private static String getSignature(String returnTypeName, Class<?>... parameterTypes) {
        StringBuilder sb = new StringBuilder();
        sb.append('(');
        for (Class<?> type : parameterTypes) {
            sb.append(getSignature(type));
        }
        sb.append(')');
        return sb.append(returnTypeName).toString();
    }

    /**
     * Converts a Java source-language class name into the internal form. The
     * internal name of a class is its fully qualified name, as returned by
     * Class.getName(), where '.' are replaced by '/'.
     * 
     * @param c an object or array class.
     * @return the internal name form of the given class
     * 
     */
    public static String getSignature(Class<?> c) {
        if (c.isPrimitive()) {
            if (int.class == c) { // or Integer.TYPE
                return "I";
            } else if (long.class == c) { // or Long.TYPE
                return "J";
            } else if (boolean.class == c) { // or Boolean.TYPE
                return "Z";
            } else if (byte.class == c) { // or Byte.TYPE
                return "B";
            } else if (short.class == c) { // or Short.TYPE
                return "S";
            } else if (char.class == c) { // or Char.TYPE
                return "C";
            } else if (float.class == c) { // or Float.TYPE
                return "F";
            } else if (double.class == c) { // or Double.TYPE
                return "D";
            } else {
                throw new IllegalArgumentException("Should never reach here");
            }
        } else if (void.class == c || Void.class == c) {
            // e.g.
            // public void return_void() { }
            // public Void return_Void() { return null; }
            return "V";
        } else {
            String internalName = c.getName().replace('.', '/');
            if (c.isArray()) {
                /* Already in the correct array style. */
                return internalName;
            } else {
                return 'L' + internalName + ';';
            }
        }
    }

    /**
     * Returns the class name of the class corresponding to an internal name.
     * 
     * @return the binary name of the class corresponding to this type.
     */
    public static String getClassName(String internalName) {
        switch (internalName.charAt(0)) {
        case 'V':
            return "void";
        case 'Z':
            return "boolean";
        case 'C':
            return "char";
        case 'B':
            return "byte";
        case 'S':
            return "short";
        case 'I':
            return "int";
        case 'F':
            return "float";
        case 'J':
            return "long";
        case 'D':
            return "double";
        case '[':
            return internalName.replace('/', '.');
        case 'L':
            return internalName.replace('/', '.').substring(1, internalName.length() - 1);
        default:
            return null;
        }
    }
}