package fr.isima.yk.container;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import javax.inject.*;

public class MyContainer {
    private Map<Class, Class> binds = new HashMap<>();
    private Map<Class, Boolean> isSing = new HashMap<>();
    private Map<Class, Object> singletons = new HashMap<>();


    public void bind(Class c1, Class c2) {
        binds.put(c1, c2);
        isSing.put(c1, false);
    }

    public void bind(Class c1, Class c2, boolean isSingleton) {
        binds.put(c1, c2);
        isSing.put(c1, isSingleton);
    }

    private Object getBindInstance(Class c) {

        Object o = null;

        if(!isSing.get(c)) {
            try {
                o = binds.get(c).newInstance();
            }
            catch(InstantiationException e) {
                System.out.println("Erreur");
            }
            catch(IllegalAccessException e) {
                System.out.println("Erreur");
            }
        }
        else {
            if(singletons.get(c) != null)
                o = singletons.get(c);
            else
            {
                try {
                    singletons.put(c, binds.get(c).newInstance());
                }
                catch(InstantiationException e) {
                    System.out.println("Erreur");
                }
                catch(IllegalAccessException e) {
                    System.out.println("Erreur");
                }

                o = singletons.get(c);
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
            if(constructors[i].isAnnotationPresent(MyInject.class))
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
            args.add(getBindInstance(parameterTypes[i]));
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
                    method.invoke(inst, getBindInstance(method.getParameterTypes()[0]));
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
                    field.set(inst, getBindInstance(field.getType()));
                }
                catch(IllegalAccessException e) {
                    System.out.println("Erreur");
                }
            }
        }
        return inst;
    }
}
