package io.github.lzmz.coupon.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("Item")
@Getter
@Setter
@AllArgsConstructor
public class Item {
    private String id;
    private Float price;
}
