package com.aboutyang.common.context;

/**
 * 线程缓存管理器<br>
 * 由于应用服务器使用线程池，所以线程被重用时，会有多个request公用同一个空间<br>
 * 确保每一次请求结束时，清空线程级缓存，防止出现冲突<br>
 * @author yangwang【yangwang@jd.com】
 * @date 2018/8/24 19:56
 */
public class ThreadContextManager {


    private static ThreadLocal<ThreadContext> threadContext = new ThreadLocal<ThreadContext>() {
        @Override
        protected synchronized ThreadContext initialValue() {
            return new ThreadContext();
        }
    };

    /**
     * 获取线程上下文对象
     */
    public static ThreadContext getThreadContext() {
        return threadContext.get();
    }

    /**
     * 清除ThreadContext
     */
    public static void clear() {
        threadContext.get().clear();
        threadContext.remove();
    }

    /****
     * 判断是否缓存内容为空
     *
     * @return
     */
    public static boolean isEmpty() {
        return threadContext.get().isEmpty();
    }

    /****
     * 获取所有缓存的字符串显示
     *
     * @return
     */
    public static String getAllCashedInf() {
        return threadContext.get().getAllCashedInf();
    }

    /**
     * 检查缓存是否存在
     *
     * @param key
     * @return
     */
    public static boolean containsKey(String key) {
        return threadContext.get().containsKey(key);
    }

    /**
     * 缓存Object
     *
     * @param key
     * @return
     */
    public static void put(String key, Object value) {
        threadContext.get().put(key, value);
    }

    /**
     * 获取缓存Object
     *
     * @param key
     * @return
     */
    public static Object get(String key) {
        return threadContext.get().get(key);
    }

    /**
     * 删除Context的key值
     *
     * @param key
     */
    public static void remove(String key) {
        if (containsKey(key)) {
            threadContext.get().remove(key);
        }
    }

}
