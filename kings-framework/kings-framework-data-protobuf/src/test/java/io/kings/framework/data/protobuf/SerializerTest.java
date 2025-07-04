package io.kings.framework.data.protobuf;

import io.kings.framework.data.exception.SerializeException;
import io.kings.framework.data.serializer.Serializer;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * unit compare the jdk kryo and protobuf 分别对以上三种方式进行测试 从序列化后的字节长度和耗时两个层面来对比如下：
 * <p>
 * serialization starting!!!!!======>>> proto_stuff serialized passed,data length:140 proto_stuff
 * deserialize passed, cut: 67/ms serialization end........
 * <p>
 * serialization starting!!!!!======>>> jdk serialized passed,data length:530 jdk deserialize
 * passed, cut: 16/ms serialization end........
 * <p>
 * serialization starting!!!!!======>>> kryo serialized passed,data length:254 kryo deserialize
 * passed, cut: 87/ms serialization end........
 * </p>
 * serialization starting!!!!!======>>> proto_stuff serialized passed,data length:140 proto_stuff
 * deserialize passed, cut: 61/ms serialization end........
 * <p>
 * serialization starting!!!!!======>>> jdk serialized passed,data length:530 jdk deserialize
 * passed, cut: 15/ms serialization end........
 * <p>
 * serialization starting!!!!!======>>> kryo serialized passed,data length:254 kryo deserialize
 * passed, cut: 92/ms serialization end........ 综上所述 单次jdk自带效率最高 但是多次或大量序列化时情况可能就不是这样了 同时proto
 * stuff在空间上的优势也比较明显 因此可根据实际场景选择可是的序列化器 更多参数对比可以参考三方多面测试 * * ========>>> * *
 * https://github.com/eishay/jvm-serializers/wiki * * ========>>>
 *
 * @author lun.kings
 * @date 2020/8/2 10:55 下午
 * @since v3.2.3
 */
@Slf4j
@SpringBootTest(classes = SerializerTest.class)
@RunWith(SpringRunner.class)
@EnableAutoConfiguration
public class SerializerTest {

    /**
     * 构造测试序列对象 通过equals来判断序列化的准确性
     */
    @Setter
    @ToString
    private static class Order implements Serializable {

        private long version = 88888888L;
        private double price = 188.88d;
        private String title = "优惠大酬宾";
        private boolean sold = true;
        private Delivery delivery = new Delivery();

        @Override
        public int hashCode() {
            BigDecimal bigDecimal = BigDecimal.valueOf(this.version)
                    .add(BigDecimal.valueOf(this.price))
                    .add(BigDecimal.valueOf(this.title.hashCode()))
                    .add(BigDecimal.valueOf(this.sold ? 1 : 0))
                    .add(BigDecimal.valueOf(this.delivery.hashCode()));
            return bigDecimal.intValue();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof Order)) {
                return false;
            }
            return this.hashCode() == obj.hashCode();
        }

        @Setter
        @ToString
        static class Delivery implements Serializable {

            private String fromAddress = "上海市虹口区天潼路188弄";
            private String sendersPhone = "11011011011";
            private double deliveryPrice = 25.8d;
            private String toAddress = "上海市普陀区兰溪路188弄";
            private String receiverPhone = "12012012012";

            @Override
            public int hashCode() {
                BigDecimal bigDecimal = BigDecimal.valueOf(this.fromAddress.hashCode())
                        .add(BigDecimal.valueOf(this.sendersPhone.hashCode()))
                        .add(BigDecimal.valueOf(this.deliveryPrice))
                        .add(BigDecimal.valueOf(this.toAddress.hashCode()))
                        .add(BigDecimal.valueOf(this.receiverPhone.hashCode()));
                return bigDecimal.intValue();
            }

            @Override
            public boolean equals(Object obj) {
                if (obj == this) {
                    return true;
                }
                if (!(obj instanceof Order)) {
                    return false;
                }
                return this.hashCode() == obj.hashCode();
            }
        }
    }

    @Autowired
    private Serializer protoStuffSerializer;
    @Autowired
    private Serializer kryoSerializer;
    private final Serializer jdkSerializer = Serializer.DEFAULT_;
    private Serializer serializer;
    /**
     * 序列化对象
     */
    private final Order order = new Order();
    private static final String SERIALIZATION_TYPE = "kings.serialization.serialize-type";

    /**
     * 测试jdk序列化对象
     */
    @Test
    public void jdk() {
        System.setProperty(SERIALIZATION_TYPE, "jdk");
        this.serializer = this.jdkSerializer;
        Assertions.assertThat(this.serializer).isNotNull();
    }

    /**
     * 测试kryo序列化对象
     */
    @Test
    public void kryo() {
        System.setProperty(SERIALIZATION_TYPE, "kryo");
        this.serializer = this.kryoSerializer;
        Assertions.assertThat(this.serializer).isNotNull();
    }

    /**
     * 测试proto stuff序列化对象
     */
    @Test
    public void protoStuff() {
        System.setProperty(SERIALIZATION_TYPE, "proto_stuff");
        this.serializer = this.protoStuffSerializer;
        Assertions.assertThat(this.serializer).isNotNull();
    }

    /**
     * common coding
     *
     * @throws SerializeException serialization exception
     */
    @After
    public void after() throws SerializeException {
        log.debug("serialization starting!!!!!======>>>");
        final String property = System.getProperty(SERIALIZATION_TYPE);
        long start = System.currentTimeMillis();
        byte[] data = serializer.serialize(order);
        log.debug(property + " serialized passed,data length:" + data.length);
        Order that = serializer.deserialize(data);
        log.debug(
                property + " deserialize passed, cut: " + (System.currentTimeMillis() - start) + "/ms");
        assert that.equals(order) : property + " deserialize not equals, a defect may occur ";
        log.debug("==========>>>before:\t" + order);
        log.debug("==========>>>after:\t" + that);
        log.debug("serialization end........\r\n");
    }
}
