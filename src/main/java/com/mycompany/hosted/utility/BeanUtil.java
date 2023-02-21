/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.hosted.utility;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
/**
 *
 * @author dinah
 * 
 */
public class BeanUtil {
    
    /*
     * Can only be used for primary fields. Does not recurse through Object references. 
     */
    public static void evalNullOrEmptyFields(Class<?> cls, Object oinstance) {
        
        String uninitialized = "";
        
        Field[] fields = cls.getDeclaredFields();
       
        try {
            for (Field fld : fields) {
                int imodifiers = fld.getModifiers();
                if(Modifier.isFinal(imodifiers))
                    continue;
                String name = fld.getName();
                String methodName = "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
                Method method = cls.getDeclaredMethod(methodName);
                Object value = method.invoke(oinstance);
                boolean invalid = CloneUtil.isEmptyOrNullValue(value);
                if(invalid)
                    uninitialized += name + ";";
             }
           
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
              
                throw new RuntimeException("BeanUtil#evalNullOrEmptyFields: " + e.getMessage(), e);
          }
        if(!uninitialized.isEmpty())
            doException(cls, uninitialized);
        
    }
    
    public static boolean isEmptyOrNullField(Class<?> container, Object oinstance, Field field) {    	
    	
    	
		try {
			
			Method method = getMethod(container, field.getName());
			
			Object value = method.invoke(oinstance);
			
			if(CloneUtil.isEmptyOrNullValue(value))
	    		return true;
			
		} catch (NoSuchMethodException| IllegalAccessException | InvocationTargetException e) {
			
			throw new IllegalArgumentException("BeanUtil#isEmptyOrNullField: " + e.getMessage(), e);
		}    	
    	
    	return false;
    }
    
    public static void evalNullOrEmptyFields(Class<?> cls, Object oinstance,
            String...fieldNames) {
        
        String uninitialized = "";
        try {
            for (String name : fieldNames) {

                Method method = getMethod(cls, name);

                Object value = method.invoke(oinstance);

                if (CloneUtil.isEmptyOrNullValue(value)) {
                    uninitialized += name + ";";
                }
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("BeanUtil#evalNullOrEmptyFields: " + e.getMessage(), e);
        }
        if (!uninitialized.isEmpty()) {
            doException(cls, uninitialized);
        }
    }
    
    private static Method getMethod(Class<?> cls, String name) throws NoSuchMethodException {
    	
    	String methodName = "get" + name.substring(0, 1).toUpperCase() + name.substring(1);

        Method method = cls.getDeclaredMethod(methodName);
        
        return method;
    	
    }
            
    private static void doException(Class<?> cls, String fieldNames) {
        String message = "BeanUtil#hasNullOrEmptyValues: " +
                cls.getCanonicalName()
                + ": {" + fieldNames
                + "} have uninitialized values.";
        throw new IllegalStateException(message);
        
    }
}
