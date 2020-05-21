package io.github.lzmz.coupon.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "Item ID.")
    private List<String> itemsId;

    @JsonProperty("total")
    @NotNull
    @Schema(description = "Total amount associated to the items of the solution.")
    private Float amount;
}
