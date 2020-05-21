package io.github.lzmz.coupon.dto.response;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
public class ItemPriceDto implements Serializable {

    @NotEmpty
    private String id;

    @NotNull
    private Float price;
}
