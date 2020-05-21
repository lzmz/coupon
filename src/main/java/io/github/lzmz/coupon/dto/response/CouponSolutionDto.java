package io.github.lzmz.coupon.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class CouponSolutionDto implements Serializable {

    @JsonProperty("item_ids")
    @NotEmpty
    private List<String> itemsId;

    @JsonProperty("total")
    @NotNull
    private Float amount;
}
