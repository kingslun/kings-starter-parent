package io.kings.framework.web.condition;

import io.kings.framework.core.condition.AbstractPropertyCondition;
import io.kings.framework.core.condition.PropertyCondition;
import org.springframework.context.annotation.Conditional;

/**
 * 在生产环境下加载spring bean env/ENV: pro/prod
 *
 * @author lun.wang
 * @date 2021/6/24 2:08 下午
 * @since v1.0
 */
@Conditional(ConditionOnProduction.ProductionCondition.class)
public @interface ConditionOnProduction {

    class ProductionCondition extends AbstractPropertyCondition implements PropertyCondition {

        @Override
        public boolean match() {
            return super.expectation("ENV", "pro") || super.expectation("ENV", "prod") ||
                    super.expectation("env", "pro") || super.expectation("env", "prod");
        }

        @Override
        public String onMatched() {
            return "ProductionCondition Success";
        }

        @Override
        public String onMismatch() {
            return String.format(
                    "ProductionCondition Failure,Maybe because the configuration:%s is not production",
                    "ENV or env");
        }
    }
}