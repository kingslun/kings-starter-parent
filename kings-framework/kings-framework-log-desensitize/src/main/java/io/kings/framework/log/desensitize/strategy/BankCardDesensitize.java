package io.kings.framework.log.desensitize.strategy;

import io.kings.framework.log.desensitize.DesensitizeApi;
import io.kings.framework.log.desensitize.DesensitizeContext;
import io.kings.framework.log.desensitize.DesensitizeException;

/**
 * 银行卡号策略
 *
 * @author lun.wang
 * @date 2021/12/21 11:04 AM
 * @since v1.1
 */
class BankCardDesensitize extends AbstractLogDesensitize implements DesensitizeApi {

    /**
     * 银行卡号策略
     * <br>各大银行以及大部分商业银行借记卡都是19位，信用卡都是16位，
     * <br>但有部分不同，如招商银行，华夏银行，中信银行借记卡是16位，兴业银行借记卡18位。
     * <br>综上需要处理16、18、19位 且是否需要兼容美化格式的卡号?【暂不】
     * <pre>
     * 19位卡号:
     * 6217002260009086150 ==> 6217************150
     * 19位卡号:(美化格式)
     * 6217 0022 6000 9086 150 ==> 6217 **** **** **** 150
     * </pre>
     */
    @Override
    public String doDesensitize(SecurityLogAppender ctx) {
        final String src = ctx.currentVal();
        final int len = ctx.currentValLen();
        //first 0-4 bit
        ctx.append(0, 4);
        switch (len) {
            case 16:
                return ctx.appendStar(8).append(13, len).flush().after();
            case 18:
            case 19:
                return ctx.appendStar(12).append(17, len).flush().after();
            default:
                throw new DesensitizeException("illegal bank card" + src);
        }
    }

    /**
     * 卡号16、18、19
     *
     * @param ctx log上下文
     * @return true/false
     */
    @Override
    public boolean valid(DesensitizeContext ctx) {
        int len = ctx.currentValLen();
        return len == 16 || len == 18 || len == 19;
    }
}
