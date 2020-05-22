package io.github.lzmz.coupon.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Getter
@AllArgsConstructor
public class CouponSolutionDto implements Serializable {

    @JsonProperty("item_ids")
    @NotEmpty
    @Schema(description = "Item ID.")
    private final List<String> itemsId;

    @NotNull
    @Schema(description = "Total amount associated to the items of the solution.")
    private final Float total;
}
