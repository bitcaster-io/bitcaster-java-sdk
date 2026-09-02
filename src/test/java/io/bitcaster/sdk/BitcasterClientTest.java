package io.bitcaster.sdk;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.POST;

class BitcasterClientTest {
    private static final String BAE = "https://token-123@bitcaster.example.com/api/o/demo-org/";
    private RestClient.Builder builder;
    private MockRestServiceServer server;
    private BitcasterClient client;

    @BeforeEach
    void setUp() {
        builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        client = new BitcasterClient(BAE, builder);
    }

    @Test
    void parsesEndpointWithoutExposingTokenInBaseUrl() {
        BitcasterClient client = new BitcasterClient(
                "https://token-123@bitcaster.example.com/api/o/demo-org");

        assertEquals("https://bitcaster.example.com/api/o/demo-org/", client.getBaseUrl());
    }

    @Test
    void rejectsMissingDomainWhenTriggering() {
        BitcasterClient client = new BitcasterClient(
                "https://token-123@bitcaster.example.com/api/o/demo-org/");

        assertThrows(BitcasterConfigurationException.class,
                () -> client.triggerEvent("signup", Map.of(), null, null));
    }

    @Test
    void rejectsMalformedEndpoint() {
        assertThrows(BitcasterConfigurationException.class,
                () -> new BitcasterClient("https://example.com/api/v1/"));
    }

        @Test
        void pingUsesServerApiAndAuthenticationHeader() {
        server.expect(requestTo("https://bitcaster.example.com/api/system/ping/"))
            .andExpect(method(GET))
            .andExpect(header(HttpHeaders.AUTHORIZATION, "Key token-123"))
            .andExpect(header(HttpHeaders.USER_AGENT, "Bitcaster-Java-SDK"))
            .andRespond(withSuccess("{\"slug\":\"demo-org\"}", MediaType.APPLICATION_JSON));

        assertEquals("demo-org", client.ping().get("slug"));
        server.verify();
        }

        @Test
        void listsAllResourcesWithEncodedPathSegments() {
        server.expect(requestTo("https://bitcaster.example.com/api/o/demo-org/p/project%20one/a/app%2Fone/e/"))
            .andExpect(method(GET)).andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));
        server.expect(requestTo("https://bitcaster.example.com/api/o/demo-org/u/"))
            .andExpect(method(GET)).andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));
        server.expect(requestTo("https://bitcaster.example.com/api/o/demo-org/p/project%20one/d/"))
            .andExpect(method(GET)).andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));
        server.expect(requestTo("https://bitcaster.example.com/api/o/demo-org/p/"))
            .andExpect(method(GET)).andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));
        server.expect(requestTo("https://bitcaster.example.com/api/o/demo-org/p/project%20one/a/"))
            .andExpect(method(GET)).andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));
        server.expect(requestTo("https://bitcaster.example.com/api/o/demo-org/p/project%20one/d/list%2Fone/m/"))
            .andExpect(method(GET)).andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        client.listEvents("project one", "app/one");
        client.listUsers();
        client.listDistributionLists("project one");
        client.listProjects();
        client.listApplications("project one");
        client.listMembers("project one", "list/one");
        server.verify();
        }

        @Test
        void triggersEventWithDefaultsAndEncodedCorrelationId() {
        client.setDomain("project", "application");
        server.expect(requestTo("https://bitcaster.example.com/api/o/demo-org/p/project/a/application/e/order%20created/trigger/?cid=correlation%20id"))
            .andExpect(method(POST))
            .andExpect(jsonPath("$.context").isMap())
            .andExpect(jsonPath("$.options").isMap())
            .andRespond(withSuccess("{\"accepted\":true}", MediaType.APPLICATION_JSON));

        assertEquals(true, client.triggerEvent("order created", null, null, "correlation id").get("accepted"));
        server.verify();
        }

        @Test
        void writesUserPayloadsAndDefaults() {
        server.expect(requestTo("https://bitcaster.example.com/api/o/demo-org/u/"))
            .andExpect(method(POST))
            .andExpect(jsonPath("$.email", equalTo("user@example.com")))
            .andExpect(jsonPath("$.first_name", equalTo("")))
            .andExpect(jsonPath("$.last_name", equalTo("")))
            .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));
        server.expect(requestTo("https://bitcaster.example.com/api/o/demo-org/u/user%40example.com/"))
            .andExpect(method(PATCH))
            .andExpect(jsonPath("$._mode", equalTo("merge")))
            .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));
        server.expect(requestTo("https://bitcaster.example.com/api/o/demo-org/p/project/a/application/register/"))
            .andExpect(method(POST))
            .andExpect(jsonPath("$.custom_fields.groups[0]", equalTo(7)))
            .andExpect(jsonPath("$.addresses[0].value", equalTo("+1555")))
            .andExpect(jsonPath("$.addresses[0].assign_to_preferred_channel", equalTo(true)))
            .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));
        server.expect(requestTo("https://bitcaster.example.com/api/o/demo-org/p/project/a/application/unregister/user%20one/"))
            .andExpect(method(POST))
            .andRespond(withSuccess("{\"deleted\":1}", MediaType.APPLICATION_JSON));

        client.addUser("user@example.com", null, null, null);
        client.updateUser("user@example.com", null, null, null, "merge");
        client.registerUser("project", "application", "user one", null, null, null,
            Map.of("groups", List.of(7)), false, List.of(new UserAddress("+1555", true)), "alerts");
        assertEquals(1, client.unregisterUser("project", "application", "user one").get("deleted"));
        server.verify();
        }

        @Test
        void mapsBadRequestsToTypedException() {
        server.expect(requestTo("https://bitcaster.example.com/api/o/demo-org/u/"))
            .andExpect(method(POST))
            .andRespond(withBadRequest().body("invalid payload"));

        BitcasterHttpException exception = assertThrows(BitcasterHttpException.class,
            () -> client.addUser("user@example.com", "", "", null));
        assertEquals(400, exception.getStatusCode());
        server.verify();
        }

        @Test
        void asyncClientDelegatesOnProvidedExecutor() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            server.expect(requestTo("https://bitcaster.example.com/api/o/demo-org/u/"))
                .andExpect(method(GET))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));
            AsyncBitcasterClient async = new AsyncBitcasterClient(client, executor);

            assertEquals(0, async.listUsers().join().size());
                async.close();
            server.verify();
        } finally {
            executor.shutdownNow();
        }
        }
}