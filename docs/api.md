# API reference

## `BitcasterClient`

### Domain and connectivity

| Method | Purpose |
| --- | --- |
| `setDomain(project, application)` | Set the default event domain |
| `getBaseUrl()` | Return the organization-scoped URL without the token |
| `ping()` | Check connectivity with the Bitcaster server |

### Events and resources

| Method | Endpoint |
| --- | --- |
| `triggerEvent(...)` | Trigger an event in the configured domain |
| `listEvents(project, application)` | List application events |
| `listProjects()` | List organization projects |
| `listApplications(project)` | List project applications |
| `listUsers()` | List organization users |
| `listDistributionLists(project)` | List project distribution lists |
| `listMembers(project, distributionList)` | List distribution-list members |

### Users

| Method | Purpose |
| --- | --- |
| `addUser(...)` | Create an organization user |
| `updateUser(...)` | Update a user and custom-field merge mode |
| `registerUser(...)` | Register a user in an application |
| `unregisterUser(...)` | Remove application membership |

## Exceptions

- `BitcasterConfigurationException`: malformed BAE or missing required domain values.
- `BitcasterHttpException`: a non-success HTTP response. Use `getStatusCode()` to inspect the response status.
- `BitcasterException`: transport failures or other SDK errors.

## Async client

`AsyncBitcasterClient` exposes the same operations as `CompletableFuture` values. It delegates work to an executor and implements `AutoCloseable` when it owns its default worker pool.