package code.allen.mylibrary.v1;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by allenni on 2018/2/27.
 */

public final class Utils {
    public static List<Method> findAnnotatedMethods(Class<?> targerType,Class<? extends BusReceiver> annotation){
        List<Method> methods = new ArrayList<>();
        Method[] declaredMethods = targerType.getDeclaredMethods();
        for (Method method : declaredMethods) {
            // 过滤
            // 1 static的不要
            if (Modifier.isStatic(method.getModifiers())){
                continue;
            }
            // 2 非public的不要
            if (!Modifier.isPublic(method.getModifiers())){
                continue;
            }
            // 3 方法参数是一个
            if (method.getParameterTypes().length != 1){
                continue;
            }
            // 4 必须带有@BusReceiver注解的
            if (!method.isAnnotationPresent(BusReceiver.class)){
                continue;
            }
            methods.add(method);
        }
        return methods;
    }
}
