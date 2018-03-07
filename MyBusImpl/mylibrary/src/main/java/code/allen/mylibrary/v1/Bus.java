package code.allen.mylibrary.v1;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by allenni on 2018/2/27.
 */

public class Bus {
    // 不同的target和对应的方法集合
    private Map<Object,List<Method>> methodMap = new HashMap<>();
    private static Bus INSTANCE = null;

    public static Bus getDefault(){
        if (INSTANCE == null){
            synchronized (Bus.class){
                if (INSTANCE == null){
                    INSTANCE = new Bus();
                }
            }
        }
        return INSTANCE;
    }
    public void register(Object target){
        List<Method> annotatedMethods = Utils.findAnnotatedMethods(target.getClass(), BusReceiver.class);
        methodMap.put(target,annotatedMethods);
    }

    public void unRegister(Object target){
        methodMap.remove(target);
    }

    public void post(Object event){
        // eventType
        Class<?> eventType = event.getClass();
        for (Map.Entry<Object, List<Method>> entry : methodMap.entrySet()) {
            Object target = entry.getKey();
            List<Method> methods = entry.getValue();
            for (Method method : methods) {
                // 方法参数的类型和事件类型匹配上才调用(invoke)
                if (eventType.equals(method.getParameterTypes()[0])){
                    try {
                        method.invoke(target,event);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
