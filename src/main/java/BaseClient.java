import org.glassfish.jersey.internal.util.Base64;
import response.TokenResponse;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class BaseClient {

	private static final String BASE_API_URL = "https://api-exchange.bankera.com";
	private static final String AUTH_ENDPOINT = "/oauth/token";
	private static final String AUTHORIZATION_HEADER_PATTERN = "Basic %s";
	private static final String ENCODABLE_CREDENTIALS_PATTERN = "%s:%s";
	private static final String GRANT_TYPE_CONST = "grant_type";
	private static final String AUTHORIZATION_CONST = "Authorization";
	private static final String CC_GRANT_TYPE = "client_credentials";

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


}
