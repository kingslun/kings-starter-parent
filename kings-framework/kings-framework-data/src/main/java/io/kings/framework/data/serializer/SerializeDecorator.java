package io.kings.framework.data.serializer;

import io.kings.framework.data.exception.SerializeDecorateException;
import java.io.Serializable;

/**
 * 序列器装饰者
 *
 * @author lun.wang
 * @date 2021/8/16 10:43 上午
 * @since v2.0
 */
public interface SerializeDecorator<I extends Serializable, O> {

    /**
     * decode the date before serialize
     *
     * @param in data
     * @return out data
     * @throws SerializeDecorateException decode failed
     */
    O decode(I in) throws SerializeDecorateException;

    /**
     * encode the bytes to data
     *
     * @param bytes data bytes
     * @param clazz in type
     * @return in data
     * @throws SerializeDecorateException encode failed
     */
    I encode(byte[] bytes, Class<I> clazz) throws SerializeDecorateException;
}
