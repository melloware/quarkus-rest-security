# Quarkus REST Security Demo

This project demonstrates secure REST API implementation using Quarkus and multiple security mechanisms including Basic Auth and OIDC. It showcases best practices for implementing secure endpoints with authentication and authorization.

## Features

- Basic Auth based authentication
- JWT token-based authentication
- Role-based access control

## Project Goals

- Demonstrate secure REST API implementation using Quarkus
- Showcase OIDC and Basic Auth integration with Quarkus applications
- Provide examples of securing endpoints with different authentication methods
- Illustrate best practices for API security in Quarkus applications

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

**Basic Auth:**
```shell script
./mvnw quarkus:dev -Dquarkus.profile=basic
```

**OIDC:**
```shell script
./mvnw quarkus:dev -Dquarkus.profile=oidc
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## REST Endpoints

The application exposes the following REST endpoints with different authentication requirements:

| Endpoint | URL | Description | Required Role |
|----------|-----|-------------|---------------|
| Public Resource | `/api/public` | Returns a public message accessible to all users. Demonstrates unrestricted endpoint access. | No authentication required |
| Authenticated Resource | `/api/authenticated` | Returns a message only for authenticated users. Shows basic authentication verification. | Any authenticated user regardless of role |
| Secure Resource | `/api/secure` | Protected endpoint that returns sensitive data. Demonstrates role-based access control. | `API_ADMIN` or `API_USER` |
| Admin Resource | `/api/admin` | Administrative endpoint for system management. Shows strict role-based authorization. | `API_ADMIN` only |
| Token Resource | `/api/playground` | Returns the current user's authentication token details. Useful for token inspection and validation. | Any authenticated user regardless of role |


## Users

The application comes with two pre-configured users:

| Username | Password | Roles |
|----------|----------|-------|
| alice | alice | ADMIN, USER |
| bob | bob |  USER |
| chris | chris | REPORTER, CONSUMER |


- **alice** is an administrator with both `API_ADMIN` and `API_USER` roles, who has full access to all endpoints including the admin resources
- **bob** is a regular user with `API_USER` role, who can access both public and secure endpoints
- **chris** is a user with `REPORTER` and `CONSUMER` roles, who can ONLY access public and authenticated endpoints


# OIDC

For OIDC make sure to use the Dev UI and examine and login to KeyCloak for OIDC.
See this ([KeyCloak Dev Services Guide](https://quarkus.io/guides/security-openid-connect-dev-services)) for how to login and test 
your OIDC bearer tokens using `alice` and `bob` accounts.

## Configuring KeyCloak for React OIDC Client

To enable the React client to work with OIDC authentication, you'll need to configure the KeyCloak realm settings. This setup implements the [Authorization Code Grant with PKCE flow](https://github.com/authts/oidc-client-ts/blob/main/docs/protocols/authorization-code-grant-with-pkce.md) for secure authentication.

## KeyCloak Configuration Steps

1. Log into the KeyCloak Admin Console:
   - URL: http://localhost:{KEYCLOAK_PORT}/admin
   - Credentials: `admin/admin`

2. Navigate to the `quarkus` realm and select the `backend-service` client

3. Configure the following settings:

### Access Settings
- Set **Valid redirect URIs** to `http://localhost:5173/*` 
- Set **Valid post logout redirect URIs** to `http://localhost:5173/*`
- Set **Web origins** to `*` to allow CORS

### Authentication Flow
- Enable **Standard flow** (Authorization Code Flow)
- Enable **Direct access grants** (Resource Owner Password Flow)

### Logout Settings  
- Enable **Front channel logout** for browser-based logout
- Enable **Backchannel logout session required** for server-side session cleanup

### Security Settings
- Disable **Client authentication** under Capability config

## React Client

Once this is saved make sure to edit the `/react-client/.env.local` with the correct settings especially `KEYCLOAK_PORT`.

```bash
VITE_AUTHORITY=http://localhost:{KEYCLOAK_PORT}/realms/quarkus
VITE_CLIENT_ID=backend-service
VITE_CLIENT_SECRET=secret
VITE_API_BASE_URL=http://localhost:8080
```

Now you can run the React application...

```bash
cd react-client
npm install
npm run dev
```

Navigate to `http://localhost:5173/` and you should be redirected to login with `bob/bob` or `alice/alice` and see the login/logout working.


## Related Guides

- Basic Authentication ([guide](https://quarkus.io/guides/security-authentication-mechanisms#basic-auth)) : Secure your applications with Basic Auth
- OIDC Bearer Token Authentication ([guide](https://quarkus.io/guides/security-oidc-bearer-token-authentication-tutorial)): Secure your applications with OIDC bearer tokens
- REST Jackson ([guide](https://quarkus.io/guides/rest#json-serialisation)): Jackson serialization support for Quarkus REST. This extension is not compatible with the quarkus-resteasy extension, or any of the extensions that depend on it


