package com.vm.tools;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtilsBean;

public class Tools {

	public static Map<String, Object> beanToMap(Object obj){
		 Map<String, Object> params = new HashMap<String, Object>(); 
         try { 
             PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean(); 
             PropertyDescriptor[] descriptors = propertyUtilsBean.getPropertyDescriptors(obj); 
             for (int i = 0; i < descriptors.length; i++) { 
                 String name = descriptors[i].getName(); 
                 if (!"class".equals(name)) { 
                     params.put(name, propertyUtilsBean.getNestedProperty(obj, name)); 
                 } 
             } 
         } catch (Exception e) { 
            System.err.println("Tools.beanToMap³ö´í£¬obj=" +obj);
         } 
         return params; 
	}
	
	public static double getAverage(double[] ds){
		if(ds != null){
			double result = 0.0;
			for(double d : ds){
				result += d;
			}
			return result/ds.length;
		}
		return 0.0;
	}
}
