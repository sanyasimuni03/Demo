package com.insurance.www.insuranceController;

import java.io.IOException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.insurance.www.exceptions.CallBackException;
import com.insurance.www.insuranceService.HumanaInsuranceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/humana")
@RequiredArgsConstructor
public class HumanaInsuranceController {
	
	 private final HumanaInsuranceService humanaInsuranceService;
	
	@GetMapping("/authorize")
	public void authorize() throws IOException {
		humanaInsuranceService.authorize();
	}

	@GetMapping("/registered/callback")
	public void handleCallback(@RequestParam("code") String authorizationCode) throws IOException {
		if (authorizationCode == null) {
			throw new CallBackException("Code not found !");
		}
		humanaInsuranceService.handleCallback(authorizationCode);
	}

}
