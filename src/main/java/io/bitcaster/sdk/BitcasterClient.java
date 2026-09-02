package io.bitcaster.sdk;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriUtils;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Synchronous client for the Bitcaster organization REST API. */
public class BitcasterClient {
    private static final ParameterizedTypeReference<Map<String, Object>> JSON = new ParameterizedTypeReference<>() {};
    private static final ParameterizedTypeReference<List<Map<String, Object>>> JSON_LIST = new ParameterizedTypeReference<>() {};

    private final RestClient restClient;
    private final String baseUrl;
    private volatile String project;
    private volatile String application;

    public BitcasterClient(String bae) {
        this(bae, RestClient.builder());
    }

    public BitcasterClient(String bae, RestClient.Builder builder) {
        Endpoint endpoint = Endpoint.parse(bae);
        this.baseUrl = endpoint.baseUrl();
        this.restClient = builder.baseUrl(baseUrl)
                .defaultHeader("Authorization", "Key " + endpoint.token())
                .defaultHeader("User-Agent", "Bitcaster-Java-SDK")
                .build();
    }

    public void setDomain(String project, String application) {
        this.project = requireText(project, "project");
        this.application = requireText(application, "application");
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public Map<String, Object> ping() {
        return getAbsolute("/api/system/ping/", JSON);
    }

    public List<Map<String, Object>> listEvents(String project, String application) {
        return get("p/%s/a/%s/e/".formatted(path(project), path(application)), JSON_LIST);
    }

    public List<Map<String, Object>> listUsers() {
        return get("u/", JSON_LIST);
    }

    public List<Map<String, Object>> listDistributionLists(String project) {
        return get("p/%s/d/".formatted(path(project)), JSON_LIST);
    }

    public List<Map<String, Object>> listProjects() {
        return get("p/", JSON_LIST);
    }

    public List<Map<String, Object>> listApplications(String project) {
        return get("p/%s/a/".formatted(path(project)), JSON_LIST);
    }

    public List<Map<String, Object>> listMembers(String project, String distributionList) {
        return get("p/%s/d/%s/m/".formatted(path(project), path(distributionList)), JSON_LIST);
    }

    public Map<String, Object> triggerEvent(String event, Map<String, String> context,
                                             Map<String, Object> options, String cid) {
        String project = requireText(this.project, "project");
        String application = requireText(this.application, "application");
        String path = "p/%s/a/%s/e/%s/trigger/".formatted(path(project), path(application), path(event));
        if (cid != null && !cid.isBlank()) {
            path += "?cid=" + UriUtils.encodeQueryParam(cid, StandardCharsets.UTF_8);
        }
        return post(path, Map.of("context", context == null ? Map.of() : context,
                "options", options == null ? Map.of() : options), JSON);
    }

    public Map<String, Object> addUser(String email, String firstName, String lastName,
                                        Map<String, Object> customFields) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("email", email);
        body.put("first_name", empty(firstName));
        body.put("last_name", empty(lastName));
        body.put("custom_fields", customFields);
        return post("u/", body, JSON);
    }

    public Map<String, Object> updateUser(String email, String firstName, String lastName,
                                           Map<String, Object> customFields, String mode) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("first_name", empty(firstName));
        body.put("last_name", empty(lastName));
        body.put("custom_fields", customFields);
        body.put("_mode", mode == null ? "ignore" : mode);
        return patch("u/%s/".formatted(path(email)), body, JSON);
    }

    public Map<String, Object> registerUser(String project, String application, String username,
                                             String firstName, String lastName, String email,
                                             Map<String, Object> customFields, boolean active,
                                             List<UserAddress> addresses, String distributionList) {
        List<Map<String, Object>> serializedAddresses = new ArrayList<>();
        for (UserAddress address : addresses == null ? List.<UserAddress>of() : addresses) {
            serializedAddresses.add(Map.of("value", address.value(),
                    "assign_to_preferred_channel", address.assignToPreferredChannel()));
        }
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("username", username);
        body.put("first_name", empty(firstName));
        body.put("last_name", empty(lastName));
        body.put("email", empty(email));
        body.put("custom_fields", customFields == null ? Map.of() : customFields);
        body.put("active", active);
        body.put("addresses", serializedAddresses);
        body.put("distribution_list", distributionList);
        return post("p/%s/a/%s/register/".formatted(path(project), path(application)), body, JSON);
    }

    public Map<String, Object> unregisterUser(String project, String application, String username) {
        return post("p/%s/a/%s/unregister/%s/".formatted(path(project), path(application), path(username)), Map.of(), JSON);
    }

    private <T> T get(String path, ParameterizedTypeReference<T> type) {
        return request(() -> restClient.get().uri(URI.create(baseUrl + path)).retrieve().body(type));
    }

    private <T> T getAbsolute(String uri, ParameterizedTypeReference<T> type) {
        return request(() -> restClient.get().uri(URI.create(absolute(uri))).retrieve().body(type));
    }

    private <T> T post(String path, Object body, ParameterizedTypeReference<T> type) {
        return request(() -> restClient.post().uri(URI.create(baseUrl + path)).body(body).retrieve().body(type));
    }

    private <T> T patch(String path, Object body, ParameterizedTypeReference<T> type) {
        return request(() -> restClient.patch().uri(URI.create(baseUrl + path)).body(body).retrieve().body(type));
    }

    private <T> T request(java.util.function.Supplier<T> action) {
        try {
            return Objects.requireNonNull(action.get(), "Bitcaster returned an empty response");
        } catch (org.springframework.web.client.RestClientResponseException exception) {
            throw new BitcasterHttpException(exception.getStatusCode().value(), exception.getResponseBodyAsString());
        } catch (org.springframework.web.client.RestClientException exception) {
            throw new BitcasterException("Unable to reach Bitcaster at " + baseUrl, exception);
        }
    }

    private String absolute(String path) {
        return baseUrl.replaceFirst("/api/o/[^/]+/$", "") + path;
    }

    private static String path(String value) {
        byte[] bytes = requireText(value, "value").getBytes(StandardCharsets.UTF_8);
        StringBuilder encoded = new StringBuilder(bytes.length);
        for (byte current : bytes) {
            int unsigned = current & 0xff;
            if ((unsigned >= 'a' && unsigned <= 'z') || (unsigned >= 'A' && unsigned <= 'Z')
                    || (unsigned >= '0' && unsigned <= '9') || unsigned == '-' || unsigned == '.'
                    || unsigned == '_' || unsigned == '~') {
                encoded.append((char) unsigned);
            } else {
                encoded.append("%%%02X".formatted(unsigned));
            }
        }
        return encoded.toString();
    }

    private static String empty(String value) {
        return value == null ? "" : value;
    }

    private static String requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new BitcasterConfigurationException(name + " must not be blank");
        }
        return value;
    }

    private record Endpoint(String scheme, String token, String host, String organization) {
        static Endpoint parse(String bae) {
            if (bae == null || bae.isBlank()) {
                throw new BitcasterConfigurationException("Set BITCASTER_BAE or provide a BAE endpoint");
            }
            URI uri;
            try {
                uri = URI.create(bae.endsWith("/") ? bae : bae + "/");
            } catch (IllegalArgumentException exception) {
                throw new BitcasterConfigurationException("Invalid Bitcaster BAE: " + bae);
            }
            String userInfo = uri.getUserInfo();
            String path = uri.getPath();
            if (!("http".equals(uri.getScheme()) || "https".equals(uri.getScheme()))
                    || userInfo == null || !path.matches("/api/o/[^/]+/")) {
                throw new BitcasterConfigurationException("BAE must match https://<token>@<host>/api/o/<organization>/");
            }
            return new Endpoint(uri.getScheme(), userInfo, uri.getRawAuthority().substring(userInfo.length() + 1),
                    path.substring("/api/o/".length(), path.length() - 1));
        }

        String baseUrl() {
            return scheme + "://" + host + "/api/o/" + organization + "/";
        }
    }
}