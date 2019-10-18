import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

public class Inspector {

    public void inspect(Object obj, boolean recursive) {
        Class c = obj.getClass();
        inspectClass(c, obj, recursive, 0);
    }

    private void inspectClass(Class c, Object obj, boolean recursive, int depth) {
        System.out.println("-------------------------------");
        formatOutput("Declaring Class", getClass(c));
        formatOutput("Immediate Superclass", getClass(getSuperClass(c)));
        formatOutput("Interface", formatArray(getInterfaces(c)));
        formatOutput("Constructors", formatArray(getConstructors(c)));
        formatOutput("Methods", formatArray(getMethods(c)));
        formatOutput("Fields", formatArray(getDeclaredFields(c)));
        formatOutput("Fields Value", formatArray(getDeclaredFieldsValue(c, obj)));

    }


    private void formatOutput(String header, String result){
        System.out.println(header + ": " + result);
    }

    private String getClass(Class c){
        return c.getSimpleName();
    }

    private Class getSuperClass(Class c){
        return c.getSuperclass();
    }

    private Class [] getInterfaces(Class c){
        return c.getInterfaces();
    }

    private <T> String formatArray(T [] array){
        if(array.length == 0){
            return "";
        }

        String retString = Arrays.toString(array).replace(", ", "\n-");
        retString = retString.substring(0,0) + "\n-" + retString.substring(1);
        retString = retString.substring(0,retString.length()-1); 
        return retString;
    }

    private Constructor [] getConstructors(Class c){
        return c.getConstructors();
    }

    private Method [] getMethods(Class c){
        return c.getMethods();    
    }

    private Field [] getDeclaredFields(Class c){
        return c.getDeclaredFields();
    }

    private Object [] getDeclaredFieldsValue(Class c, Object obj){
        Field [] allFields = getDeclaredFields(c);
        Object [] objectValues = new Object[allFields.length];
        for(int i = 0; i < allFields.length; i++){
            Field f = allFields[i];
            f.setAccessible(true);

            try{
                objectValues[i] = f.get(obj);
            }
            catch(Exception e){

            }
        }
        return objectValues;
    }
    
}