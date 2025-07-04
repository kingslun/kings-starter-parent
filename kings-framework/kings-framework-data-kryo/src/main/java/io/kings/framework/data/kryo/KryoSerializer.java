package io.kings.framework.data.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import io.kings.framework.core.bean.BeanLifecycle;
import io.kings.framework.data.exception.SerializeException;
import io.kings.framework.data.serializer.Serializer;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;


/**
 * <p>
 * jdk对象序列化
 * </p>
 *
 * @author lun.kings
 * @date 2020/8/1 2:47 下午
 * @since v3.2.3
 */
public class KryoSerializer implements Serializer, BeanLifecycle {

    /**
     * kryo内部使用的thread local
     */
    private static final ThreadLocal<Kryo> KRYO_THREAD_LOCAL;

    static {
        KRYO_THREAD_LOCAL = ThreadLocal.withInitial(Kryo::new);
    }

    /**
     * 序列化对象的class
     */
    private final Class<Object> clazz;

    public KryoSerializer() {
        clazz = Object.class;
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
        Kryo kryo = KRYO_THREAD_LOCAL.get();
        kryo.setReferences(false);
        kryo.register(clazz);
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             Output output = new Output(bos)) {
            kryo.writeClassAndObject(output, serializable);
            output.flush();
            return bos.toByteArray();
        } catch (Exception e) {
            throw new SerializeException("kryo serialize failed " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * 反序列化对象
     *
     * @param bytes data bytes
     * @return obj
     * @throws SerializeException 序列化异常
     */
    @Override
    @SuppressWarnings("unchecked")
    public <E extends Serializable> E deserialize(byte[] bytes) throws SerializeException {
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        Kryo kryo = KRYO_THREAD_LOCAL.get();
        kryo.setReferences(false);
        kryo.register(clazz);
        try (Input input = new Input(bytes)) {
            return (E) kryo.readClassAndObject(input);
        } catch (Exception e) {
            throw new SerializeException("kryo deserialize failed " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * destroy failed
     */
    @Override
    public void destroy() {
        KRYO_THREAD_LOCAL.remove();
    }

    /**
     * init failed
     */
    @Override
    public void complete() {
        //nothing to do
    }
}
