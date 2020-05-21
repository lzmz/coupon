package io.github.lzmz.coupon.dto.request;

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
    private List<String> itemsId;

    @NotNull
    private Float amount;
}
