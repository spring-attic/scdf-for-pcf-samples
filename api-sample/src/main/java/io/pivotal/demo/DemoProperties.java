package io.pivotal.demo;

import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *
 */
@ConfigurationProperties(prefix = "scdf-demo")
public class DemoProperties {

	@NotNull
	private String accessTokenUrl;

	@NotNull
	private String clientId;

	@NotNull
	private String clientSecret;

	@NotNull
	private String dataflowUrl;

	public String getAccessTokenUrl() {
		return accessTokenUrl;
	}

	public void setAccessTokenUrl(String accessTokenUrl) {
		this.accessTokenUrl = accessTokenUrl;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getDataflowUrl() {
		return dataflowUrl;
	}

	public void setDataflowUrl(String dataflowUrl) {
		this.dataflowUrl = dataflowUrl;
	}
}
