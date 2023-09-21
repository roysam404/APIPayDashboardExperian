package com.qa.gorest.tests;


import static org.hamcrest.Matchers.equalTo;

import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.qa.gorest.base.BaseTest;
import com.qa.gorest.client.RestClient;
import com.qa.gorest.constants.APIHttpStatus;
import com.qa.gorest.utils.*;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class AmadeusAPITest extends BaseTest{
	
	private String accessToken;
	Xls_Reader reader= new Xls_Reader();
	
	//@Parameters({"baseURI", "grantType", "clientId", "clientSecret"})
	@BeforeMethod
	public void flightAPiSetup() {
		restClient = new RestClient(prop, prop.getProperty("baseURI"));
		accessToken = restClient.getAccessToken(prop.getProperty("baseURI"), AMADEUS_TOKEN_ENDPOINT, prop.getProperty("grantType"), prop.getProperty("clientId"), prop.getProperty("clientSecret"));
	}
	
	
	@Test
	public void getFlightInfoTest() {
		
		RestClient restClientFlight = new RestClient(prop, baseURI);
		
		Map<String, Object> queryParams = new HashMap<String, Object>();
		
		String origin=reader.getTestData("APITestData","User","User_002","Origin");
		//int maxPrice=Integer.parseInt(reader.getTestData("APITestData","User","User_002","MaxPrice"));
		String maxPrice=reader.getTestData("APITestData","User","User_002","MaxPrice");		
		Double maxPrice1=Double.parseDouble(maxPrice);
		queryParams.put("origin", origin);
		queryParams.put("maxPrice", maxPrice1);
		System.out.println("Origin: "+origin);
		System.out.println("Max Price: "+ maxPrice);

		Map<String, String> headersMap = new HashMap<String, String>();
		headersMap.put("Authorization", "Bearer "+ accessToken);
		
		Response flightDataResponse = restClientFlight.get(AMADEUS_FLIGHTBOKKING_ENDPOINT, queryParams, headersMap, false, true)
							.then().log().all()
							.assertThat()
								.statusCode(APIHttpStatus.BAD_REQUEST_400.getCode())
									.and()
										.extract()
											.response();
		
		JsonPath js = flightDataResponse.jsonPath();
		String type = js.get("data[0].type");
		System.out.println(type);//flight-destination
		//Assert.assertEquals(type, "flight-destination");
		Assert.assertEquals(type, null);
		
		/*
		 * RestClient clientGet = new RestClient(prop, baseURI);
		 * 
		 * //2. GET: clientGet.get(GOREST_ENDPOINT+"/"+userId, true, true)
		 * .then().log().all() .assertThat().statusCode(APIHttpStatus.OK_200.getCode())
		 * .assertThat().body("id", equalTo("Samir"));
		 */		
		System.out.println("end test");
		
	}
	
	
	

}
