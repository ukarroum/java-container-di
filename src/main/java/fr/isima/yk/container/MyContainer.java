package fr.isima.yk.container;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import javax.inject.*;

public class MyContainer {
    private Map<Class, Class> binds = new HashMap<>();

    public void bind(Class c1, Class c2) {
        binds.put(c1, c2);
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

        Class[] parameterTypes = cons.getParameterTypes();

        List<Object> args = new ArrayList<>();

        for(int i = 0; i < parameterTypes.length; i++)
        {
            try{
                args.add(binds.get(parameterTypes[i]).newInstance());
            }
            catch(InstantiationException e) {
                System.out.println("Erreur");
            }
            catch(IllegalAccessException e) {
                System.out.println("Erreur");
            }
        }

        T inst = null;

        try{
            inst = (T)cons.newInstance(args.toArray());
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

        /* Setters Injection */

        Method[] methods = c.getMethods();

        for(Method method : methods) {
            if(method.getName().startsWith("set") && method.isAnnotationPresent(Inject.class)) {
                try {
                    method.invoke(inst, binds.get(method.getParameterTypes()[0]).newInstance());
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
            }
        }

        /* Fields Injection */

        Field[] fields = c.getDeclaredFields();

        for(Field field: fields) {
            if(field.isAnnotationPresent(Inject.class)) {
                try {
                    field.setAccessible(true);
                    field.set(inst, binds.get(field.getClass()).newInstance());

                }
                catch(InstantiationException e) {
                    System.out.println("Erreur");
                }
                catch(IllegalAccessException e) {
                    System.out.println("Erreur");
                }
            }
        }

        return inst;
    }

}
