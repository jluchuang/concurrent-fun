package ioc.container;

import ioc.annotation.SampleAutowired;
import ioc.reflect.ReflectUtil;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by chuang on 2017/8/21.
 */
public class SampleContainer implements Container {

    /**
     * 保存所有bean对象，格式为 com.xxx.Person : @52x2xa
     */
    private Map<String, Object> beans;

    /**
     * 存储bean和name的关系
     */
    private Map<String, String> beanKeys;

    public SampleContainer() {
        this.beans = new ConcurrentHashMap<String, Object>();
        this.beanKeys = new ConcurrentHashMap<String, String>();
    }

    @Override
    public <T> T getBean(Class<T> clazz) {
        String name = clazz.getName();
        Object object = beans.get(name);
        if (null != object) {
            return (T) object;
        }
        return null;
    }

    @Override
    public <T> T getBeanByName(String name) {
        String className = beanKeys.get(name);
        Object object = beans.get(className);
        if (null != object) {
            return (T) object;
        }
        return null;
    }

    @Override
    public Object registerBean(Object bean) {
        String name = bean.getClass().getName();
        beanKeys.put(name, name);
        beans.put(name, bean);
        return bean;
    }

    @Override
    public Object registerBean(Class<?> clazz) {
        String name = clazz.getName();
        beanKeys.put(name, name);
        Object bean = null;
        bean = ReflectUtil.newInstance(clazz);

        beans.put(name, bean);
        return bean;
    }

    @Override
    public Object registerBean(String name, Object bean) {
        String className = bean.getClass().getName();
        beanKeys.put(name, className);
        beans.put(className, bean);
        return bean;
    }

    @Override
    public void remove(Class<?> clazz) {
        String className = clazz.getName();
        if (null != className && !className.equals("")) {
            beanKeys.remove(className);
            beans.remove(className);
        }
    }

    @Override
    public void removeByName(String name) {
        String className = beanKeys.get(name);
        if (null != className && !className.equals("")) {
            beanKeys.remove(name);
            beans.remove(className);
        }
    }

    @Override
    public Set<String> getBeanNames() {
        return beanKeys.keySet();
    }

    @Override
    public void initWired() {
        Iterator<Map.Entry<String, Object>> it = beans.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> entry = (Map.Entry<String, Object>) it.next();
            Object object = entry.getValue();
            injection(object);
        }
    }

    /**
     * 注入对象
     *
     * @param object
     */
    public void injection(Object object) {
        // 所有字段
        try {
            Field[] fields = object.getClass().getDeclaredFields();
            for (Field field : fields) {
                // 需要注入的字段
                SampleAutowired autoWired = field.getAnnotation(SampleAutowired.class);
                if (null != autoWired) {

                    // 要注入的字段
                    Object autoWiredField = null;

                    String name = autoWired.name();
                    if (!name.equals("")) {
                        String className = beanKeys.get(name);
                        if (null != className && !className.equals("")) {
                            autoWiredField = beans.get(className);
                            System.out.println(String.format("Autowired %s by className. 134", name));
                        }
                        if (null == autoWiredField) {
                            throw new RuntimeException("Unable to load " + name);
                        }
                    } else {
                        if (autoWired.value() == Class.class) {
                            autoWiredField = recursiveAssembly(field.getType());
                            System.out.println(String.format("Autowired %s by value. 142", autoWired.value()));
                        } else {
                            // 指定装配的类
                            autoWiredField = this.getBean(autoWired.value());
                            if (null == autoWiredField) {
                                autoWiredField = recursiveAssembly(autoWired.value());
                                System.out.println(String.format("Autowired %s by value. 148", autoWired.value()));
                            }
                        }
                    }

                    if (null == autoWiredField) {
                        throw new RuntimeException("Unable to load " + field.getType().getCanonicalName());
                    }

                    boolean accessible = field.isAccessible();
                    field.setAccessible(true);
                    field.set(object, autoWiredField);
                    field.setAccessible(accessible);
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private Object recursiveAssembly(Class<?> clazz) {
        if (null != clazz) {
            return this.registerBean(clazz);
        }
        return null;
    }

}
