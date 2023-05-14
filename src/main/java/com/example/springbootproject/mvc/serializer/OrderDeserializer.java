package com.example.springbootproject.mvc.serializer;

import com.example.springbootproject.domain.*;
import com.example.springbootproject.exception.MyException;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.EnumMap;

@Slf4j
public class OrderDeserializer extends StdDeserializer<Order> {

    private ObjectMapper objectMapper;
    private EnumMap<OrderType, Class> orderTypeMap = new EnumMap<>(OrderType.class);
    public OrderDeserializer(ObjectMapper mapper) {
        this(mapper, null);
    }

    public OrderDeserializer(ObjectMapper mapper, Class<?> vc) {
        super(vc);

        objectMapper = mapper;
        orderTypeMap.put(OrderType.FBI, FbiOrder.class);
        orderTypeMap.put(OrderType.CA, CaOrder.class);
        orderTypeMap.put(OrderType.P2C, P2cOrder.class);
    }

    @Override
    public Order deserialize(JsonParser jp, DeserializationContext context)
            throws IOException, JsonProcessingException {

        log.info("################ Hello there, I'm here in AbstractOrderDeserializer...");

//        JsonNode node = jp.getCodec().readTree(jp);
        String json = jp.readValueAsTree().toString();
        log.info("################ jsonAsText: {}", json);

//        int id = (Integer) ((IntNode) node.get("id")).numberValue();
//        String itemName = node.get("itemName").asText();
//        int userId = (Integer) ((IntNode) node.get("createdBy")).numberValue();

        JsonFactory factory = new JsonFactory();
        JsonParser parser  = factory.createParser(json);
        log.info("################ parser: {}", parser);

//        JsonNode node = parser.getCodec().readTree(parser);
        JsonNode node=objectMapper.readValue(parser, JsonNode.class);

        String orderTypeText = node.get("orderType").asText();
        log.info("################ type: {}", orderTypeText);

        OrderType orderType = OrderType.fromString(orderTypeText);
        log.info("################ orderType: {}", orderType);

        if(orderType == null){
            throw new MyException("Invalid Order type: " + orderTypeText);
        }

        Class clazz = orderTypeMap.get(orderType);
        log.info("################ orderType clazz: {}", clazz);


        return (Order) objectMapper.readValue(json, clazz);
    }
}