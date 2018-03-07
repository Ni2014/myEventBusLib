package code.allen.mylibrary;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by allenni on 2018/2/27.
 */

public class Bus implements IBus {
    // 1 reg时 把target类型 记录
    // 2 通过target类型(Class）找到含有指定@BusReceiver注解的方法 并拿到方法参数类型 以及事件类型
    // map<targetType,eventType>
    // post调用 Bus需要根据发送的事件类型找到 map中含有该事件类型的target中的方法集合并调用方法

    private static Bus INSTANCE = null;

    // 某target下的方法集合
    private Map<Object,List<Method>> mMethodMap = new HashMap<>();

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

    @Override
    public void register(Object target) {
        // 找到target下的带有@BusReceiver注解的合法方法并加到mMethodMap中
        List<Method> annotatedMethods = Utils.findAnnotatedMethods(target.getClass(), BusReceiver.class);
        mMethodMap.put(target,annotatedMethods);
    }

    @Override
    public void unRegister(Object target) {
        mMethodMap.remove(target);
    }

    @Override
    public void post(Object event) {
        Class<?> eventClass = event.getClass();
        // mMethodMap中的方法 需要判断事件类型
        for (Map.Entry<Object, List<Method>> entry : mMethodMap.entrySet()) {
            Object target = entry.getKey();
            List<Method> methods = entry.getValue();
            if (methods == null || methods.isEmpty()){
                continue;
            }
            for (Method method : methods) {
                if (eventClass.equals(method.getParameterTypes()[0])){
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
