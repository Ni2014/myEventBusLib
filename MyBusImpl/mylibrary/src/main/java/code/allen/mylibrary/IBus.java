package code.allen.mylibrary;

/**
 * Created by allenni on 2018/2/27.
 */

public interface IBus {
    void register(Object target);
    void unRegister(Object target);
    void post(Object event);
}
