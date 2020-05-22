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
        ids.forEach(id -> items.put(id, getItemPrice(id)));
        return items;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Float getItemPrice(String id) {
        Optional<Item> itemCache = itemRepository.findById(id);

        if (itemCache.isPresent()) {
            return itemCache.get().getPrice();
        }

        Float price = this.webClient
                .get()
                .uri(builder -> builder.path(id).build())
                .retrieve()
                .bodyToMono(ItemPriceDto.class)
                .block()
                .getPrice();

        itemRepository.save(new Item(id, price));
        return price;
    }
}
