package io.github.lzmz.coupon.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class ConsumerServiceErrorDto implements Serializable {
    String message;
}
