package io.github.lzmz.coupon.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConsumerServiceErrorDto implements Serializable {
    String message;
    String error;
    int status;
    List<String> cause;
}
