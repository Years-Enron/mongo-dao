package mongo.dao.condition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Field {

    /**
     * 属性名
     */
    private String name;

    /**
     * 操作器
     */
    private Operator operator;

    /**
     * 值
     */
    private Object value;

    public static enum Operator {
        EQ, IN;
    }

    public static Field eq(String name, Object value) {
        return Field.builder().name(name).operator(Operator.EQ).value(value).build();
    }

    public static Field in(String name, Object value) {
        return Field.builder().name(name).operator(Operator.IN).value(value).build();
    }

}
