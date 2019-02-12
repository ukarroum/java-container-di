package fr.isima.yk.container;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import javax.inject.*;

public class MyContainer {
    private Multimap<Class, Class> binds = HashMultimap.create();
    private Map<Class, Boolean> isSing = new HashMap<>();
    private Map<Class, Object> singletons = new HashMap<>();


    public void bind(Class c1, Class c2) {
        binds.put(c1, c2);
        isSing.put(c2, false);
    }

    public void bind(Class c1, Class c2, boolean isSingleton) {
        binds.put(c1, c2);
        isSing.put(c2, isSingleton);
    }

    private Object getBindInstance(Class c, Class c2) {

        Object o = null;

        if(c2 == Class.class)
            c2 = binds.get(c).iterator().next();

        if(!isSing.get(c2)) {
            try {
                o = c2.newInstance();
            }
            catch(InstantiationException e) {
                System.out.println("Erreur");
            }
            catch(IllegalAccessException e) {
                System.out.println("Erreur");
            }
        }
        else {
            if(singletons.get(c2) != null)
                o = singletons.get(c2);
            else
            {
                try {
                    singletons.put(c2, c2.newInstance());
                }
                catch(InstantiationException e) {
                    System.out.println("Erreur");
                }
                catch(IllegalAccessException e) {
                    System.out.println("Erreur");
                }

                o = singletons.get(c2);
            }
        }

        return o;
    }

    public <T> T newInstance(Class c) {

        /* Constructor Injection */

        Constructor[] constructors = c.getConstructors();
        Constructor cons = null;

        for(int i = 0; i < constructors.length; i++)
        {
            if(constructors[i].isAnnotationPresent(Inject.class))
            {
                cons = constructors[i];
                break;
            }
        }

        Class[] parameterTypes = {};

        /* Check if we have a constructor annoted with Inject */
        if(cons != null) {
            parameterTypes = cons.getParameterTypes();
        }

        List<Object> args = new ArrayList<>();

        for(int i = 0; i < parameterTypes.length; i++)
        {
            args.add(getBindInstance(parameterTypes[i], Class.class));
        }

        T inst = null;

        try{
            if(cons != null)
                inst = (T)cons.newInstance(args.toArray());
            else
                inst = (T)c.getConstructor().newInstance();
        }
        catch(InstantiationException e) {
            System.out.println("Erreur");
        }
        catch(IllegalAccessException e) {
            System.out.println("Erreur");
        }
        catch(InvocationTargetException e) {
            System.out.println("Erreur");
        }
        catch(NoSuchMethodException e) {
            System.out.println("Erreur");
        }


        /* Setters Injection */

        Method[] methods = c.getMethods();

        for(Method method : methods) {
            if(method.getName().startsWith("set") && method.isAnnotationPresent(MyInject.class)) {
                try {
                    method.invoke(inst, getBindInstance(method.getParameterTypes()[0], method.getAnnotation(MyInject.class).name()));
                }
                catch(IllegalAccessException e) {
                    System.out.println("Erreur");
                }
                catch(InvocationTargetException e) {
                    System.out.println("Erreur");
                }
            }
        }

        /* Fields Injection */

        Field[] fields = c.getDeclaredFields();

        for(Field field: fields) {
            if(field.isAnnotationPresent(MyInject.class)) {
                try {
                    field.setAccessible(true);
                    field.set(inst, getBindInstance(field.getType(), field.getAnnotation(MyInject.class).name()));
                }
                catch(IllegalAccessException e) {
                    System.out.println("Erreur");
                }
            }
        }
        return inst;
    }
}
