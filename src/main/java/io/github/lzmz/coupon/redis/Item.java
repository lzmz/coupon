package io.github.lzmz.coupon.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("Item")
@Getter
@AllArgsConstructor
public class Item {
    private final String id;
    private final Float price;
}
