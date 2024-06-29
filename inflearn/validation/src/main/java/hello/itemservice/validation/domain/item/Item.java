package hello.itemservice.validation.domain.item;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.ScriptAssert;

@Data
// @ScriptAssert(lang = "javascript", script = "_this.price * _this.quantity >= 10000", message = "총합이 10000원 이상이어야 합니다.") JDK 17 이상부터 지원하지않음
public class Item {

//    @NotNull(groups = UpdateCheck.class)
    private Long id;
//    @NotBlank(groups = {SaveCheck.class, UpdateCheck.class})
    private String itemName;
//    @NotNull(groups = {SaveCheck.class, UpdateCheck.class})
//    @Range(min = 1000,max = 1000000)
    private Integer price;
//    @NotNull(groups = {SaveCheck.class, UpdateCheck.class})
//    @Max(value = 9999,  groups = SaveCheck.class) //수정 요구사항 추가
    private Integer quantity;

    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
