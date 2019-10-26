import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Array;

public class Inspector {

    public void inspect(Object obj, boolean recursive) {
        Class c = getClass(obj);
        inspectClass(c, obj, recursive, 0);
    }

    private void inspectClass(Class c, Object obj, boolean recursive, int depth) {

        formatOutputDepth("Declaring Class Name: " + getSimpleName(c), depth);
        inspectImmediateSuperClass(c, obj, recursive, depth);
        inspectInterfaces(c, obj, recursive, depth);
        inspectConstructors(c, obj, recursive, depth);
        inspectMethods(c, obj, recursive, depth);
        inspectFields(c, obj, recursive, depth);
    }    

    private <T> boolean checkArrayEmpty( T [] array){
        if(array.length == 0){
            return true;
        }
        return false;
    }
    
    private void formatOutputDepth(String value, int depth){
        for(int i = 0; i < depth; i++){
            System.out.print("\t");
        }
        System.out.println(value);
    }

    private void inspectImmediateSuperClass(Class c, Object obj, boolean recursive, int depth){
        if(getSuperClass(c) == null){
            formatOutputDepth("Super Class Name: " + "none", depth);
        }

        else{
            formatOutputDepth("Super Class Name: " + getSimpleName(getSuperClass(c)),depth);
            inspectClass(getSuperClass(c), obj, recursive, depth+1);
        }
    }

    private void inspectInterfaces(Class c, Object obj, boolean recursive, int depth){
        if(checkArrayEmpty(getInterfaces(c))){
            formatOutputDepth("Interface Name: "  + "none", depth);
        }

        else{
            for(Class inter: getInterfaces(c)){
                formatOutputDepth("Interface Name: " + getSimpleName(inter),depth);
                inspectClass(inter, obj, recursive, depth+1);
            }
        }
    }

    private void inspectConstructors(Class c, Object obj, boolean recursive, int depth){
        if(checkArrayEmpty(getDeclaredConstructors(c))){
            formatOutputDepth("Constructor Name: " + "none",depth);
        }

        else{
            for(Constructor cons: getDeclaredConstructors(c)){
                formatOutputDepth("Constructor Name: " + cons.getName(), depth);

                if(checkArrayEmpty(cons.getParameterTypes())){
                    formatOutputDepth("- Parameter: " + "none", depth+1);
                }

                else{
                    for(Class par: cons.getParameterTypes()){
                        formatOutputDepth("- Parameter: " + par.getName(), depth+1);
                    }     
                }

                formatOutputDepth("- Modifier: " + ModifierToString(cons.getModifiers()), depth+1);

            }
        }
    }

    private void inspectMethods(Class c, Object obj, boolean recursive, int depth){
        if(checkArrayEmpty(getDeclaredMethods(c))){
            formatOutputDepth("Method Name: " + "none", depth);
        }

        else{
            for(Method meth : getDeclaredMethods(c)){
                formatOutputDepth("Method Name: " + meth.getName(), depth);

                if(checkArrayEmpty(meth.getExceptionTypes())){
                    formatOutputDepth("- Exception Type: " + "none", depth+1);
                }
                else{
                    for(Class except: meth.getExceptionTypes()){
                        formatOutputDepth("- Exception Type: " + except.getName(), depth+1);
                    }
                }

                if(checkArrayEmpty(meth.getParameterTypes())){
                    formatOutputDepth("- Parameter Type: " + "none", depth+1);
                }

                else{
                    for(Class parType : meth.getParameterTypes()){
                        formatOutputDepth("- Parameter Type: " + getSimpleName(parType), depth+1);
                    }
                }

                if(meth.getReturnType() == null){
                    formatOutputDepth("- Return Type: " + "none", depth+1);
                }

                else{
                    formatOutputDepth("- Return Type: " + meth.getReturnType(), depth+1);
                }

                formatOutputDepth("- Modifier: " + ModifierToString(meth.getModifiers()), depth+1);
            }
        }
    }

    private void inspectFields(Class c, Object obj, boolean recursive, int depth){
        if(checkArrayEmpty(getDeclaredFields(c))){
            formatOutputDepth("Declared Fields: " + "none", depth);
        }

        else{
            for(Field field: getDeclaredFields(c)){
                formatOutputDepth("Declared Field Name: " + field.getName(), depth);

                if(field.getType().isArray()){
                    formatOutputDepth("- Type: " + "Array", depth+1);
                }

                else{
                    formatOutputDepth("- Type: " + field.getType().getName(), depth+1);
                }

                formatOutputDepth("- Modifier: " + ModifierToString(field.getModifiers()), depth+1);

                field.setAccessible(true);
                try{
                    if(field.getType().isPrimitive()){
                        formatOutputDepth("- Value: " + field.get(obj), depth+1);
                    }

                    else if(field.getType().isArray()){
                        handelArrayField(field.getType(), field.get(obj), recursive, depth);
                    }

                    //recursive 
                    else{
                        if(recursive){
                            inspectClass(getClass(field.get(obj)), obj, recursive, depth);
                        }

                        else{
                            formatOutputDepth("- Value: " + getObjectReference(obj, field), depth+1);
                        }
                    }

                }

                catch(NullPointerException e){
                    formatOutputDepth("- Value: " + "null", depth+1);
                }

                catch(IllegalAccessException e){
                    formatOutputDepth("- Value: Error! Cannot Access field", depth+1);
                }

                catch(Exception e){
                    formatOutputDepth("-Value: Error! Something went really wrong, trace message-->" + e, depth+1);
                }

            }
        }
    }

    private void handelArrayField(Class c, Object obj, boolean recursive, int depth){
        formatOutputDepth("- Component Type: " + getSimpleName(c.getComponentType()), depth+1);
        formatOutputDepth("- Length: " + ArrayGetLength(obj), depth+1);
        formatOutputDepth("- Contents: ", depth+1);
        
        for(int i = 0; i < ArrayGetLength(obj); i++){
            if(ArrayGetObj(obj, i) == null){
                formatOutputDepth("- " + "null", depth+2);
            }
            else if(c.getComponentType().isPrimitive()){
                formatOutputDepth("- " + getSimpleName(getClass(ArrayGetObj(obj, i))), depth+2);
            }
            //2d arrays
            else if(c.getComponentType().isArray()){
                handelArrayField(getClass(ArrayGetObj(obj, i)), ArrayGetObj(obj, i), recursive, depth+2);
            }
            //recursive 
            else{
                if(recursive){
                    if(recursive){
                        inspectClass(getClass(ArrayGetObj(obj, i)), ArrayGetObj(obj, i), recursive, depth+2);
                    }

                    else{
                        formatOutputDepth("- Value: " + getObjectReference(obj, i), depth+2);
                    }
                }
            }
        }
    }

    private Class getSuperClass(Class c){
        return c.getSuperclass();
    }

    private Class [] getInterfaces(Class c){
        return c.getInterfaces();
    }

    private String getSimpleName(Class c){
        return c.getSimpleName();
    }

    private Constructor [] getDeclaredConstructors(Class c){
        return c.getDeclaredConstructors();
    }
    
    private Class getClass(Object obj)
	{
		return obj.getClass();
    }
    
    private Object ArrayGetObj(Object obj, int i){
        return Array.get(obj, i);
    }

    private int ArrayGetLength(Object obj){
        return Array.getLength(obj);
    }

    private Field [] getDeclaredFields(Class c){
        return c.getDeclaredFields();
    }

    private Method [] getDeclaredMethods(Class c){
        return c.getDeclaredMethods();
    }

    private String ModifierToString (int i){
        return Modifier.toString(i);
    }

    private String getObjectReference(Object obj, int i){
        String className = getClass(ArrayGetObj(obj, i)).getName();
        String hashCode = Integer.toHexString(System.identityHashCode(ArrayGetObj(obj, i)));
        return className + "@" + hashCode; 
    }

    private String getObjectReference(Object obj, Field field){
        String className = "";
        String hashCode = "";
        try{
            className = getClass(field.get(obj)).getName();
            hashCode = Integer.toHexString(System.identityHashCode(field.get(obj)));
        }
        catch(Exception e){
            System.out.println("Error: something happened" + e);
        }
        return className + "@" + hashCode; 
    }
}