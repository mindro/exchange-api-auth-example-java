package response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenResponse {

	@JsonProperty("access_token")
	private String accessToken;

	@JsonProperty("token_type")
	private String tokenType;

	@JsonProperty("expires_in")
	private String expiresIn;

	@JsonProperty("scope")
	private String scope;

	@JsonProperty("user_id")
	private String userId;

	@JsonProperty("user_name")
	private String userName;

	@JsonProperty("environment_code")
	private String environmentCode;

	@JsonProperty("login_ip")
	private String loginIp;

	@JsonProperty("authorities")
	private String authorities;

	@JsonProperty("jti")
	private String jti;
}
