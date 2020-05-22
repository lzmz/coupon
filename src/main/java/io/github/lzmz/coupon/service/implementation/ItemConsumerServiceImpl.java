package io.github.lzmz.coupon.service.implementation;

import io.github.lzmz.coupon.dto.response.ItemPriceDto;
import io.github.lzmz.coupon.exceptions.NoItemPriceException;
import io.github.lzmz.coupon.redis.Item;
import io.github.lzmz.coupon.redis.ItemRepository;
import io.github.lzmz.coupon.service.ItemConsumerService;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
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
    public Map<String, Float> getItemsPrice(List<String> ids) throws NoItemPriceException {
        Map<String, Float> items = new HashMap<>();
        List<String> errors = new ArrayList<>();

        for (String id : ids) {
            try {
                Float price = getItemPrice(id);
                items.put(id, price);
            } catch (NoItemPriceException ex) {
                errors.add(id);
            }
        }

        if (!errors.isEmpty()) {
            throw new NoItemPriceException(errors);
        }

        return items;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Float getItemPrice(String id) throws NoItemPriceException {
        Optional<Item> itemCache = itemRepository.findById(id);

        if (itemCache.isPresent()) {
            return itemCache.get().getPrice();
        }

        ItemPriceDto itemPriceDto = this.webClient
                .get()
                .uri(builder -> builder.path(id).build())
                .retrieve()
                .bodyToMono(ItemPriceDto.class)
                .block();

        if (itemPriceDto == null || itemPriceDto.getPrice() == null) {
            throw new NoItemPriceException(id);
        }

        itemRepository.save(new Item(id, itemPriceDto.getPrice()));
        return itemPriceDto.getPrice();
    }
}
