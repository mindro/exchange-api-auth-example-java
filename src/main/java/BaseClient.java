import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.sun.xml.internal.ws.util.StringUtils;
import org.glassfish.jersey.internal.util.Base64;
import response.TokenResponse;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.Serializable;
import java.util.concurrent.Future;

public class BaseClient {

	private static final String BASE_API_URL = "https://api-exchange.bankera.com";
	private static final String AUTH_ENDPOINT = "/oauth/token";
	private static final String AUTHORIZATION_HEADER_PATTERN = "Basic %s";
	private static final String ENCODABLE_CREDENTIALS_PATTERN = "%s:%s";
	private static final String GRANT_TYPE_CONST = "grant_type";
	private static final String AUTHORIZATION_CONST = "Authorization";
	private static final String CC_GRANT_TYPE = "client_credentials";
	private static final String CHARSET_NAME = "UTF-8";

	//Client info
	private String clientId;
	private String clientSecret;

	//Token
	private String tokenType;
	private String accessToken;


	public BaseClient(String clientId, String clientSecret) {
		this.clientId = clientId;
		this.clientSecret = clientSecret;
	}

	public void authenticate() {
		Client client = ClientBuilder.newClient();
		String credentials = String.format(ENCODABLE_CREDENTIALS_PATTERN, clientId, clientSecret);
		String encodedCredentials = new String(Base64.encode(credentials.getBytes()));
		String authorizationHeader = String.format(AUTHORIZATION_HEADER_PATTERN, encodedCredentials);

		Response response = client
			.target(BASE_API_URL.concat(AUTH_ENDPOINT))
			.queryParam(GRANT_TYPE_CONST, CC_GRANT_TYPE)
			.request(MediaType.APPLICATION_JSON_TYPE)
			.header(AUTHORIZATION_CONST, authorizationHeader)
			.get();

		if (response.getStatus() == 200) {
			TokenResponse tokenResponse = response.readEntity(TokenResponse.class);
			accessToken = tokenResponse.getAccessToken();
			tokenType = tokenResponse.getTokenType();
		}
	}

	protected <Req, Resp> Resp process(Req request, String url, String httpMethod, Class<Resp> respClass) throws Exception {
		Client client = getClient();

		if (accessToken == null || tokenType == null) {
			authenticate();
		}

		WebTarget target = client.target(BASE_API_URL.concat(url));
		Invocation.Builder builder = target.request()
				.header(HttpHeaders.AUTHORIZATION, getToken())
				.acceptEncoding(CHARSET_NAME)
				.accept(MediaType.APPLICATION_JSON);

		Future<Response> responseFuture;
		switch (httpMethod) {
			case HttpMethod.GET:
				responseFuture = builder.buildGet().submit();
				break;
			case HttpMethod.POST:
				responseFuture = builder.buildPost(Entity.entity(request, MediaType.APPLICATION_JSON)).submit();
				break;
			default:
				throw new UnsupportedOperationException();
		}

		Response response = responseFuture.get();

		if (response.getStatus() == Response.Status.OK.getStatusCode()) {
			return response.readEntity(respClass);
		} else if (response.getStatus() == Response.Status.UNAUTHORIZED.getStatusCode()) {
			authenticate();
			process(request, url, httpMethod, respClass);
		}

		throw new Exception();
	}

	private Client getClient() {
		Client client = ClientBuilder.newClient();
		client.register(new JacksonJaxbJsonProvider().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false));
		return client;
	}

	private String getToken() {
		return StringUtils.capitalize(tokenType).concat(" ").concat(accessToken);
	}
}
