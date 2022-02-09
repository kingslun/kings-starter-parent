package io.kings.framework.data.serializer;

import static io.kings.framework.data.serializer.Serializer.SERIALIZE_ENABLE_PREFIX;

import io.kings.framework.core.condition.AbstractPropertyCondition;

/**
 * 序列化装载条件
 *
 * @author lun.wang
 * @date 2021/8/16 4:52 下午
 * @since v2.0
 */
final class OnSerializeCondition extends AbstractPropertyCondition {

    @Override
    public boolean match() {
        return super.on(SERIALIZE_ENABLE_PREFIX);
    }
}
