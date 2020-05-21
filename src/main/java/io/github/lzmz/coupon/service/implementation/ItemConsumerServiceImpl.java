package io.github.lzmz.coupon.service.implementation;

import io.github.lzmz.coupon.dto.response.ItemPriceDto;
import io.github.lzmz.coupon.redis.Item;
import io.github.lzmz.coupon.redis.ItemRepository;
import io.github.lzmz.coupon.service.ItemConsumerService;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ItemConsumerServiceImpl implements ItemConsumerService {

    private final WebClient webClient;
    private final ItemRepository itemRepository;

    public ItemConsumerServiceImpl(WebClient webClient, ItemRepository itemRepository) {
        this.webClient = webClient;
        this.itemRepository = itemRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Float> getItemsPrice(List<String> ids) {
        Map<String, Float> items = new HashMap<>();

        for (String id : ids) {
            Optional<Item> itemCache = itemRepository.findById(id);

            if (itemCache.isPresent()) {
                items.put(id, itemCache.get().getPrice());
            } else {
                float price = getItemPrice(id);
                items.put(id, price);
                itemRepository.save(new Item(id, price));
            }
        }

        return items;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getItemPrice(String id) {
        return this.webClient
                .get()
                .uri(builder -> builder.path(id).build())
                .retrieve()
                .bodyToMono(ItemPriceDto.class)
                .block()
                .getPrice();
    }
}
