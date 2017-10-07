package com.common_lib.io;

import java.lang.reflect.Constructor;

public class ObjectReflectionParser extends GeneralFileParser{

	public static Object instantiate(String className,Object...constructorParams){
		
		Object ret = null;
		try {
			Class<?> c = Class.forName(className);
			Constructor<?> con = c.getConstructor(getClasses(constructorParams));
			ret = con.newInstance(constructorParams);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return ret;
	}
	
	public static Object instantiate(Class<?> cls,Object...constructorParams){
		
		Object ret = null;
		try {
			Constructor<?> con = cls.getConstructor(getClasses(constructorParams));
			ret = con.newInstance(constructorParams);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return ret;
	}
	
	public static Object instantiateWide(String className,Object...constructorParams){
		
		Object ret = null;
		try {
			Class<?> c = Class.forName(className);
			Constructor<?> con = findConstructor(c, constructorParams);
			ret = con.newInstance(constructorParams);
		} catch (Exception e) {
			//System.out.println(className);
			e.printStackTrace();
		}
		
		return ret;
	}
	
	public static Object instantiateWide(Class<?> cls,Object...constructorParams){
		
		Object ret = null;
		try {
			Constructor<?> con = findConstructor(cls, constructorParams);
			ret = con.newInstance(constructorParams);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return ret;
	}
	
	public static Class<?>[] getClasses(Object...args){
		
		Class<?>[] ret = new Class<?>[args.length];
		int i = 0;
		for (Object arg : args) {
			ret[i++] = arg.getClass();
		}
		
		return ret;
	}

	public static Class<?> classForName(String className){
		
		Class<?> ret = null;
		try {
			ret = Class.forName(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return ret;
	}

	public static boolean doesClassConform(Class<?> cls,Class<?> superOrInterface){
		
		return superOrInterface.isAssignableFrom(cls);
	}

	//----------------------------------------------------------------------+
	public static void printConstructors(Class<?> cls){
		
		Constructor<?>[] cons = cls.getConstructors();
		for (Constructor<?> constructor : cons) {
			System.out.println(constructor);
		}
	}
	
	public static Constructor<?> findConstructor(Class<?> cls,Object...constructorParams){
		
		Constructor<?> ret = null;
		Constructor<?>[] cons = cls.getConstructors();
		Class<?>[] params;
		int paramIndex = 0;
		for (Constructor<?> con : cons) {
			//System.out.println(con);
			paramIndex = 0;
			params = con.getParameterTypes();
			for (Class<?> pCls : params) {
				//System.out.println(pCls+" "+constructorParams[paramIndex].getClass());
				if(!pCls.isAssignableFrom(constructorParams[paramIndex++].getClass())){
					//System.out.println("2");
					--paramIndex;
					break;
				}
				
			}
			//System.out.println(paramIndex+" "+(params.length));
			if(paramIndex == params.length){
				ret = con;
				break;
			}
		}
		
		//System.out.println("ret "+ret);
		return ret;
	}

	//----------------------------------------------------------------------+
}
