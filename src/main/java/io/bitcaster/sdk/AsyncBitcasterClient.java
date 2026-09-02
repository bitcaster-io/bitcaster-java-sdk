package io.bitcaster.sdk;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/** Non-blocking facade backed by a worker pool, matching the Python SDK's async client. */
public class AsyncBitcasterClient implements AutoCloseable {
    private final BitcasterClient delegate;
    private final Executor executor;
    private final boolean ownsExecutor;

    public AsyncBitcasterClient(String bae) {
        this(new BitcasterClient(bae), Executors.newFixedThreadPool(4), true);
    }

    public AsyncBitcasterClient(String bae, Executor executor) {
        this(new BitcasterClient(bae), executor, false);
    }

    public AsyncBitcasterClient(BitcasterClient delegate, Executor executor) {
        this(delegate, executor, false);
    }

    private AsyncBitcasterClient(BitcasterClient delegate, Executor executor, boolean ownsExecutor) {
        this.delegate = delegate;
        this.executor = executor;
        this.ownsExecutor = ownsExecutor;
    }

    public void setDomain(String project, String application) {
        delegate.setDomain(project, application);
    }

    public CompletableFuture<Map<String, Object>> ping() {
        return supply(delegate::ping);
    }

    public CompletableFuture<List<Map<String, Object>>> listUsers() {
        return supply(delegate::listUsers);
    }

    public CompletableFuture<List<Map<String, Object>>> listEvents(String project, String application) {
        return supply(() -> delegate.listEvents(project, application));
    }

    public CompletableFuture<List<Map<String, Object>>> listDistributionLists(String project) {
        return supply(() -> delegate.listDistributionLists(project));
    }

    public CompletableFuture<List<Map<String, Object>>> listProjects() {
        return supply(delegate::listProjects);
    }

    public CompletableFuture<List<Map<String, Object>>> listApplications(String project) {
        return supply(() -> delegate.listApplications(project));
    }

    public CompletableFuture<List<Map<String, Object>>> listMembers(String project, String distributionList) {
        return supply(() -> delegate.listMembers(project, distributionList));
    }

    public CompletableFuture<Map<String, Object>> triggerEvent(String event, Map<String, String> context,
                                                               Map<String, Object> options, String cid) {
        return supply(() -> delegate.triggerEvent(event, context, options, cid));
    }

    public CompletableFuture<Map<String, Object>> addUser(String email, String firstName, String lastName,
                                                           Map<String, Object> customFields) {
        return supply(() -> delegate.addUser(email, firstName, lastName, customFields));
    }

    public CompletableFuture<Map<String, Object>> updateUser(String email, String firstName, String lastName,
                                                              Map<String, Object> customFields, String mode) {
        return supply(() -> delegate.updateUser(email, firstName, lastName, customFields, mode));
    }

    public CompletableFuture<Map<String, Object>> registerUser(String project, String application, String username,
                                                               String firstName, String lastName, String email,
                                                               Map<String, Object> customFields, boolean active,
                                                               List<UserAddress> addresses, String distributionList) {
        return supply(() -> delegate.registerUser(project, application, username, firstName, lastName, email,
                customFields, active, addresses, distributionList));
    }

    public CompletableFuture<Map<String, Object>> unregisterUser(String project, String application, String username) {
        return supply(() -> delegate.unregisterUser(project, application, username));
    }

    public BitcasterClient syncClient() {
        return delegate;
    }

    @Override
    public void close() {
        if (ownsExecutor && executor instanceof java.util.concurrent.ExecutorService service) {
            service.shutdown();
        }
    }

    private <T> CompletableFuture<T> supply(java.util.function.Supplier<T> operation) {
        return CompletableFuture.supplyAsync(operation, executor);
    }
}