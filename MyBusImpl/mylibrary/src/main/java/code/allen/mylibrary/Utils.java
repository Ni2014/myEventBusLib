package code.allen.mylibrary;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by allenni on 2018/2/27.
 */

public final class Utils {
    public static List<Method> findAnnotatedMethods(Class<?> targetClass,Class<?> annotationClass){
        List<Method> methods = new ArrayList<>();
        Method[] declaredMethods = targetClass.getDeclaredMethods();
        for (Method method : declaredMethods) {
            // 过滤条件
            // 1 static的不要
            if (Modifier.isStatic(method.getModifiers())){
                continue;
            }
            // 2 非public的不要
            if (!Modifier.isPublic(method.getModifiers())){
                continue;
            }
            // 3 参数必须是一个
            if (method.getParameterTypes().length != 1){
                continue;
            }
            // 4 必须带有@BusReveiver注解
            if (!method.isAnnotationPresent(BusReceiver.class)){
                continue;
            }
            methods.add(method);
        }
        return methods;
    }
}
