import response.UsersInfoResponse;

import javax.ws.rs.HttpMethod;

public class ExchangeClient extends BaseClient {

	private static final String USERS_INFO_URL = "/users/info";


	/**
	 * @param clientId - API client id
	 * @param clientSecret - API client secret key
	 */
	public ExchangeClient(String clientId, String clientSecret) {
		super(clientId, clientSecret);
	}

	/**
	 * GET /users/info
	 * @return Exchange User Information
	 * @throws Exception
	 *
	 */
	public UsersInfoResponse getUserInfo() throws Exception {
		return this.process(null, USERS_INFO_URL, HttpMethod.GET, UsersInfoResponse.class);
	}
}
