package com.appdynamics.extensions.hipchat;

/**
 * @author ashish mehta
 *
 */
public class HipChat {

	private String version = "v2";
	private String scheme = "https";
	private String host = "api.hipchat.com";
	private String authToken;

	public String getScheme() {
		return scheme;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public String getVersion() {
		return version;
	}

}
