package fr.isima.yk.container;

/*

    Authors :

            Imad Enneiymy
            Yassir Karroum

    Purpose :

            Our solution for a programming assignement at ISIMA 3rd year, the whole point of this is to create
            a dependency injection container, like the one present in multiple frameworks (spring, symfony, etc.).

            The working directory is not very clean (a lot of MovieLister{i}) we may fix that in the future (or not).
            The main unit tests used are in the MyContainerTest.

    Special Thanks :

            We would like to thank our friend and collegue : Reda Benchraa [redabenchraa@gmail.com] for the help
            and guidance in this subject.

 */

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import javax.inject.*;

public class MyContainer {

    /* Here we use some data structures to ease our work :

    1) Binds : A MultiMap that stores all the implementations of a given class,
                for example (based on the programming assignement :
                AuditService -> SimpleAuditService.

                We should note here that one abstract class may have multiple implementations.
     2) isSing : Should the given Class c implemented using the singleton pattern ? Can be useful for some applications.
     3) singletons : contains the list of the implemented singletons.
     4) AutoWired : What classes are autowired ? (check the programming assignement for more info).

     */
    private Multimap<Class, Class> binds = HashMultimap.create();
    private Map<Class, Boolean> isSing = new HashMap<>();
    private Map<Class, Object> singletons = new HashMap<>();
    private HashSet<Class> autoWired = new HashSet<Class>();

    /* The binding function, we have two versions :

        1) The first bind without taking into consideration the singleton pattern (in fact it s basically the same as
        the second one except the thirs parameter is always false).
        2) May be used to specify if we want to use the singleton pattern or not.
     */
    public void bind(Class c1, Class c2) {
        binds.put(c1, c2);
        isSing.put(c2, false);
    }

    public void bind(Class c1, Class c2, boolean isSingleton) {
        binds.put(c1, c2);
        isSing.put(c2, isSingleton);
    }

    /*
        Not very "user friendly" but makes our work easier, this can be used to "activate" AutoWiring for a given class.
        We use (as proposed by the programming assignement) AutoWiring by name.
     */
    public void setAutoWiring(Class c) {
        autoWired.add(c);
    }

    /*
        This function return the object that we asked for, taking into consideration that we may be in singleton pattern
        and don t really need to create a new object, more in the function body.
     */
    private Object getBindInstance(Class c, Class c2) {

        Object o = null;

        /* Since we allow the user to choose it s prefered implementation for a given class, we use Class.class as
            a default argument to basically say : Just pick whatever implementation that you want, this is generally useful
            if we only have one implementation, if the user have more than one implementation, the one choosed in undefined,
            we just pick one "randomly".
         */
        if(c2 == Class.class)
            c2 = binds.get(c).iterator().next();

        /* Not Singleton ? Create  a new instance, note that we use a recursive version to manage graph resolution */
        if(!isSing.get(c2)) {
                o = newInstance(c2);
        }

        /* We re using a singleton pattenr */
        else {

            /* The instance is already created, we just return it */
            if(singletons.get(c2) != null)
                o = singletons.get(c2);
            else
            {
                /* The first time ? we create an instance */
                o = newInstance(c2);

                singletons.put(c2, o);
            }
        }

        return o;
    }

    /* This is the main function, a lot of reflexivity is involved, we use the annotation @Inject for the constructor,
            and MyInject (so that the user can choose an implementation) for the setters and fields.

            TODO : The exception handling done in this portion of code is terrible (a simple print ), we should definitly fix this.
     */
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

        /* Auto Wiring Injection */

        for(Field field: fields) {
            if(!field.isAnnotationPresent(MyInject.class)) {
                if(autoWired.contains(field.getType()))
                {
                    try {
                        field.setAccessible(true);
                        field.set(inst, getBindInstance(field.getType(), Class.class));
                    }
                    catch(IllegalAccessException e) {
                        System.out.println("Erreur");
                    }
                }
            }
        }

        return inst;
    }
}
