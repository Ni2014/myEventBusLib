# 写一个自己的event库
    在使用BmobSDK开发App的时候，会有很多异步回调，类似如登录成功，插入数据成功的事件，虽然V3.5.0开始内部用rxjava去重构，也提供
    了rx风格的api，不过写事件回调的方法还是比较烦，这时候你可以会用下流行的EventBus库。下面就逐渐实现一个简单的Demo，第一个版
    本，先不考虑优化和性能，线程切换之类的！
## 没有事件总线库之前
    你可能写接口回调，发广播，但是Activity和Fragment等组件交互也有点麻烦，写起来代码都是比较冗余的！
## 用了之后
    代码优雅，依赖于注解，使得代码分离开来，在需要的时候send事件，对应注解到的特定方法就会被调用到，其实类似的还有Otto这个库。
## 开始实现
### 用法实例
    // 注册事件库
    Bus.getDefault().register(this);
    // 创建自定义事件并发送事件
    Event event = new Event();
    event.setUserId("111");
    Bus.getDefault().post(event);
    // 指定了特定注解的合理方法会被调用
    @BusReceiver
    public void onEvent(Event event){
        System.out.println("getEvent " + event.getUserId());
    }
    // 注销事件库
    Bus.getDefault().unRegister(this);

基本和EventBus的Api有点类似。

    
### 自定义注解
    
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface BusReceiver {
    }
第一个版本先不考虑事件调度模型，所以该注解目前只是作为代码标识，为了内部处理而已。
### 注解处理
#### 基本思路
##### 1 注册事件库
     注册事件库的时候可以拿到该注册类如Activty和对应改类中包涵了@BusReceiver注解的方法，
     并创建mMethodMap中(类型为Map<Object,List<Method>>),便于后面查找。
##### 2 发送事件
     此时根据事件类型，去mMethodMap中遍历，事件类型判断通过就调用method.invoke()调用
     特定的注解方法。
     
 基本都是对reflect下的api使用，比如拿到该类的方法，判断注解并处理等的操作！
#### 代码

    public interface IBus {
        void register(Object target);
        void unRegister(Object target);
        void post(Object event);
    }
    
    
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
 
### more
    基本的事件驱动思路就是这样，转换思路换来代码的整洁，特别是App客户端事件比较多
    的情况比较适合用类似的库，不过还有一些不足，后续继续改进：
    1 没加入调度和分发模型，指定方法在特定的线程回调；
    2 方法查找的效率问题，可以放到map缓存，标准库jdk，Android SDK中的方法是可以跳过的；
    3 注解处理的效率问题，判断符合方法的条件的顺序是不是可以调整下；
    4 可以适当对方法Method对象做一定的抽象；
    5 还没加入支持事件继承；

