package com.kms.katalon.custom.factory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.persistence.internal.libraries.asm.ClassReader;
import org.eclipse.persistence.internal.libraries.asm.Type;
import org.eclipse.persistence.internal.libraries.asm.tree.ClassNode;
import org.eclipse.persistence.internal.libraries.asm.tree.LocalVariableNode;
import org.eclipse.persistence.internal.libraries.asm.tree.MethodNode;

import com.kms.katalon.core.annotation.Keyword;
import com.kms.katalon.core.keyword.IKeywordContributor;
import com.kms.katalon.core.keyword.KeywordContributorCollection;

public class BuiltInMethodNodeFactory {
    
	private final static String CLASS_FILE_EXTENSION = ".class";
    private static BuiltInMethodNodeFactory _instance;
    public static final String CALL_TEST_CASE_METHOD_NAME = "callTestCase";
    
    public void addKeywordContributor(IKeywordContributor contributor) {
    	KeywordContributorCollection.getInstance().getKeywordContributors().add(contributor);
    }
    
    public List<IKeywordContributor> getKeywordContributors() {
        return KeywordContributorCollection.getInstance().getKeywordContributors();
    }

    public static BuiltInMethodNodeFactory getInstance() {
        if (_instance == null) {
            _instance = new BuiltInMethodNodeFactory();
        }
        return _instance;
    }
    
    public Method getMethod(String className, String methodName) {
    	List<Method> methods = getMethods(className);
    	if (methods.isEmpty()) {
    		return null;
    	}
    	for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }
    
    public List<Method> getSortedMethods(String className) {
        List<Method> methods = getMethods(className);
        for (int i = 0; i < methods.size() ; i++) {
        	if (methods.get(i).getName().equals(CALL_TEST_CASE_METHOD_NAME)) {
        		methods.remove(i);
        		break;
        	}
        }
        //Alphabet sort
        Collections.sort(methods, new Comparator<Method>() {
            @Override
            public int compare(Method o1, Method o2) {
                if(o1 != null && o2 != null){
                    return o1.getName().compareTo(o2.getName());
                }
                return 0;
            }
        });
        
        return methods;
    }
    
    public Method getCallTestCaseMethod(String className) {
         for (Method method : getMethods(className)) {
         	if (method.getName().equals(CALL_TEST_CASE_METHOD_NAME)) {
         		return method;
         	}
         }
         return null;
    }
    
    private List<Method> getMethods(String className) {
        for (IKeywordContributor contributor : getKeywordContributors()) {
            Class<?> clazz = contributor.getKeywordClass();
            if (contributor.getKeywordClass().getName().equals(className) || 
                    contributor.getKeywordClass().getSimpleName().equals(className)) {
                List<Method> methods = new ArrayList<Method>();
                for (Method method : clazz.getMethods()) {
                   if (isBuiltinMethod(method)) {
                	   methods.add(method);
                   }
                }
                return methods;
            }
        }
        return Collections.emptyList();
    }
    
    private boolean isBuiltinMethod(Method method) {
    	  int modifiers = method.getModifiers();
          Annotation ann = method.getAnnotation(Keyword.class);
          if (Modifier.isPublic(modifiers) && ann != null) {
              return true;
          } else {
        	  return false;
          }
    }
    
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<String> getParameterNames(Method method) throws IOException {		
		Class clazz = method.getDeclaringClass();
		InputStream is = clazz.getClassLoader().getResourceAsStream(Type.getType(clazz)
				.getInternalName() + CLASS_FILE_EXTENSION);
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(is);
		classReader.accept(classNode, 0);

		int increament = 2;
		for (MethodNode node : (List<MethodNode>) classNode.methods) {
			if (node.name.equals(method.getName())) {
				List<String> parameterNames = new ArrayList<String>(
						node.localVariables.size());
	
				List<LocalVariableNode> localVariables = node.localVariables;
				for (int i = 0; i < method.getParameterTypes().length; i ++) {				
					parameterNames.add(localVariables.get(i * increament).name);
				}
				return parameterNames;
			}
		}
		return Collections.emptyList();
	}
	
	public List<Class<?>> getKeywordClasses() {
	    List<Class<?>> keywordClasses = new ArrayList<Class<?>>();
	    for (IKeywordContributor contributor : getKeywordContributors()) {
	        keywordClasses.add(contributor.getKeywordClass());
	    }
	    return keywordClasses;
	}
	
	public List<String> getKeywordClassNames() {
	    List<String> keywordClassNames = new ArrayList<String>();
	    for (IKeywordContributor contributor : getKeywordContributors()) {
	        keywordClassNames.add(contributor.getKeywordClass().getName());
	    }
	    return keywordClassNames;
	}
	
	public boolean isBuiltinMethodName(Class<?> declaringClass, String methodName) throws Exception {
		Method method = declaringClass.getMethod(methodName, declaringClass);
		return isBuiltinMethod(method);
	}
}
