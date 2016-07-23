package com.yoho.gateway.utils;

import com.yoho.gateway.utils.annotation.Mapping;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @author lijian
 *         对象copy工具类
 */
public class BeanTool {

    @SuppressWarnings("serial")
    private final static List<Class<?>> PrimitiveClasses = new ArrayList<Class<?>>() {
        {
            add(Long.class);
            add(Double.class);
            add(Integer.class);
            add(String.class);
            add(Boolean.class);
            add(Date.class);
            add(java.sql.Date.class);
        }
    };

    private final static boolean _IsPrimitive(Class<?> cls) {
        return cls.isPrimitive() || PrimitiveClasses.contains(cls);
    }

    /**
     * copy不同的pojo数据对象
     *
     * @param fromObj
     * @param toObjClazz
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T copyObject(Object fromObj, Class<T> toObjClazz) {
        try {

            Class<?> fromObjClazz = fromObj.getClass();
            // 普通类型直接返回
            if (_IsPrimitive(toObjClazz))
                return (T) fromObj;

            T toObj = toObjClazz.newInstance();

            Field[] fields = toObjClazz.getDeclaredFields();

            for (Field toF : fields) {
                try {

                    int mod = toF.getModifiers();
                    // 静态成员及常量成员不copy
                    if (Modifier.isFinal(mod) || Modifier.isStatic(mod))
                        continue;

                    String toFieldName = toF.getName();
                    String fromFieldName;
                    Mapping mapping = toF.getAnnotation(Mapping.class);

                    if (mapping == null || mapping.name() == null
                            || mapping.name().trim().equals(""))
                        fromFieldName = toFieldName;
                    else
                        fromFieldName = mapping.name();

                    toF.setAccessible(true);
                    Field fromF = fromObjClazz.getDeclaredField(fromFieldName);
                    fromF.setAccessible(true);
                    // System.out.println("aaaaa"+fromF.get(fromObj));
                    toF.set(toObj, fromF.get(fromObj));
                    // System.out.println(toF.get(toObj));
                } catch (Exception e) {
                    if (e instanceof IllegalArgumentException)
                        e.printStackTrace();
                    // e.printStackTrace();
                }
            }
            return toObj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * copy list对象
     *
     * @param fromObjList
     * @param toObjClazz
     * @return
     */
    public static <T> List<T> copyList(List<?> fromObjList, Class<T> toObjClazz) {
        List<T> toObjList = new ArrayList<T>(fromObjList.size());

        for (int i = 0; i < fromObjList.size(); i++) {
            toObjList.add(copyObject(fromObjList.get(i), toObjClazz));
        }
        return toObjList;
    }

    /**
     * copy map 对象
     *
     * @param fromObjMap
     * @param toObjClazz
     * @return
     */
    public static <T> Map<String, T> copyMap(Map<String, ?> fromObjMap,
                                             Class<T> toObjClazz) {
        Map<String, T> toObjMap = new HashMap<String, T>(fromObjMap.size());
        Iterator<String> iter = fromObjMap.keySet().iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            Object fromObj = fromObjMap.get(key);

            // if(List.class.isAssignableFrom(fromObj.getClass())){
            // toObjMap.put(key, copyList((List<?>)fromObj, toObjClazz));
            // }

            toObjMap.put(key, copyObject(fromObj, toObjClazz));
        }
        return toObjMap;
    }

    public static <T> List<T> copyListMap(List<Map<String, ?>> mapList,
                                          Class<T> toObjClass) {
        List<T> toObjList = new ArrayList<T>(mapList.size());
        for (Map<String, ?> map : mapList) {
            toObjList.add(copyMapToBean(map, toObjClass));
        }
        return toObjList;
    }

    public static <T> T copyMapToBean(Map<String, ?> map, Class<T> toObjClass) {
        try {
            Set<String> set = map.keySet();
            T objT = toObjClass.newInstance();
            for (String key : set) {
                try {
                    Object value = map.get(key);
                    Field toF = toObjClass.getDeclaredField(key);
                    toF.setAccessible(true);
                    toF.set(objT, value);
                } catch (Exception e) {
                    // 吃掉这个异常
                }
            }
            return objT;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 复制集合
     *
     * @param <E>
     * @param source
     * @param destinationClass
     * @return
     * @throws InstantiationException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static <E> List<E> copyToList(List<?> source, Class<E> destinationClass) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        if (source.size() == 0) return Collections.emptyList();
        List<E> res = new ArrayList<E>(source.size());
        for (Object o : source) {
            E e = destinationClass.newInstance();
            BeanUtils.copyProperties(e, o);
            res.add(e);
        }
        return res;
    }

    public static void copyPropertysWithoutNull(Object des, Object src,String expectFields) {        //将源拷贝到目的，NULL字段不拷贝
        Class<?> clazz = des.getClass();
        Field[] srcfields = src.getClass().getDeclaredFields();
        for (Field field : srcfields) {
            if (field.getName().equals("serialVersionUID")){
                continue;
            }
            if (expectFields!=null&&expectFields.contains(field.getName())){
                continue;
            }

            Field f;
            try {
                f = clazz.getDeclaredField(field.getName());
                f.setAccessible(true);
                field.setAccessible(true);
                Object obj = field.get(src);
                if (obj != null){
                    f.set(des, obj);
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }   //end for
    }

    private static void copyPriperties(Object o1, Object o2) {

        String fileName, str, getName, setName;
        List fields = new ArrayList();
        Method getMethod = null;
        Method setMethod = null;
        try {
            Class c1 = o1.getClass();
            Class c2 = o2.getClass();

            Field[] fs1 = c1.getDeclaredFields();
            Field[] fs2 = c2.getDeclaredFields();
//两个类属性比较剔除不相同的属性，只留下相同的属性
            for (int i = 0; i < fs2.length; i++) {
                for (int j = 0; j < fs1.length; j++) {
                    if (fs1[j].getName().equals(fs2[i].getName())) {
                        fields.add(fs1[j]);
                        break;
                    }
                }
            }
            if (null != fields && fields.size() > 0) {
                for (int i = 0; i < fields.size(); i++) {
//获取属性名称
                    Field f = (Field) fields.get(i);
                    fileName = f.getName();
//属性名第一个字母大写
                    str = fileName.substring(0, 1).toUpperCase();
//拼凑getXXX和setXXX方法名
                    getName = "get" + str + fileName.substring(1);
                    setName = "set" + str + fileName.substring(1);
//获取get、set方法
                    getMethod = c1.getMethod(getName, new Class[]{});
                    setMethod = c2.getMethod(setName, new Class[]{f.getType()});

                    //获取属性值
                    Object o = getMethod.invoke(o1, new Object[]{});
                    System.out.println(fileName + " : " + o);
                    //将属性值放入另一个对象中对应的属性
                    if (null != o) {
                        System.out.println("o2.setMethod = " + setMethod);
                        setMethod.invoke(o2, new Object[]{o});
                    }
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * @description 实现将一个List转化为Map<Key,List>的样式
     * @param
     * @return Map<Key,List>
     */
/*    public static Map<Object,Object> list2Map(List<Object> list) {

        Map<Object, List<Object>> map = new HashMap<Object, List<Object>>();

        if ((list != null) && (list.size() != 0)) {
            for (Object test : list) {
                Object testList = map.get(test.getClass().getDeclaredFields());
                if (testList == null) {
                    testList = new ArrayList<Object>();
                };
                map.put(test.getClass().getDeclaredFields(), testList);
            }
        }
        return map;
    }

 */
}