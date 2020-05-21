package io.github.lzmz.coupon.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ItemPriceDto implements Serializable {
    private String id;
    private Float price;
}
