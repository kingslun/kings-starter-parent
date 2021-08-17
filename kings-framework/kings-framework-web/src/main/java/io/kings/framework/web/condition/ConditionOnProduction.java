package io.kings.framework.web.condition;

import io.kings.framework.core.condition.PropertyCondition;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.Conditional;

/**
 * 在生产环境下加载spring bean
 * env/ENV: pro/prod
 *
 * @author lun.wang
 * @date 2021/6/24 2:08 下午
 * @since v1.0
 */
@Conditional(ConditionOnProduction.ProductionCondition.class)
public @interface ConditionOnProduction {
    class ProductionCondition extends PropertyCondition implements Condition {

        @Override
        protected boolean matches() {
            return super.expectedly("ENV", "pro") || super.expectedly("ENV", "prod") ||
                    super.expectedly("env", "pro") || super.expectedly("env", "prod");
        }

        @Override
        public String onMatch() {
            return "ProductionCondition Success";
        }

        @Override
        protected String onMismatch() {
            return String.format(
                    "ProductionCondition Failure,Maybe because the configuration:%s is not production",
                    "ENV or env");
        }
    }
}