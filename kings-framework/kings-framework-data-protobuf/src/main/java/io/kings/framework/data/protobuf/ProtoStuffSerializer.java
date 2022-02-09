package io.kings.framework.data.protobuf;

import io.kings.framework.data.exception.SerializeException;
import io.kings.framework.data.serializer.Serializer;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.runtime.RuntimeSchema;
import java.io.Serializable;
import java.util.Optional;


/**
 * proto stuff 序列化器
 *
 * @author lun.kings
 * @date 2020/8/1 2:47 下午
 * @since v3.2.3
 */
public class ProtoStuffSerializer implements Serializer {

    /**
     * schema thread local
     */
    private final ThreadLocal<RuntimeSchema<? extends Serializable>> schemaThreadLocal;

    public ProtoStuffSerializer() {
        schemaThreadLocal = new ThreadLocal<>();
    }

    /**
     * 序列化参数对象
     *
     * @param serializable 参数对象
     * @return serialized bytes
     * @throws SerializeException 序列化异常
     */
    @Override
    @SuppressWarnings("unchecked")
    public <E extends Serializable> byte[] serialize(E serializable) throws SerializeException {
        if (serializable == null) {
            return EMPTY_BYTE_ARRAY;
        }
        try {
            final RuntimeSchema<E> runtimeSchema = (RuntimeSchema<E>) RuntimeSchema.createFrom(
                serializable.getClass());
            schemaThreadLocal.set(runtimeSchema);
            return ProtostuffIOUtil
                .toByteArray(serializable, runtimeSchema,
                    LinkedBuffer.allocate(DEFAULT_BUFFER_SIZE));
        } catch (Exception e) {
            throw new SerializeException("proto stuff serialized failed,cause:" + e.getMessage(),
                e);
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
        try {
            RuntimeSchema<E> runtimeSchema = (RuntimeSchema<E>) schemaThreadLocal.get();
            E serial = runtimeSchema.newMessage();
            ProtostuffIOUtil.mergeFrom(bytes, serial, runtimeSchema);
            return serial;
        } catch (Exception e) {
            throw new SerializeException("proto stuff deserialize failed,cause:" + e.getMessage(),
                e);
        } finally {
            schemaThreadLocal.remove();
        }
    }

    /**
     * destroy failed
     */
    @Override
    public void destroy() {
        Optional.of(schemaThreadLocal).ifPresent(ThreadLocal::remove);
    }

    /**
     * init failed
     */
    @Override
    public void complete() {
        //not support exception
    }
}
