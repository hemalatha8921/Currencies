package sample;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

import com.fasterxml.jackson.databind.JsonNode;
import common.FileUtilities;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONObject;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.*;
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class getCurrencies {

    static RequestSpecification request;
    static Response response;
    String getListOfCurrenciesAPI = "https://cdn.jsdelivr.net/gh/fawazahmed0/currency-api@1/latest/currencies.json";
    String currencyExchangeAPI = "https://cdn.jsdelivr.net/gh/fawazahmed0/currency-api@1/latest/currencies/%s.json";

    static ArrayList<String> currencyCodes= new ArrayList<>();
    ArrayList<String> abbreviations= new ArrayList<>();

    @Test
    @Order(1)
    public void verifyListOfCurrencies(){
        System.out.println(getListOfCurrenciesAPI);
        request =RestAssured.given()
                .relaxedHTTPSValidation()
                .urlEncodingEnabled(false)
                .baseUri(getListOfCurrenciesAPI);
        response= request.when().get();
        System.out.println("Status code"+response.statusCode());
        //System.out.println(response.prettyPrint());
    }
    @Test
    @Order(2)
    public void verifyTheNumberOfItems(){
        ArrayList<String> listOfItems = new ArrayList<>(Arrays.asList(response.getBody().asString().split(",")));
        System.out.println("Total number of Items in The Response: "+listOfItems.size());
        //Validation1
        Assertions.assertTrue(listOfItems.size()>20);
    }
    @Test
    @Order(3)
    public void verifyTheExpectedCurrenciesAndStoreAbbreviationsAndKeys() throws Exception{
        JSONObject jsonObject = new JSONObject(response.getBody().asString());
        Iterator keys = jsonObject.keys();
        while (keys.hasNext()){
            String currencyKeyName = (String)keys.next();
            currencyCodes.add(currencyKeyName);
            String currencyKeyValue = jsonObject.getString(currencyKeyName);
            abbreviations.add(currencyKeyValue);
        }
        Collections.sort(currencyCodes);
        Collections.sort(abbreviations);
        System.out.println("The List of Abbreviations are : "+abbreviations.toString());
        Assertions.assertTrue(abbreviations.contains("Pound sterling"));
        Assertions.assertTrue(abbreviations.contains("United States dollar"));
        //Assertions.assertTrue(abbreviations.contains("British Pound"));
    }
    @Test
    @Order(4)
    public void verifyTheExchangeRatesForAllCurrencyCodes() throws Exception {
        System.out.println("size of currency codes:" + currencyCodes.size());
        for (String name : currencyCodes) {
            String url = String.format(currencyExchangeAPI, name);
            System.out.println(url);
            request = RestAssured.given()
                    .relaxedHTTPSValidation()
                    .urlEncodingEnabled(false)
                    .baseUri(url);
            response = request.when().get();
            System.out.println(response.statusCode());

            String exchangeRateDate = response.getBody().jsonPath().getString("date");

            JsonNode exchangeRates = response.getBody().jsonPath().getObject(name, JsonNode.class);
            System.out.println(exchangeRates);
            //Store all theExchange rates for all currencies to the output folder for 1st iteration in a day
            FileUtilities.createOutputFolderAndStoreTheExchangeRates(exchangeRateDate, name, exchangeRates);
            String storedJsonFile = FileUtilities.readFile(System.getProperty("user.dir") + "/" + "output_" + exchangeRateDate + "/" + exchangeRateDate + "_" + name + ".json");
            JSONObject todayFirstIterationExchangeRateResponse = new JSONObject(storedJsonFile);
            System.out.println("Today's 1st Iterations Rates:" + todayFirstIterationExchangeRateResponse);
            assertThatJson(exchangeRates).isEqualTo(todayFirstIterationExchangeRateResponse);
        }
    }



}
