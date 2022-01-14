package io.kings.framework.log.desensitize;

import java.util.concurrent.TimeUnit;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 分别对日志进行脱敏和不脱敏情况交叉耗时测试
 * <br><font color=yellow>每轮进行多次验证 取其平均值 (以下单位为ms 小数为约等)</font>
 * <br>脱敏次数{@link this#printLogCount}
 * <br>统计次数{@link this#count}
 * <br>为了数据准确性 可以分别改变上述两个值进行多轮测试进行验证
 * <table>
 *     <tr><td>次数</td><td>不脱敏耗时</td><td>关键字脱敏</td><td>损耗倍数</td><td>正则脱敏</td><td>损耗倍数</td></tr>
 *     <tr><td>1000</td><td>8.6</td><td>19.3</td><td>2.24</td><td>42.8</td><td>4.98</td></tr>
 *     <tr><td>10000</td><td>49.6</td><td>107.2</td><td>2.16</td><td>210.6</td><td>4.25</td></tr>
 *     <tr><td>100000</td><td>371.4</td><td>590.8</td><td>1.59</td><td>1150.0</td><td>3.10</td></tr>
 *     <tr><td>1000000</td><td>4965.8</td><td>6550.6</td><td>1.32</td><td>13647.8</td><td>2.75</td></tr>
 *     <tr><td>10000000</td><td>60782.6</td><td>70008.6</td><td>1.15</td><td>135767.4</td><td>2.23</td></tr>
 * </table>
 * <p>
 * <font color=yellow>
 * 综上数据面板粗略得出用以下结论：
 * <br>1、性能损失随着log频繁度逐渐降低
 * <br>2、脱敏过程包含：解析、'*'替换处理 这一定存在性能损耗
 * <br>3、正则匹配相比关键字匹配性能要高两倍之余【原因？优化？】
 * </font>
 * <br>正则损耗点：
 * <br>1、match过程 匹配失败频率越大性能越低 这是正则的痛点
 * <br>2、replace过程 会导致字符串重建 大量的创建过程也是性能损耗点
 * <br>综上 不建议使用正则脱敏！除非不在乎性能【开放正则表达式TODO 目前是代码写死的】
 * <br>由上可知大约在1kw次log时关键字脱敏性能最佳 约等于1.15倍于不脱敏 几乎无损耗 这大概就是字符串缓存池【intern】的功效。
 * <br>后期可以加入正则脱敏，并加入对应的深度脱敏测试(单次log脱敏数量递增而非log数量递增)来更直观的检测其性能差别
 * </p>
 *
 * @author lun.wang
 * @date 2022/01/01 14:00
 * @since v1.2 脱敏性能优化测试
 */
public class DesensitizeTest {

    private final Logger desensitizeLogger = LoggerFactory.getLogger("desensitize-logger");
    private final Logger notDesensitizeLogger = LoggerFactory.getLogger("not-desensitize-logger");
    //统计次数、log次数
    private final int count = 10;
    private final int printLogCount = 10000;

    private long statistics(Logger logger, int count) {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            logger.info("mobile_phone=15021261772,chinese_name=张三丰");
        }
        return System.currentTimeMillis() - startTime;
    }

    @Test
    public void desensitize() throws InterruptedException {
        long take = 0;
        for (int i = 0; i < this.count; i++) {
            take += statistics(this.desensitizeLogger, this.printLogCount);
        }
        System.out.printf("脱敏统计:[%s次连续打印%s个日志平均耗时：%s毫秒]%n", this.count, this.printLogCount,
            (double) take / this.count);
        //异步日志可能还未开始写日志,进程就已经结束、(在验证微量log脱敏时debug自动终止无log,被这个问题坑了很惨)
        TimeUnit.MILLISECONDS.sleep(100);
        Assertions.assertThat(take > 0).isTrue();
    }

    @Test
    public void notDesensitize() throws InterruptedException {
        long take = 0;
        for (int i = 0; i < this.count; i++) {
            take += statistics(this.notDesensitizeLogger, this.printLogCount);
        }
        System.out.printf("不脱敏统计:[%s次连续打印%s个日志平均耗时：%s毫秒]%n", this.count, this.printLogCount,
            (double) take / this.count);
        //异步日志可能还未开始写日志,进程就已经结束、(在验证微量log脱敏时debug自动终止无log,被这个问题坑了很惨)
        TimeUnit.MILLISECONDS.sleep(100);
        Assertions.assertThat(take > 0).isTrue();
    }
}
