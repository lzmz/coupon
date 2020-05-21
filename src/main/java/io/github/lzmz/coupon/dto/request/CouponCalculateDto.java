package io.github.lzmz.coupon.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class CouponCalculateDto implements Serializable {

    @NotEmpty
    @Schema(required = true, description = "Item ID.")
    private List<String> itemsId;

    @NotNull
    @Schema(required = true, description = "Total amount to spend.")
    private Float amount;
}
