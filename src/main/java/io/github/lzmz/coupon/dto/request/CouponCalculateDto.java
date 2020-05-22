package io.github.lzmz.coupon.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CouponCalculateDto implements Serializable {

    @JsonProperty("item_ids")
    @NotEmpty
    @Schema(required = true, description = "Item ID.")
    private List<String> itemsId;

    @NotNull
    @Schema(required = true, description = "Total amount to spend.")
    private Float amount;
}
