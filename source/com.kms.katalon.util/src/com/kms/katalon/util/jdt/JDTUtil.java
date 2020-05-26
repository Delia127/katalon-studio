package com.kms.katalon.util.jdt;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

import com.kms.katalon.util.TypeUtil;

public class JDTUtil {
    
    public static IMethod findMethod(
            IProject project,
            String className,
            String methodName,
            String[] parameterTypes) throws JavaModelException {
        
        IType type = findType(project, className);
        for (IMethod method : type.getMethods()) {
            if (isSameMethod(method, className, methodName, parameterTypes, false)) {
                return method;
            }
        }
        return null;
    }
    
    public static IMethod findMethod(
            IProject project,
            String className,
            String methodName,
            int numOfParams) throws JavaModelException {
        
        IType type = findType(project, className);
        for (IMethod method : type.getMethods()) {
            if (method.getElementName().equals(methodName) && numOfParams == method.getParameters().length) {
                return method;
            }
        }
        return null;
    }
    
    public static IMethod findMethodWithLooseParamTypesMatching(
            IProject project,
            String className,
            String methodName,
            String[] parameterTypes) throws JavaModelException {
        
        IType type = findType(project, className);
        for (IMethod method : type.getMethods()) {
            if (isSameMethod(method, className, methodName, parameterTypes, true)) {
                return method;
            }
        }
        return null;
    }
    
    
    private static boolean isSameMethod(
            IMethod method,
            String className,
            String methodName,
            String[] parameterTypes,
            boolean useLooseParameterTypesChecking) throws JavaModelException {
        
        String className1 = method.getDeclaringType().getFullyQualifiedName();
        String methodName1 = method.getElementName();
        String[] parameterTypes1 = getParameterTypes(method);
        boolean hasSameParameterTypes = useLooseParameterTypesChecking
                ? TypeUtil.primitiveAwareSameTypesCheckWithLooseTypeMatching(parameterTypes, parameterTypes1)
                : TypeUtil.primitiveAwareSameTypesCheck(parameterTypes, parameterTypes1);
        return className1.equals(className) && methodName1.equals(methodName) && hasSameParameterTypes;
    }
    
    private static String[] getParameterTypes(IMethod method) throws JavaModelException {
        List<String> parameterTypes = new ArrayList<>();
        for (String type : method.getParameterTypes()) {
            parameterTypes.add(getReadableType(type, method));
        }
        
        return parameterTypes.toArray(new String[parameterTypes.size()]);
    }
    
    private static String getReadableType(String typeSignature, IMethod method) throws IllegalArgumentException, JavaModelException {
        String readableType = Signature.toString(resolveTypeSignature(method, typeSignature)).replace("/", ".");
        return readableType;
    }

    private static String resolveTypeSignature(IMethod method, String typeSignature) throws JavaModelException {
        int count = Signature.getArrayCount(typeSignature);
        String elementTypeSignature = Signature.getElementType(typeSignature);
        if (elementTypeSignature.length() == 1) {
            // no need to resolve primitive types
            return typeSignature;
        }
        String elementTypeName = Signature.toString(elementTypeSignature);
        IType type = method.getDeclaringType();
        String[][] resolvedElementTypeNames = type.resolveType(elementTypeName);
        if (resolvedElementTypeNames == null || resolvedElementTypeNames.length != 1) {
            // check if type parameter
            ITypeParameter typeParameter = method.getTypeParameter(elementTypeName);
            if (!typeParameter.exists()) {
                typeParameter = type.getTypeParameter(elementTypeName);
            }
            if (typeParameter.exists()) {
                String[] bounds = typeParameter.getBounds();
                if (bounds.length == 0) {
                    return "Ljava/lang/Object;"; //$NON-NLS-1$
                }
                String bound = Signature.createTypeSignature(bounds[0], false);
                return Signature.createArraySignature(resolveTypeSignature(method, bound), count);
            }
            // the type name cannot be resolved
            return null;
        }

        String[] types = resolvedElementTypeNames[0];
        types[1] = types[1].replace('.', '$');

        String resolvedElementTypeName = Signature.toQualifiedName(types);
        String resolvedElementTypeSignature = "";
        if(types[0].equals("")) {
            resolvedElementTypeName = resolvedElementTypeName.substring(1);
            resolvedElementTypeSignature = Signature.createTypeSignature(resolvedElementTypeName, true);
        }
        else {
            resolvedElementTypeSignature = Signature.createTypeSignature(resolvedElementTypeName, true).replace('.', '/');
        }

        return Signature.createArraySignature(resolvedElementTypeSignature, count);
    }
    
    public static IType findType(IProject project, String className) throws JavaModelException {
        IJavaProject javaProject = getJavaProject(project);
        return javaProject.findType(className);
    }
    
    public static String findJavadoc(IJavaElement element) throws JavaModelException {
        IMember member;
        if (element instanceof ILocalVariable) {
            member= ((ILocalVariable) element).getDeclaringMember();
        } else if (element instanceof ITypeParameter) {
            member= ((ITypeParameter) element).getDeclaringMember();
        } else if (element instanceof IMember) {
            member= (IMember) element;
        } else {
            return null;
        }
        
        IBuffer buf= member.getOpenable().getBuffer();
        if (buf == null) {
            return null; // no source attachment found
        }

        ISourceRange javadocRange = member.getJavadocRange();
        if (javadocRange == null) {
            return "";
        }
        return buf.getText(javadocRange.getOffset(), javadocRange.getLength());
    }

    
    private static IJavaProject getJavaProject(IProject project) {
        return JavaCore.create(project);
    }
}
