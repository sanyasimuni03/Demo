package com.insurance.www.insuranceService;

import java.io.IOException;

public interface AetnaInsuranceService {
	
	void authorize() throws IOException;

	void handleCallback(String authorizationCode) throws IOException;
}
