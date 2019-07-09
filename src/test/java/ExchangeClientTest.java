import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import response.UsersInfoResponse;

class ExchangeClientTest {

	private static final String CLIENT_ID = "";
	private static final String CLIENT_SECRET = "";

	private ExchangeClient client = new ExchangeClient(CLIENT_ID, CLIENT_SECRET);

	@Test
	void getUsersInfo() throws Exception {
		UsersInfoResponse response = client.getUserInfo();
		Assertions.assertNotNull(response);
		Assertions.assertNotNull(response.getUser().getId());
		Assertions.assertNotNull(response.getUser().getEmail());
	}
}
