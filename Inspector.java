import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Array;

public class Inspector {

    public void inspect(Object obj, boolean recursive) {
        Class c = obj.getClass();
        inspectClass(c, obj, recursive, 0);
    }

    private void inspectClass(Class c, Object obj, boolean recursive, int depth) {

        formatOutputDepth("Declaring Class Name: " + c.getSimpleName(), depth);
        getImmediateSuperClass(c, obj, recursive, depth);
        getInterfaces(c, obj, recursive, depth);
        getConstructors(c, obj, recursive, depth);
        getMethods(c, obj, recursive, depth);
        getFields(c, obj, recursive, depth);
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

    private void getImmediateSuperClass(Class c, Object obj, boolean recursive, int depth){
        if(c.getSuperclass() == null){
            formatOutputDepth("Super Class Name: " + "none", depth);
        }

        else{
            formatOutputDepth("Super Class Name: " + c.getSuperclass().getSimpleName(),depth);
            inspectClass(c.getSuperclass(), obj, recursive, depth+1);
        }
    }

    private void getInterfaces(Class c, Object obj, boolean recursive, int depth){
        if(checkArrayEmpty(c.getInterfaces())){
            formatOutputDepth("Interface Name: "  + "none", depth);
        }
        else{
            for(Class inter: c.getInterfaces()){
                formatOutputDepth("Interface Name: " + inter.getSimpleName(),depth);
                inspectClass(inter, obj, recursive, depth+1);
            }
        }
    }

    private void getConstructors(Class c, Object obj, boolean recursive, int depth){
        if(checkArrayEmpty(c.getDeclaredConstructors())){
            formatOutputDepth("Constructor Name: " + "none",depth);
        }

        else{
            for(Constructor cons: c.getDeclaredConstructors()){
                formatOutputDepth("Constructor Name: " + cons.getName(), depth);

                if(checkArrayEmpty(cons.getParameterTypes())){
                    formatOutputDepth("- Parameter: " + "none", depth+1);
                }

                else{
                    for(Class par: cons.getParameterTypes()){
                        formatOutputDepth("- Parameter: " + par.getName(), depth+1);
                    }     
                }

                formatOutputDepth("- Modifier: " + Modifier.toString(cons.getModifiers()), depth+1);

            }
        }
    }

    private void getMethods(Class c, Object obj, boolean recursive, int depth){
        if(checkArrayEmpty(c.getDeclaredMethods())){
            formatOutputDepth("Method Name: " + "none", depth);
        }

        else{
            for(Method meth : c.getDeclaredMethods()){
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
                        formatOutputDepth("- Parameter Type: " + parType.getSimpleName(), depth+1);
                    }
                }

                if(meth.getReturnType() == null){
                    formatOutputDepth("- Return Type: " + "none", depth+1);
                }

                else{
                    formatOutputDepth("- Return Type: " + meth.getReturnType(), depth+1);
                }

                formatOutputDepth("- Modifier: " + Modifier.toString(meth.getModifiers()), depth+1);
            }
        }
    }

    private void getFields(Class c, Object obj, boolean recursive, int depth){
        if(checkArrayEmpty(c.getDeclaredFields())){
            formatOutputDepth("Declared Fields: " + "none", depth);
        }

        else{
            for(Field field: c.getDeclaredFields()){
                formatOutputDepth("Declared Field Name: " + field.getName(), depth);

                if(field.getType().isArray()){
                    formatOutputDepth("- Type: " + "Array", depth+1);
                }

                else{
                    formatOutputDepth("- Type: " + field.getType().getName(), depth+1);
                }

                formatOutputDepth("- Modifier: " + Modifier.toString(field.getModifiers()), depth+1);

                field.setAccessible(true);
                try{
                    if(field.getType().isPrimitive()){
                        formatOutputDepth("- Value: " + field.get(obj), depth+1);
                    }

                    else if(field.getType().isArray()){
                        handelArrayField(field.getType(), field.get(obj), recursive, depth);
                    }

                    //type of class 
                    else{
                        if(recursive){
                            inspectClass(field.get(obj).getClass(), obj, recursive, depth);
                        }

                        else{
                            formatOutputDepth("- Value: " + field.get(obj).getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(field.get(obj))), depth+1);
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
        formatOutputDepth("- Component Type: " + c.getComponentType().getSimpleName(), depth+1);
        formatOutputDepth("- Length: " + Array.getLength(obj), depth+1);
        formatOutputDepth("- Contents: ", depth+1);
        
        for(int i = 0; i < Array.getLength(obj); i++){
            if(Array.get(obj, i) == null){
                formatOutputDepth("- " + "null", depth+2);
            }
            else if(c.getComponentType().isPrimitive()){
                formatOutputDepth("- " + Array.get(obj, i).getClass().getSimpleName(), depth+2);
            }
            //2d arrays
            else if(c.getComponentType().isArray()){
                handelArrayField(Array.get(obj, i).getClass(), Array.get(obj, i), recursive, depth+2);
            }
            //recursive 
            else{
                if(recursive){
                    if(recursive){
                        inspectClass(Array.get(obj, i).getClass(), Array.get(obj, i), recursive, depth+2);
                    }

                    else{
                        formatOutputDepth("- Value: " + Array.get(obj, i).getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(Array.get(obj, i))), depth+2);
                    }
                }
            }
        }
    }
    
}