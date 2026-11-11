//package org.Foodcards;
//
//import io.quarkus.test.InjectMock;
//import io.quarkus.test.Mock;
//import io.quarkus.test.junit.QuarkusTest;
//import jakarta.inject.Inject;
//import net.datafaker.Faker;
//import org.Foodcards.dao.AppChangerDao;
//import org.Foodcards.dto.internal.IncomingMessageCommand;
//import org.Foodcards.dto.kafka.GruIncomingDTO;
//import org.Foodcards.factory.dto.TransDtoFactory;
//import org.Foodcards.repository.ConfigRepository;
//import org.Foodcards.service.in.IngestionService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.ArgumentCaptor;
//import org.mockito.Mockito;
//
//import java.util.Collections;
//
//import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//@QuarkusTest
//public class IngestionServiceTest {
//    @Inject
//    IngestionService ingestionService;
//
//    @InjectMock
//    AppChangerDao dao;
//
//    @InjectMock
//    ConfigRepository configRepository;
//
//    private Faker faker;
//
//    @BeforeEach
//    void setUp() {
//        faker = new Faker();
//
//        Mockito.when(configRepository.findActiveConfigs()).thenReturn(Collections.emptyList());
//    }
//
//    @Test
//    public void testDao() {
//        String fakeSystem = faker.company().name();
//        String fakeRequestId = faker.number().digits(10);
//
//        String json = "{\"systemId\":\"" + fakeSystem + "\", \"eventType\":\"BALANCE\", \"requestId\":\"" + fakeRequestId + "\"}";
//
//        GruIncomingDTO dto = new GruIncomingDTO();
//        dto.setSystemId(fakeSystem);
//        dto.setEventType("BALANCE");
//        dto.setRequestId(faker.number().digits(10));
//
//        ArgumentCaptor<IncomingMessageCommand> captor = ArgumentCaptor.forClass(IncomingMessageCommand.class);
//        Mockito.doNothing().when(dao).saveIncomning(Mockito.any(IncomingMessageCommand.class));
//
//        assertDoesNotThrow(() -> {
//            ingestionService.process(dto, json);
//        });
//
//        Mockito.verify(dao, Mockito.times(1)).saveIncomning(Mockito.any());
//
//        IncomingMessageCommand savedCommand = captor.getValue();
//
//        assertEquals(fakeRequestId, savedCommand.getPayload().getRequestId());
//        assertEquals(fakeSystem, savedCommand.getPayload().getRequestId());
//        assertEquals("BALANCE", savedCommand.getPayload().getEventType());
//    }
//}
