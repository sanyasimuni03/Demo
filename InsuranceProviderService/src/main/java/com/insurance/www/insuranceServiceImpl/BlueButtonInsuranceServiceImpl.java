package com.insurance.www.insuranceServiceImpl;

import java.io.IOException;
import java.net.URLEncoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.insurance.www.insuranceService.BlueButtonInsuranceService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BlueButtonInsuranceServiceImpl  implements BlueButtonInsuranceService{
	
	
	//run  http://localhost:6868/bluebutton/authorize
	
	@Autowired
	private HttpServletResponse response;

	@Value("${bluebutton.client.id}")
	private String clientId;

	@Value("${bluebutton.client.secret}")
	private String clientSecret;

	@Value("${bluebutton.auth-url}")
	private String bluebuttonAuthUrl;

	@Value("${bluebutton.callback-url}")
	private String callBackUrl;

	@Value("${bluebutton.scopes}")
	private String scopes;

	@Value("${bluebutton.aud}")
	private String aud;

	@Value("${bluebutton.token-exchange-url}")
	private String bluebuttonTokenExchangeUrl;

	@Value("${bluebutton.patient-verification-url}")
	private String   bluebuttonPatientVerificationUrl;

	@Value("${bluebutton.redirect-url}")
	private String redirectUri;

	@Value("${bluebutton.error-url}")
	private String errorUri;
	
	
	
	

	@Override
	public void authorize() throws IOException {
		
		String authorizationUrl = bluebuttonAuthUrl + "?response_type=code" + "&client_id=" + clientId + "&redirect_uri="
				+ callBackUrl + "&scope=" + scopes + "&state=1234" + "&aud=" + aud;
		response.sendRedirect(authorizationUrl);
	}
		


	@Override
	public void handleCallback(String authorizationCode) throws IOException {
		
	
		
		String tokenUrl = bluebuttonTokenExchangeUrl;

		
		// Prepare the request body for token exchange
		String requestBody = "grant_type=authorization_code" 
		+ "&code=" + authorizationCode 
		+ "&client_id=" + clientId
		+ "&client_secret="+ clientSecret;
		//+ "&redirect_Uri= "+redirectUri;

		// Set headers for token exchange
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		// Create the request entity for token exchange
		HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
		RestTemplate restTemplate = new RestTemplate();

		// Send POST request to exchange authorization code for access token
		ResponseEntity<String> tokenResponse = restTemplate.exchange(tokenUrl, HttpMethod.POST, entity, String.class);

		if (tokenResponse.getStatusCode() == HttpStatus.OK) {
			String tokenResponseBody = tokenResponse.getBody();
			String accessToken = extractAccessToken(tokenResponseBody);

			// Use the access token to make a request to the Patient Access API
			String patientApiUrl = bluebuttonPatientVerificationUrl;
			HttpHeaders patientApiHeaders = new HttpHeaders();
			patientApiHeaders.set("Authorization", "Bearer " + accessToken);
			HttpEntity<String> patientApiEntity = new HttpEntity<>(patientApiHeaders);

			ResponseEntity<String> patientApiResponse = restTemplate.exchange(patientApiUrl, HttpMethod.GET,
					patientApiEntity, String.class);

			if (patientApiResponse.getStatusCode() == HttpStatus.OK) {
				String patientData = patientApiResponse.getBody();
				// Encode both the token and the patient data
				String redirectUrl = redirectUri + "?data=" + URLEncoder.encode(patientData, "UTF-8");
				response.sendRedirect(redirectUrl);
			} else {
				response.sendRedirect(errorUri);
			}
		} else {
			response.sendRedirect(errorUri);
		}
	}

	
	private String extractAccessToken(String responseBody) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(responseBody);
			JsonNode accessTokenNode = jsonNode.get("access_token");
			return accessTokenNode != null ? accessTokenNode.asText() : null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
			
	}
	
