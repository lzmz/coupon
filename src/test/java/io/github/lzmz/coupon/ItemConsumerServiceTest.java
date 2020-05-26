package io.github.lzmz.coupon;


import io.github.lzmz.coupon.exception.NoItemPriceException;
import io.github.lzmz.coupon.redis.Item;
import io.github.lzmz.coupon.redis.ItemRepository;
import io.github.lzmz.coupon.service.ItemConsumerService;
import io.github.lzmz.coupon.service.implementation.ItemConsumerServiceImpl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemConsumerServiceTest {

    private MockWebServer mockWebServer;
    private ItemConsumerService itemConsumerService;
    private ItemRepository itemRepository;

    @BeforeEach
    public void setUp() {
        mockWebServer = new MockWebServer();
        itemRepository = mock(ItemRepository.class);
        WebClient webClient = WebClient.create(mockWebServer.url("/").toString());
        itemConsumerService = new ItemConsumerServiceImpl(webClient, itemRepository);
    }

    @Test
    public void getItemsPrice_validIdsNotCached_shouldCallExternalServiceAndReturnPrices() throws NoItemPriceException {
        List<String> ids = Arrays.asList("MLA1", "MLA2");
        List<Float> prices = Arrays.asList(100F, 210F);

        for (int i = 0; i < ids.size(); i++) {
            mockWebServer
                    .enqueue(new MockResponse()
                            .setResponseCode(HttpStatus.OK.value())
                            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .setBody("{\"id\": \"" + ids.get(i) + "\", \"price\":" + prices.get(i) + "}")
                    );
        }

        Map<String, Float> items = itemConsumerService.getItemsPrice(ids);
        assertNotNull(items);
        assertEquals(ids.size(), items.size());
        assertTrue(items.keySet().containsAll(ids));
        assertTrue(items.values().containsAll(prices));
        assertEquals(mockWebServer.getRequestCount(), ids.size());
    }

    @Test
    public void getItemsPrice_validIdsCached_shouldReturnPricesFromCache() throws NoItemPriceException {
        List<String> ids = Arrays.asList("MLA1", "MLA2");
        List<Float> prices = Arrays.asList(100F, 210F);

        for (int i = 0; i < ids.size(); i++) {
            Item item = new Item(ids.get(i), prices.get(i));
            when(itemRepository.findById(ids.get(i))).thenReturn(Optional.of(item));

            mockWebServer
                    .enqueue(new MockResponse()
                            .setResponseCode(HttpStatus.OK.value())
                            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .setBody("{\"id\": \"" + item.getId() + "\", \"price\":" + item.getPrice() + "}")
                    );
        }

        Map<String, Float> items = itemConsumerService.getItemsPrice(ids);
        assertNotNull(items);
        assertEquals(ids.size(), items.size());
        assertTrue(items.keySet().containsAll(ids));
        assertTrue(items.values().containsAll(prices));
        assertEquals(mockWebServer.getRequestCount(), 0);
    }

    @Test
    public void getItemsPrice_validIdNoItem_shouldThrowNoItemPriceException() {
        List<String> ids = Collections.singletonList("MLA1");

        mockWebServer
                .enqueue(new MockResponse()
                        .setResponseCode(HttpStatus.OK.value())
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                );

        assertThrows(NoItemPriceException.class, () -> itemConsumerService.getItemsPrice(ids));
        assertEquals(mockWebServer.getRequestCount(), ids.size());
    }

    @Test
    public void getItemsPrice_validIdsNoPrice_shouldThrowNoItemPriceException() {
        List<String> ids = Arrays.asList("MLA1", "MLA2");
        List<Float> prices = Collections.singletonList(100F);

        mockWebServer
                .enqueue(new MockResponse()
                        .setResponseCode(HttpStatus.OK.value())
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setBody("{\"id\": \"" + ids.get(0) + "\", \"price\":" + prices.get(0) + "}")
                );

        mockWebServer
                .enqueue(new MockResponse()
                        .setResponseCode(HttpStatus.OK.value())
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setBody("{\"id\": \"" + ids.get(1) + "\"}")
                );

        assertThrows(NoItemPriceException.class, () -> itemConsumerService.getItemsPrice(ids));
        assertEquals(mockWebServer.getRequestCount(), ids.size());
    }
}