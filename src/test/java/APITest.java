package test.java;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.testng.Assert.assertEquals;

public class APITest {
    RandomGenerator rg = new RandomGenerator();
    String baseURL = "https://api.github.com/";
    //Documentation  "https://docs.github.com/en/rest"
    CloseableHttpClient client;
    HttpGet getRequest;
    HttpResponse response;


    HttpDelete deleteRequest;

    HttpPost postRequest;

    String jsonMimeType = "application/json";
    String email = "victoria.prokopchuk@gmail.com";
    String user = "login";
    String pass = "password";
    String token = "6076d36ef63014999a1360f34f5075f921c8ba84";
    private String auth;

    @BeforeClass
    public void setUp() throws Exception {

    }

    @BeforeTest
    public void init() {
        client = HttpClientBuilder.create().build();
    }

    @AfterTest
    public void tearDown() throws IOException {
        client.close();
    }

    @DataProvider(name = "users")
    public Object[][] data() {

        return new Object[][]{{"prokov", "200"}, {"prokov", "200"}, {"invfkykfyhUser", "404"}, {"invfkykfyhUser", "404"}};
    }

    @DataProvider(name = "endpoints")
    public Object[][] url() {
        return new Object[][]{{"user"}, {"user/followers"}

        };

    }

//    @Test()
//    public void performanceTestGet() throws ClientProtocolException, IOException  {
//
//        List<GetUserThread> arr = new ArrayList<>();
//
//        int n =100;
//
//        for (int i = 0; i < n; i++) {
//            arr.add(new GetUserThread(baseURL + "users/" + user, makeHeader()));
//
//        }
//
//        for (int i = 0; i < n; i++) {
//            arr.get(i).start();
//
//        }
//
//        for (int i = 0; i < n; i++) {
//            try {
//
//                arr.get(i).join();
//
//                Assert.assertEquals(arr.get(i).getStatus(),"200");
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//        }
//
//    }

    @Test(dataProvider = "users")
    public void getUserGeneric(String user, String status) throws ClientProtocolException, IOException {

        // client = HttpClientBuilder.create().build();

        // Given
        getRequest = new HttpGet(baseURL + "users/" + user);
        getRequest.setHeader(HttpHeaders.AUTHORIZATION, makeHeader());

        // When
        response = client.execute(getRequest);

        // Then
        String s = String.valueOf(response.getStatusLine().getStatusCode());
        assertEquals(s, status);

        client.close();

    }

    @Test
    public void NotExistentUser404() throws ClientProtocolException, IOException {

        // Given
        getRequest = new HttpGet(baseURL + "users/" + rg.getRandomString(8, 9));

        // When
        response = client.execute(getRequest);

        // Then

        assertEquals(response.getStatusLine().getStatusCode(), HttpStatus.SC_NOT_FOUND);

    }

    @Test(dataProvider = "endpoints")
    public void NotAuthentificated401Generic(String endpoint) throws ClientProtocolException, IOException {

        client = HttpClientBuilder.create().build();

        // Given
        getRequest = new HttpGet(baseURL + "user");

        // When
        response = client.execute(getRequest);

        // Then

        assertEquals(response.getStatusLine().getStatusCode(), 401);

        client.close();

    }

    @Test // valid user

    public void testRestAssured() throws ClientProtocolException, IOException {


        getRequest = new HttpGet(baseURL + "users/" + user);

        // When
        response = client.execute(getRequest);

        // Then
        assertEquals(response.getStatusLine().getStatusCode(),
                HttpStatus.SC_OK);

    }

    @Test // valid user

    public void ExistentUser200() throws IOException {

        client = HttpClientBuilder.create().build();

        // Given
        getRequest = new HttpGet(baseURL + "users/" + user);

        // When
        response = client.execute(getRequest);

        // Then
        assertEquals(response.getStatusLine().getStatusCode(), HttpStatus.SC_NOT_FOUND);

    }

    @Test
    public void responseTypeJson() throws ClientProtocolException, IOException {

        // Given
        getRequest = new HttpGet(baseURL + "users/" + user);

        // When
        response = client.execute(getRequest);

        // Then
        String mimeType = ContentType.getOrDefault(response.getEntity()).getMimeType();
        assertEquals(jsonMimeType, mimeType);
    }

    @Test
    public void checkResponseBody() throws ClientProtocolException, IOException {


        Response r = RestAssured.given()
                // .body(arg0)
                .when().get(baseURL + "users/" + user);

        JSONObject json = new JSONObject(r.asString());

        System.out.println(json);

        assertEquals(user, json.getString("login"));

        // Given
        getRequest = new HttpGet(baseURL + "users/" + user);

        // When
        response = client.execute(getRequest);

        // System.out.println(r.getString("login"));

        // StringBuffer result = new StringBuffer();
        // String line;
        // while ((line = new BufferedReader(new
        // InputStreamReader(response.getEntity().getContent()))
        // .readLine()) != null) {
        // result.append(line);
        // }
        // JSONObject o = new JSONObject(result.toString());
        // System.out.println(o);
        //
        // Assert.assertEquals(user, o.getString("login"));


        // // Then
//        GitHubUser resource =
//                RetrieveUtilSimple.retrieveResourceFromResponse(response,
//                        GitHubUser.class);
//        Assert.assertEquals(user, resource.getLogin());
//        Assert.assertEquals("User", resource.getType());
    }

    @Test(priority = 1)
    //test delete
    //TODO Get repo list and delete by some criteria

    //DELETE /repos/:owner/:repo/contents/:path
    public void testDelete() throws ClientProtocolException, IOException {


        // Given

        deleteRequest = new HttpDelete(baseURL + "repos/" + user + "/Repo920630");
        // Only after 403 - add authorization
        deleteRequest.setHeader(HttpHeaders.AUTHORIZATION, "token " + token);

        // When
        response = client.execute(deleteRequest);

        // Then
        assertEquals(response.getStatusLine().getStatusCode(), 204);

    }


    @Test(priority = 1)

    //TODO Get repo list and delete by some criteria

    //DELETE /repos/:owner/:repo/contents/:path
    public void testDeleteMyRepos() throws IOException {

        for (String s : getMyRepoList()) {

            System.out.println(s);
            if (!s.equals("prokov/LoggingReportingDemo") && s.toLowerCase().contains("test")) {
                deleteRequest = new HttpDelete(baseURL + "repos/" + s);
                // Only after 403 - add authorization
                deleteRequest.setHeader(HttpHeaders.AUTHORIZATION, "token " + token);

                // When
                response = client.execute(deleteRequest);
                System.out.println(response.getStatusLine().getStatusCode());
           }


        }


    }

    private List<String> getMyRepoList() {

        ArrayList<String> result = new ArrayList();
        // Given


        Response r = RestAssured.given()
                .header("Accept", "application/vnd.github.v3+json")
                //    .header(HttpHeaders.AUTHORIZATION,makeHeader())
//users/prokov/repos\?type=all
                .when().get(baseURL + "users/" + user + "/repos");


        JSONArray jsonArray = new JSONArray(r.asString());
        int responseSize = jsonArray.length();

        for (int i = 0; i < responseSize; i++) {

            result.add(r.jsonPath().get("full_name[" + i + "]").toString());

            //   System.out.println(r.jsonPath().get("full_name["+i+"]").toString());

        }
        return result;


    }

    @Test //test post
    public void testCreate() throws IOException {
        // Given

        String authHeader = makeHeader();
        //  for (int i = 0; i < 3; i++) {

        client = HttpClientBuilder.create().build();

        String name = String.valueOf(new Random().nextInt(10000));
        //  System.out.println("Generated number: " + name);

        postRequest = new HttpPost(baseURL + "user/repos");
        postRequest.setHeader(HttpHeaders.AUTHORIZATION, authHeader);

        String reponame = "TestRepoName";
        String json = "{\"name\":\"" + reponame + "\"}";


        // String json = "{\"name\":\"TelRanRepo\" }";
        postRequest.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));

        // When
        response = client.execute(postRequest);

        System.out.println(response);

        // Then
        assertEquals(response.getStatusLine().getStatusCode(), 201);


        client.close();
        //  }

//		 RestAssured.given()
//		 .body( "{\"name\":\"deleteme\"}")
//		 .header("Content-Type", "application/json")
//		 //.param("name", "testrepo6")
//		 .header(HttpHeaders.AUTHORIZATION,authHeader)
//		 .when()
//		 .post(baseURL + "user/repos")
//		 .then()
//		 .statusCode(201); //figure out why I have matcher error here


    }

    private String makeHeader() {
        auth = email + ":" + pass;
        byte[] encodedAuth = Base64.encodeBase64(auth
                .getBytes(Charset.forName("ISO-8859-1")));
        String authHeader = "Basic " + new String(encodedAuth);
        System.out.println(authHeader);
        return authHeader;
    }

    private static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

}
