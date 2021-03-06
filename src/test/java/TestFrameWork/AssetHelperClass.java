package TestFrameWork;


import io.restassured.response.Response;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AssetHelperClass {
    RestOperations operations;
    HelperClass helper;

    public Response createAssetLoggedin(String env, String accessToken, String assetType, String assetID,Map<String, Object> createAssetBody){
        operations = new RestOperations();
        Setup.setupPersonURL(env);
        return operations.putMethod(createAssetBody,"/v1/person/CURRENT/asset/" +assetType + "/" +assetID,accessToken);
    }

    public Response createAssetNonLoggedin(String env,String uid,String assetType,String assetID,Map<String, Object> createAssetBody){
        operations = new RestOperations();
        Setup.setupPersonURL(env);
        return operations.putMethod(createAssetBody, "/v1/person/" + uid + "/" + assetType + "/" + assetID);
    }

    public Response createAssetWithoutAssetIDLoggedin(String env,String accessToken,String assetType,Map<String, Object> createAssetBody){
        operations = new RestOperations();
        Setup.setupPersonURL(env);
        return operations.putMethod(createAssetBody,"/v1/person/CURRENT/generator/asset/" +assetType,accessToken);
    }

    public Response deleteAssetLoggedin(String env,String accessToken,String assetType,String assetID){
        operations = new RestOperations();
        Setup.setupPersonURL(env);
        return operations.deleteMethod("/v1/person/CURRENT/asset/" + assetType + "/" + assetID, accessToken);
    }

    public Response deleteAssetNonLoggedin(String env,String assetType,String assetID){
        operations = new RestOperations();
        Setup.setupPersonURL(env);
        return operations.deleteMethod("/v1/asset/" + assetType + "/" + assetID);
    }

    public void deleteAllAssetByAssetType(String env,String accessToken,String assetType){
        operations = new RestOperations();
        Setup.setupPersonURL(env);
        List<Map<String, Object>> getAssetResponseBody = operations.getMethod("/v1/person/CURRENT/asset/" + assetType, accessToken).getBody().as(List.class);
        for(Map<String, Object> asset : getAssetResponseBody){
            operations.deleteMethod("/v1/person/CURRENT/asset/" + assetType + "/" + asset.get("asset_value"), accessToken);
        }
    }

    public void createAssetIfNotPresent(String env,String username,String password,String assetType,String assetID){
        helper = new HelperClass();
        String accessToken = helper.getTokenWithSignedinProof(env,username,password);
        operations = new RestOperations();
        Setup.setupPersonURL(env);
        Response createAssetResponse = operations.putMethod(new HashMap<>(), "/v1/person/CURRENT/asset/" + assetType + "/" + assetID,accessToken);
        if(createAssetResponse.getStatusCode() == 409){
            if("asset_creation_limit_reached".equalsIgnoreCase(createAssetResponse.as(Map.class).get("error").toString())){
                deleteAllAssetByAssetType(env, accessToken, assetType);
                createAssetIfNotPresent(env, accessToken, assetType, assetID);
            }
        }
    }

    public void createAssetIfNotPresent(String env,String accessToken,String assetType,String assetID){
        operations = new RestOperations();
        Setup.setupPersonURL(env);
        Response createAssetResponse = operations.putMethod(new HashMap<>(), "/v1/person/CURRENT/asset/" + assetType + "/" + assetID,accessToken);
        if(createAssetResponse.getStatusCode() == 409){
            if("asset_creation_limit_reached".equalsIgnoreCase(createAssetResponse.as(Map.class).get("error").toString())){
                deleteAllAssetByAssetType(env, accessToken, assetType);
                createAssetIfNotPresent(env, accessToken, assetType, assetID);
            }
        }
    }

}
