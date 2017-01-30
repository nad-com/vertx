package com.test.dto;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryResult;
import com.couchbase.client.java.query.N1qlQueryRow;

public class StatementDTO {

    private static final String CLASS_NAME = StatementDTO.class.getSimpleName();

    private static CouchbaseEnvironment env = null;
    private static Cluster cluster = null;
    private static Bucket bucket;
 

    private static final String E0_CLUSTER = "127.0.0.1:8091";

    private static final String E0_BUCKET = "default";

    static {
    	
        env = DefaultCouchbaseEnvironment.builder()
                .connectTimeout(30000) // 10000ms = 10s, default is 5s
                .build();
        System.out.println("creating the cluster");
 
        cluster = CouchbaseCluster.create(env, E0_CLUSTER);
        
        System.out.println("created the cluster instance:"+cluster.toString());
        
        bucket = cluster.openBucket(E0_BUCKET);
        System.out.println("opened the bucket instance:"+bucket.name());
   

    }
    public static void init(){
    	System.out.println("************init**************");        
    }

    public static void main(String[] args){
    	insertStatement("1", "John", "101.25");
    	System.out.println(getStatement("1"));
    }

    
    public static String getStatement(String id) {

    	JsonDocument statement = bucket.get(JsonDocument.create(id));
    	JsonObject result = statement.content();
    	return result.toString();
    }
    
    public static void insertStatement(String id, String name, String balance) {
        // Create a JSON Document
        JsonObject statement = JsonObject.create()
                .put("id", id)
                .put("name", name)
                .put("balance", balance);

        //Store the Document
        bucket.upsert(JsonDocument.create(id, statement));
        System.out.println("Statement document inserted for id: "+id);
    }
    
    public static String getLatestAddress(String id){
    	
    	JsonDocument addressDoc = bucket.get(JsonDocument.create(id));
    	JsonObject result = addressDoc.content();
    	return result.toString();
    }
    
    public static Boolean addSignUpDetails(io.vertx.core.json.JsonObject cmInfo,String jwtToken){    	
    	JsonObject ShipingAddress = null;
    	    	
    	JsonObject signUpCardDetails = JsonObject.create();
    	JsonObject signUpBillingAddress = JsonObject.create();
    	JsonObject signUpDetails = JsonObject.create()
    			.put("jwtToken", jwtToken)    			
    			.put("clientId", cmInfo.getString("clientId"));
    	
    	signUpCardDetails.put("cm15", cmInfo.getString("cm15"))
    			.put("defaultCardFlag", cmInfo.getString("defaultCardFlag"))
    			.put("expiryMonth", cmInfo.getString("expiryMonth"))
    			.put("expiryYear", cmInfo.getString("expiryYear"))
    			.put("nameOnCard", cmInfo.getString("nameOnCard"))    			
    			.put("cVV", cmInfo.getString("cVV"))
    			.put("firstName", cmInfo.getString("firstName"))
    			.put("lastName", cmInfo.getString("lastName"))
    			.put("cardNickName", cmInfo.getString("cardNickName"));
    	
    	signUpBillingAddress.put("country", cmInfo.getString("country"))
    			.put("addressLine1", cmInfo.getString("addressLine1"))
    			.put("addressLine2", cmInfo.getString("addressLine2"))
    			.put("city", cmInfo.getString("city"))
    			.put("stateOrTerritory", cmInfo.getString("stateOrTerritory"))    			
    			.put("PostalCode", cmInfo.getString("PostalCode"))
    			.put("sameAsBillingAddress", cmInfo.getString("sameAsBillingAddress"));
    	
    	signUpDetails.put("cardInformation", signUpCardDetails)
    	             .put("billingAddress", signUpBillingAddress);
    			
    			if("Y".equalsIgnoreCase(cmInfo.getString("sameAsBillingAddress"))){
    				
    				ShipingAddress = JsonObject.create();
    				ShipingAddress.put("defaultAddressFlag", "Y")
    				.put("recipientCountry", cmInfo.getString("country"))    				
    				.put("recipientFirstName", cmInfo.getString("firstName"))
    				.put("recipientLastName", cmInfo.getString("lastName"))
    				.put("recipientAddressLine1", cmInfo.getString("addressLine1"))
    				.put("recipientAddressLine2", cmInfo.getString("addressLine2"))
    				.put("recipientCity", cmInfo.getString("city"))
    				.put("recipientStateOrTerritory", cmInfo.getString("stateOrTerritory"))    				
    				.put("recipientPostalCode", cmInfo.getString("PostalCode"))
    				.put("recipientPhoneNumber", "");
    				signUpDetails.put("shippingAddress", ShipingAddress);
    			}
    			
    			System.out.println("signUpDetails"+signUpDetails);
    	try{
		    	JsonDocument addressDoc = JsonDocument.create(jwtToken+":card", signUpDetails);
		    	
		    	System.out.println("to update @@ "+signUpDetails);
		    	
		    	bucket.upsert(addressDoc);
    	
    	}catch(Exception e){
    		e.printStackTrace();
    		return false;
    	}
    	   JsonDocument jsonDocument1 = bucket.get(jwtToken+":card");
   	   
    	
    	System.out.println("Address update to DB is successful===>"+jsonDocument1.content());
    	
    	
    	return true;
    }
    
    public static Boolean addSignUpCardDetails(io.vertx.core.json.JsonObject cmInfo){    	
    	JsonObject ShipingAddress = null;
    	String jwtToken = cmInfo.getString("jwtToken");
    	System.out.println("e============jwtToken==========>"+cmInfo.getString("jwtToken"));
    	JsonObject signUpCardDetails = JsonObject.create();
    	JsonObject signUpBillingAddress = JsonObject.create();
    	JsonObject signUpDetails = JsonObject.create()
    			.put("jwtToken", jwtToken)    			
    			.put("clientId", cmInfo.getString("clientId"));
    	
    	signUpDetails.put("cm15", cmInfo.getString("cm15"))
    			.put("defaultCardFlag", cmInfo.getString("defaultCardFlag"))
    			.put("expiryMonth", cmInfo.getString("expiryMonth"))
    			.put("expiryYear", cmInfo.getString("expiryYear"))
    			.put("nameOnCard", cmInfo.getString("nameOnCard"))    			
    			.put("cVV", cmInfo.getString("cVV"))
    			.put("firstName", cmInfo.getString("firstName"))
    			.put("lastName", cmInfo.getString("lastName"))
    			.put("cardNickName", cmInfo.getString("cardNickName"));
    	
    	signUpDetails.put("country", cmInfo.getString("country"))
    			.put("addressLine1", cmInfo.getString("addressLine1"))
    			.put("addressLine2", cmInfo.getString("addressLine2"))
    			.put("city", cmInfo.getString("city"))
    			.put("stateOrTerritory", cmInfo.getString("stateOrTerritory"))    			
    			.put("PostalCode", cmInfo.getString("PostalCode"))
    			.put("sameAsBillingAddress", cmInfo.getString("sameAsBillingAddress"));
    	
    	/*signUpDetails.put("cardInformation", signUpCardDetails)
    	             .put("billingAddress", signUpBillingAddress);*/
    			
    			if("Y".equalsIgnoreCase(cmInfo.getString("sameAsBillingAddress"))){
    				
    				//ShipingAddress = JsonObject.create();
    				signUpDetails.put("defaultAddressFlag", "Y")
    				.put("recipientCountry", cmInfo.getString("country"))    				
    				.put("recipientFirstName", cmInfo.getString("firstName"))
    				.put("recipientLastName", cmInfo.getString("lastName"))
    				.put("recipientAddressLine1", cmInfo.getString("addressLine1"))
    				.put("recipientAddressLine2", cmInfo.getString("addressLine2"))
    				.put("recipientCity", cmInfo.getString("city"))
    				.put("recipientStateOrTerritory", cmInfo.getString("stateOrTerritory"))    				
    				.put("recipientPostalCode", cmInfo.getString("PostalCode"))
    				.put("recipientPhoneNumber", "");
    				//signUpDetails.put("shippingAddress", ShipingAddress);
    			}
    			
    			System.out.println("signUpDetails"+signUpDetails);
    	try{
    		System.out.println("Dco ID ====>"+jwtToken+":Card");
    	JsonDocument addressDoc = JsonDocument.create(jwtToken+":Card", signUpDetails);
    	
    	System.out.println("to update @@ "+signUpDetails);
    	
    	bucket.upsert(addressDoc);
    	
    	}catch(Exception e){
    		e.printStackTrace();
    		return false;
    	}
    	   JsonDocument jsonDocument1 = bucket.get(jwtToken+":Card");
   	   
    	
    	System.out.println("Address update to DB is successful===>"+jsonDocument1.content());
    	
    	
    	return true;
    }
    
 public static Boolean addSignUpShipAdderDetails(io.vertx.core.json.JsonObject shipCMInfo){
    	
	 String jwtToken =  shipCMInfo.getString("jwtToken");

    	
    	JsonObject ShipingAddress = null;	
    				
		ShipingAddress = JsonObject.create();
		ShipingAddress.put("defaultAddressFlag", shipCMInfo.getString("defaultAddressFlag"))
			.put("recipientCountry", shipCMInfo.getString("recipientCountry"))    				
			.put("recipientFirstName", shipCMInfo.getString("recipientFirstName"))
			.put("recipientLastName", shipCMInfo.getString("recipientLastName"))
			.put("recipientAddressLine1", shipCMInfo.getString("recipientAddressLine1"))
			.put("recipientAddressLine2", shipCMInfo.getString("recipientAddressLine2"))
			.put("recipientCity", shipCMInfo.getString("recipientCity"))
			.put("recipientStateOrTerritory", shipCMInfo.getString("recipientStateOrTerritory"))    				
			.put("recipientPostalCode", shipCMInfo.getString("recipientPostalCode"))
			.put("recipientPhoneNumber", shipCMInfo.getString("recipientPhoneNumber"));

    	try{
    		System.out.println("Dco ID ====>"+jwtToken+":Card");
	    	JsonDocument addressDoc = JsonDocument.create(jwtToken+":Ship", ShipingAddress);
	    	
	    	System.out.println("to update @@ "+ShipingAddress);
	    	
	    	bucket.upsert(addressDoc);
	    	
	    		
	    	
    	
    	}catch(Exception e){
    		e.printStackTrace();
    		return false;
    	}
    	JsonDocument jsonDocument1 = bucket.get(jwtToken+":Ship");
	   	   
    	
    	System.out.println("Address update to DB is successful++++>"+jsonDocument1.content());
    	return true;
 }
    
    public static Boolean addSignUpShipDetails(io.vertx.core.json.JsonObject shipCMInfo,String jwtToken){
    	
    	
    	JsonDocument jsonDocument = bucket.get(jwtToken);
    	JsonObject signUpDetails = jsonDocument.content();
   	   
    	
    	System.out.println("Address update to DB is successful===>"+jsonDocument.content());
    	
    	JsonObject ShipingAddress = null;	
    				
		ShipingAddress = JsonObject.create();
		ShipingAddress.put("defaultAddressFlag", shipCMInfo.getString("defaultAddressFlag"))
			.put("recipientCountry", shipCMInfo.getString("recipientCountry"))    				
			.put("recipientFirstName", shipCMInfo.getString("recipientFirstName"))
			.put("recipientLastName", shipCMInfo.getString("recipientLastName"))
			.put("recipientAddressLine1", shipCMInfo.getString("recipientAddressLine1"))
			.put("recipientAddressLine2", shipCMInfo.getString("recipientAddressLine2"))
			.put("recipientCity", shipCMInfo.getString("recipientCity"))
			.put("recipientStateOrTerritory", shipCMInfo.getString("recipientStateOrTerritory"))    				
			.put("recipientPostalCode", shipCMInfo.getString("recipientPostalCode"))
			.put("recipientPhoneNumber", shipCMInfo.getString("recipientPhoneNumber"));
		
		signUpDetails.put("shippingAddress", ShipingAddress);
    		
    			
    	System.out.println("signUpDetails"+signUpDetails);
    	try{
	    	JsonDocument addressDoc = JsonDocument.create(jwtToken, signUpDetails);
	    	
	    	System.out.println("to update @@ "+signUpDetails);
	    	
	    	bucket.upsert(addressDoc);
	    	
	    		JsonDocument jsonDocument1 = bucket.get(jwtToken);
	   	   
	    	
	    	System.out.println("Address update to DB is successful++++>"+jsonDocument1.content());
    	
    	}catch(Exception e){
    		e.printStackTrace();
    		return false;
    	}
    	   
    	
    	
    	return true;
    }
  
 public static Boolean addSignUpUserDetail(io.vertx.core.json.JsonObject inAddress,String jwtToken){
    	
    	
    	JsonDocument jsonDocument = bucket.get(jwtToken);
    	JsonObject signUpDetails = jsonDocument.content();
   	   
    	
    	System.out.println("Address update to DB is successful===>"+jsonDocument.content());
    	
    	//JsonObject userInfo = JsonObject.create();;
		
		
    	signUpDetails.put("email", inAddress.getString("email"))    				
				.put("Phone", inAddress.getString("Phone"))
				.put("publicGuid", inAddress.getString("publicGuid"))
				.put("userID", inAddress.getString("userID"));
		
    	//signUpDetails.put("userInformation", userInfo);

	
		System.out.println("signUpDetails"+signUpDetails);
    	try{
    		
	    	JsonDocument addressDoc = JsonDocument.create(jwtToken+":User", signUpDetails);
	    	
	    	System.out.println("to update @@ "+signUpDetails);
	    	
	    	bucket.upsert(addressDoc);
	    	
	    	JsonDocument jsonDocument1 = bucket.get(jwtToken);
	   	   
	    	
	    	System.out.println("Address update to DB is successful++++>"+jsonDocument1.content());
    	
    	}catch(Exception e){
    		e.printStackTrace();
    		return false;
    	}
    	   
    	
    	
    	return true;
    }
    
 public static Boolean addSignUpUserDetails(io.vertx.core.json.JsonObject inAddress){
	 String jwtToken =  inAddress.getString("jwtToken");
	 String oldJwtToken =  inAddress.getString("oldJwtToken");
	 
 	JsonDocument jsonDocumentCard = bucket.get(oldJwtToken+":Card");
 	JsonObject signUpCardDetails = jsonDocumentCard.content();
 	System.out.println("signUpCardDetails"+signUpCardDetails);

    	
	 

    	
    	JsonObject userInfo = JsonObject.create();;
		
		
    	
    	
    	userInfo.put("cm15", signUpCardDetails.getString("cm15"))
		.put("defaultCardFlag", signUpCardDetails.getString("defaultCardFlag"))
		.put("expiryMonth", signUpCardDetails.getString("expiryMonth"))
		.put("expiryYear", signUpCardDetails.getString("expiryYear"))
		.put("nameOnCard", signUpCardDetails.getString("nameOnCard"))    			
		.put("cVV", signUpCardDetails.getString("cVV"))
		.put("firstName", signUpCardDetails.getString("firstName"))
		.put("lastName", signUpCardDetails.getString("lastName"))
		.put("cardNickName", signUpCardDetails.getString("cardNickName"));

    	userInfo.put("country", signUpCardDetails.getString("country"))
		.put("addressLine1", signUpCardDetails.getString("addressLine1"))
		.put("addressLine2", signUpCardDetails.getString("addressLine2"))
		.put("city", signUpCardDetails.getString("city"))
		.put("stateOrTerritory", signUpCardDetails.getString("stateOrTerritory"))    			
		.put("PostalCode", signUpCardDetails.getString("PostalCode"))
		.put("sameAsBillingAddress", signUpCardDetails.getString("sameAsBillingAddress"));

/*signUpDetails.put("cardInformation", signUpCardDetails)
             .put("billingAddress", signUpBillingAddress);*/
		
		if("Y".equalsIgnoreCase(signUpCardDetails.getString("sameAsBillingAddress"))){
			
			//ShipingAddress = JsonObject.create();
			userInfo.put("defaultAddressFlag", signUpCardDetails.getString("defaultAddressFlag"))
			.put("recipientCountry", signUpCardDetails.getString("country"))    				
			.put("recipientFirstName", signUpCardDetails.getString("firstName"))
			.put("recipientLastName", signUpCardDetails.getString("lastName"))
			.put("recipientAddressLine1", signUpCardDetails.getString("addressLine1"))
			.put("recipientAddressLine2", signUpCardDetails.getString("addressLine2"))
			.put("recipientCity", signUpCardDetails.getString("city"))
			.put("recipientStateOrTerritory", signUpCardDetails.getString("stateOrTerritory"))    				
			.put("recipientPostalCode", signUpCardDetails.getString("PostalCode"))
			.put("recipientPhoneNumber", signUpCardDetails.getString("recipientPhoneNumber"));
			//signUpDetails.put("shippingAddress", ShipingAddress);
		}else {
			
		 	JsonDocument jsonDocumentShip = bucket.get(oldJwtToken+":Ship");
		 	JsonObject signUpShipDetails = jsonDocumentShip.content();
		 	System.out.println("signUpShipDetails"+signUpShipDetails);
			userInfo.put("defaultAddressFlag",signUpShipDetails.getString("defaultAddressFlag"))
			.put("recipientCountry", signUpShipDetails.getString("recipientCountry"))    				
			.put("recipientFirstName", signUpShipDetails.getString("recipientFirstName"))
			.put("recipientLastName", signUpShipDetails.getString("recipientLastName"))
			.put("recipientAddressLine1", signUpShipDetails.getString("recipientAddressLine1"))
			.put("recipientAddressLine2", signUpShipDetails.getString("recipientAddressLine2"))
			.put("recipientCity", signUpShipDetails.getString("recipientCity"))
			.put("recipientStateOrTerritory", signUpShipDetails.getString("recipientStateOrTerritory"))    				
			.put("recipientPostalCode", signUpShipDetails.getString("recipientPostalCode"))
			.put("recipientPhoneNumber", signUpShipDetails.getString("recipientPhoneNumber"));
		}
		userInfo.put("email", inAddress.getString("email"))    				
		.put("Phone", inAddress.getString("Phone"))
		.put("publicGuid", inAddress.getString("publicGuid"))
		.put("userID", inAddress.getString("userID"));
	
		System.out.println("userInfo"+userInfo);
    	try{
    		
	    	JsonDocument addressDoc = JsonDocument.create(jwtToken, userInfo);
	    	
	    	System.out.println("to update @@ "+userInfo);
	    	
	    	bucket.upsert(addressDoc);
	    	
	    	JsonDocument jsonDocument1 = bucket.get(jwtToken);
	   	   
	    	
	    	System.out.println("Address update to DB is successful++++>"+jsonDocument1.content());
    	
    	}catch(Exception e){
    		e.printStackTrace();
    		return false;
    	}
    	   
    	
    	
    	return true;
    }
    
 public static void updateAddress(io.vertx.core.json.JsonObject inAddress){
    	
    	String id = inAddress.getString("id");
    	JsonObject address = JsonObject.create()
    			.put("id", inAddress.getString("id"))
    			.put("addressLine1", inAddress.getString("addressLine1"))
    			.put("addressLine2", inAddress.getString("addressLine2"));
    	
    	JsonDocument addressDoc = JsonDocument.create(id, address);
    	
    	System.out.println("to update @@ "+address);
    	
    	bucket.upsert(addressDoc);
    	System.out.println("Address update to DB is successful");
    }
    
public static void validateMerchant(io.vertx.core.json.JsonObject inAddress){
    	
    	/*String id = inAddress.getString("id");
    	JsonObject address = JsonObject.create()
    			.put("id", inAddress.getString("id"))
    			.put("addressLine1", inAddress.getString("addressLine1"))
    			.put("addressLine2", inAddress.getString("addressLine2"));
    	
    	JsonDocument addressDoc = JsonDocument.create(id, address);
    	
    	System.out.println("to update @@ "+address);
    	
    	bucket.upsert(addressDoc);
    	System.out.println("Address update to DB is successful");*/
    }
    
    public static void loadWalletInfo() {

        // Create a JSON Document
        JsonObject mycaWallet = JsonObject.create()
                .put("id", "1")
                .put("name", "MYCA");

        // Create a JSON Document
        JsonObject aecWallet = JsonObject.create()
                .put("id", "2")
                .put("name", "AEC");

        JsonObject auWallet = JsonObject.create()
                .put("market", "AU")
                .put("wallets", JsonArray.from(mycaWallet, aecWallet));

        JsonObject usWallet = JsonObject.create()
                .put("market", "US")
                .put("wallets", JsonArray.from(mycaWallet));

        //Store the Document
        bucket.upsert(JsonDocument.create("AU_wallet", auWallet));
        bucket.upsert(JsonDocument.create("US_wallet", usWallet));

    }
    
    public static void loadClient() {
    	
    	System.out.println("Start : loadClient");

        JsonObject aec = JsonObject.create()
                .put("aec", "1.0")
                .put("secretKey", "4e017bfb-4dea-47a4-b0a8-c8892a680140");

        JsonObject aec2 = JsonObject.create()
        		.put("aec", "1.0")
                .put("secretKey", "4e017bfb-4dea-47a4-b0a8-c8892a680140");
        
        bucket.upsert(JsonDocument.create("a6f14031-696b-4359-8824-258f3700c868", aec));
        bucket.upsert(JsonDocument.create("0b515eea-7d1e-4ac6-9134-f6d7b7011af5", aec2));
        bucket.upsert(JsonDocument.create("d1dcb455-3b8a-420c-871b-ddfed58e8589", aec));
        bucket.upsert(JsonDocument.create("cca0887c-42d8-48e5-9a91-c649ab7ca141", aec));
        System.out.println("end : loadClient");
        
    }
    
    public static String getAecVersion(String clientId) {

	    String version= null;	
	    JsonDocument jsonDocument = bucket.get(clientId);
	    if(jsonDocument!=null){
	        System.out.println("getAecVersion===>"+bucket.get(clientId));
	        version =(String) jsonDocument.content().get("aec");
	        System.out.println("jsonDocument===>"+jsonDocument.content());
	        System.out.println("version===>"+version);
	    }
	       
	        return version;
    }
    
    public static void getSignUpDetails(String jwtToken) {

	   
	    JsonDocument jsonDocument = bucket.get(jwtToken);
	    System.out.println("jsonDocument===>"+jsonDocument.content());  
	       
	        
    }
    
    public static Boolean getSessionData(String jwtToken) {

    	 System.out.println("jwtToken===>"+jwtToken);  
	    JsonDocument jsonDocument = bucket.get(jwtToken);
	    System.out.println("jsonDocument===>"+jsonDocument.content());  
	    return true;   
	        
    }

    
    public static String getSessionData(io.vertx.core.json.JsonObject inAddress){
    	
    	JsonDocument jsonDocument = bucket.get(inAddress.getString("jwtToken")+":"+inAddress.getString("page"));
	    System.out.println("jsonDocument===>"+jsonDocument.content());  
    	JsonObject result = jsonDocument.content();
    	return result.toString();
    }
public static String getCMInfo(io.vertx.core.json.JsonObject inAddress){
    	
    	JsonDocument jsonDocument = bucket.get(inAddress.getString("jwtToken"));
	    System.out.println("jsonDocument===>"+jsonDocument.content());  
    	JsonObject result = jsonDocument.content();
    	return result.toString();
    }
public static String getCMInfo(String jwtToken){
	
	JsonDocument jsonDocument = bucket.get(jwtToken);
    System.out.println("jsonDocument===>"+jsonDocument.content());  
	JsonObject result = jsonDocument.content();
	return result.toString();
}
    
    public static String getSecretKey(String clientId) {

	    String secretKey= null;	
	    JsonDocument jsonDocument = bucket.get(clientId);
	    if(jsonDocument!=null){
	        System.out.println("getSecretKey===>"+bucket.get(clientId));
	        secretKey =(String) jsonDocument.content().get("secretKey");
	        System.out.println("jsonDocument===>"+jsonDocument.content());
	        System.out.println("secretKey===>"+secretKey);
	    }
	       
	        return secretKey;
    }
    
    public static Boolean verifyClient(String clientId) {

    	String version = null;
        
    	//Prints Content and Metadata of the stored Document
    	
    	 //Perform a N1QL Query
   /*     String E1_Query = "SELECT wallets FROM " + bucket.name() + " WHERE market = '" + clientId + "'";

        N1qlQueryResult result = bucket.query(
                N1qlQuery.parameterized(E1_Query,
                        JsonArray.from(clientId))
        );
        System.out.println("row size:"+result.allRows().size());*/
       

        JsonDocument jsonDocument = bucket.get(clientId);
        if(jsonDocument!=null){
        	 System.out.println(bucket.get(clientId));
        	 version =(String) jsonDocument.content().get("aec");
        }
        System.out.println("version===>"+version);
        
        
        if(version!=null){
        	System.out.println("true");
        	return true;
        }else{
        	System.out.println("false");
        	return false;
        }
        
    }



    public static String getWallets(String market) {

        final String METHOD_NAME = "getWallets()";
        String rows = " ";

        //Prints Content and Metadata of the stored Document
        System.out.println(bucket.get("u:AU_wallet"));
        System.out.println(bucket.get("u:US_wallet"));

        System.out.println("Now querying for:"+market);
        //Perform a N1QL Query
        String E1_Query = "SELECT wallets FROM " + bucket.name() + " WHERE market = '" + market + "'";

        N1qlQueryResult result = bucket.query(
                N1qlQuery.parameterized(E1_Query,
                        JsonArray.from(market))
        );
        System.out.println("row size:"+result.allRows().size());
        //List<Wallet> walletList = new ArrayList<Wallet>(2);
        //Print each found Row
        for (N1qlQueryRow row : result) {
            //
            System.out.println("row details: "+row);
            rows += row.toString();
        }
        
        return rows;
        //cluster.disconnect();
        //LogUtil.logExit(CLASS_NAME, METHOD_NAME);
    }
/*
    public static void insertEvent(Event event) {

        JsonObject eventObj = JsonObject.create()
                .put("eventName", event.getEventName())
                .put("merchantId", event.getMerchantId())
                .put("conversationId", event.getConversationId());

        System.out.println(" eventObj " + eventObj.toString());

        bucket.upsert(JsonDocument.create(event.getEventId(), eventObj));

    }
    
    public static Boolean insertToken(Event event) {

        JsonObject eventObj = JsonObject.create()
                .put("eventName", event.getEventName())
                .put("merchantId", event.getMerchantId())
                .put("conversationId", event.getConversationId());

        System.out.println(" eventObj " + eventObj.toString());
        try{
        	bucket.upsert(JsonDocument.create(event.getEventId(), eventObj));
        	
        }catch(Exception e){
        	return false;
        }
        return true;
    }
    public static String getToken(String eventId) {
        return bucket.get(eventId) != null ? bucket.get(eventId).toString() : null;

    }
    public static String getEvent(String eventId) {
        return bucket.get(eventId) != null ? bucket.get(eventId).toString() : null;

    }
    
    
    public static Boolean addSignUpDetails(CMDetails cmDetails){    
    	
    	System.out.println("cmDetails.getJwtToken()"+cmDetails.getJwtToken());
    	JsonObject ShipingAddress = null;
    	    	
    	JsonObject signUpCardDetails = JsonObject.create();
    	JsonObject signUpBillingAddress = JsonObject.create();
    	JsonObject signUpDetails = JsonObject.create()
    			.put("jwtToken", cmDetails.getJwtToken())    			
    			.put("clientId", cmDetails.getClientId());
    	
    	signUpCardDetails.put("cm15", cmDetails.getCm15())
    			.put("defaultCardFlag", cmDetails.getDefaultCardFlag())
    			.put("expiryMonth", cmDetails.getExpiryMonth())
    			.put("expiryYear", cmDetails.getExpiryYear())
    			.put("nameOnCard", cmDetails.getNameOnCard())    			
    			.put("cVV", cmDetails.getcVV())
    			.put("firstName", cmDetails.getFirstName())
    			.put("lastName", cmDetails.getLastName())
    			.put("cardNickName", cmDetails.getCardNickName());
    	
    	signUpBillingAddress.put("country", cmDetails.getCountry())
    			.put("addressLine1", cmDetails.getAddressLine1())
    			.put("addressLine2", cmDetails.getAddressLine2())
    			.put("city", cmDetails.getCity())
    			.put("stateOrTerritory", cmDetails.getStateOrTerritory())    			
    			.put("PostalCode", cmDetails.getPostalCode())
    			.put("sameAsBillingAddress", cmDetails.getSameAsBillingAddress());
    	
    	signUpDetails.put("cardInformation", signUpCardDetails)
    	             .put("billingAddress", signUpBillingAddress);
    			
    			if("Y".equalsIgnoreCase(cmDetails.getSameAsBillingAddress())){
    				
    				ShipingAddress = JsonObject.create();
    				ShipingAddress.put("defaultAddressFlag", "Y")
    				.put("recipientCountry", cmDetails.getCountry())    				
    				.put("recipientFirstName", cmDetails.getFirstName())
    				.put("recipientLastName", cmDetails.getLastName())
    				.put("recipientAddressLine1", cmDetails.getAddressLine1())
    				.put("recipientAddressLine2", cmDetails.getAddressLine2())
    				.put("recipientCity", cmDetails.getCity())
    				.put("recipientStateOrTerritory", cmDetails.getStateOrTerritory())    				
    				.put("recipientPostalCode", cmDetails.getPostalCode())
    				.put("recipientPhoneNumber", "");
    				signUpDetails.put("shippingAddress", ShipingAddress);
    			}
    			
    			System.out.println("signUpDetails"+signUpDetails);
    	try{
    	JsonDocument cmInfoDoc = JsonDocument.create(cmDetails.getJwtToken(), signUpDetails);
    	
    	System.out.println("to update @@ "+signUpDetails);
    	
    	bucket.upsert(cmInfoDoc);
    	
    	}catch(Exception e){
    		System.out.println("e++++++++++>"+e);
    		e.printStackTrace();
    		return false;
    	}
    	   JsonDocument jsonDocument = bucket.get(cmDetails.getJwtToken());
   	   
    	
    	System.out.println("Address update to DB is successful===>"+jsonDocument.content());
    	
    	
    	return true;
    }
    
    
  public static Boolean insertSessionData(CMDetails cmDetails){    
    	
    	System.out.println("cmDetails.getJwtToken()"+cmDetails.getJwtToken());
    	
    	    	
    	JsonObject cardDetails = JsonObject.create();
    	JsonObject billingAddress = JsonObject.create();
    	JsonObject sessionData = JsonObject.create().put("jwtToken", cmDetails.getJwtToken())
    			.put("clientId", cmDetails.getClientId());
    	
    	cardDetails.put("cm15", cmDetails.getCm15())
    			.put("defaultCardFlag", cmDetails.getDefaultCardFlag())
    			.put("expiryMonth", cmDetails.getExpiryMonth())
    			.put("expiryYear", cmDetails.getExpiryYear())
    			.put("nameOnCard", cmDetails.getNameOnCard())    			
    			.put("cVV", cmDetails.getcVV())
    			.put("firstName", cmDetails.getFirstName())
    			.put("lastName", cmDetails.getLastName())
    			.put("cardNickName", cmDetails.getCardNickName());
    	
    	billingAddress.put("country", cmDetails.getCountry())
    			.put("addressLine1", cmDetails.getAddressLine1())
    			.put("addressLine2", cmDetails.getAddressLine2())
    			.put("city", cmDetails.getCity())
    			.put("stateOrTerritory", cmDetails.getStateOrTerritory())    			
    			.put("PostalCode", cmDetails.getPostalCode())
    			.put("sameAsBillingAddress", cmDetails.getSameAsBillingAddress());
    	
    	sessionData.put("cardInformation", cardDetails)
    	             .put("billingAddress", billingAddress);
    			
    			
    			
    			System.out.println("signUpDetails"+sessionData);
    	try{
    	JsonDocument cmInfoDoc = JsonDocument.create(cmDetails.getJwtToken(), sessionData);
    	
    	System.out.println("to update @@ "+sessionData);
    	
    	bucket.upsert(cmInfoDoc);
    	
    	}catch(Exception e){
    		System.out.println("e++++++++++>"+e);
    		e.printStackTrace();
    		return false;
    	}
    	   JsonDocument jsonDocument = bucket.get(cmDetails.getJwtToken());
   	   
    	
    	System.out.println("Address update to DB is successful===>"+jsonDocument.content());
    	
    	
    	return true;
    }
  
  public static Boolean insertTokenData(MerchantDetails merchantDetails) {

      JsonObject eventObj = JsonObject.create()
              .put("jwtToken", merchantDetails.getJwtToken())
              .put("market", merchantDetails.getMarket())
              .put("redirectURL", merchantDetails.getRedirectURL())
              .put("locale",merchantDetails.getLocale())
              .put("encryptedData",merchantDetails.getEncryptedData());

      System.out.println(" eventObj " + eventObj.toString());
      try{
      	bucket.upsert(JsonDocument.create(merchantDetails.getJwtToken(), eventObj));
      	
      	
      }catch(Exception e){
      	return false;
      }
      JsonDocument jsonDocument = bucket.get(merchantDetails.getJwtToken());
	   
   	
   	System.out.println("Token update to DB is successful===>"+jsonDocument.content());
    	
      return true;
  }*/
    
}
