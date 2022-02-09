package io.kings.framework.data.serializer;

import io.kings.framework.core.Nameable;
import io.kings.framework.core.bean.BeanLifecycle;
import io.kings.framework.data.exception.SerializeException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * 序列化接口 序列化zookeeper读写的value类型 必须包含序列化和反序列化方法
 *
 * @author lun.kings
 * @date 2020/7/6 4:48 下午
 * @since v2.8.6
 */
public interface Serializer extends BeanLifecycle, Nameable {

    byte[] EMPTY_BYTE_ARRAY = new byte[0];
    int DEFAULT_BUFFER_SIZE = 1024;
    String SERIALIZE_ENABLE_PREFIX = "kings.serialization.enabled";
    /**
     * 默认jdk序列化器
     */
    Serializer DEFAULT_ = new Serializer() {
        @Override
        public String name() {
            return "JDKSerializer";
        }

        /**
         * 序列化参数对象
         *
         * @param serializable 参数对象
         * @return serialized bytes
         * @throws SerializeException 序列化异常
         */
        @Override
        public <E extends Serializable> byte[] serialize(E serializable) throws SerializeException {
            if (serializable == null) {
                return EMPTY_BYTE_ARRAY;
            }
            try (ByteArrayOutputStream bos = new ByteArrayOutputStream(DEFAULT_BUFFER_SIZE);
                ObjectOutputStream oos = new ObjectOutputStream(bos)) {
                oos.writeObject(serializable);
                oos.flush();
                return bos.toByteArray();
            } catch (Exception e) {
                throw new SerializeException("jdk serialize failed " + e.getLocalizedMessage(), e);
            }
        }

        /**
         * 反序列化
         *
         * @param bytes data bytes
         * @return obj
         * @throws SerializeException 序列化异常
         */
        @Override
        public <E extends Serializable> E deserialize(byte[] bytes) throws SerializeException {
            try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                ObjectInputStream ois = new ObjectInputStream(bis)) {
                @SuppressWarnings("unchecked")
                E e = (E) ois.readObject();
                return e;
            } catch (Exception e) {
                throw new SerializeException("jdk deserialize failed " + e.getLocalizedMessage(),
                    e);
            }
        }
    };

    /**
     * 序列化参数对象
     *
     * @param e 参数对象
     * @return serialized bytes
     * @throws SerializeException 序列化异常
     */
    <D extends Serializable> byte[] serialize(D e) throws SerializeException;

    /**
     * 反序列化
     *
     * @param bytes data bytes
     * @return obj
     * @throws SerializeException 序列化异常
     */
    <D extends Serializable> D deserialize(byte[] bytes) throws SerializeException;

    @Override
    default String name() {
        return getClass().getSimpleName();
    }
}
