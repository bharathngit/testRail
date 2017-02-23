package com.automation.testrail;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.JSONException;
import org.json.simple.JSONObject;

public class ReadfromTestRail {
	//Initialize variables
	public static String baseUrl="https://internationalsos.testrail.net";
	public static String uName="bharath.nadukatla@gallop.net";
	public static String pwD="pqUkNXLigTp63CBxmGfx-WUvg8MVPQZ7UcwO2cFuh";
	public static String projectName="Sandbox";
	public static String milestoneName="Sandbox_1.0";
	public static String subMilestoneName="SubSandbox_1.0";
	public static String testPlanName="Sandbox_1.0_TestPlan";
	public static String testRunName="Sandbox_1.0_TestSuite_1";
	public static String browserName="IE";
	
	
	//Declare variables
	public static JSONArray jsonArr;
	public static JSONObject jsonObj;
	public static Long milestoneID;
	public static Long projectID;
	public static Long submilestoneID;
	public static Long planID;
	public static Long configID;
	public static Long browserID;
	public static List<Long> testRunID=new ArrayList<Long>();
	public static List<String> testRunNames=new ArrayList<String>();
	public static Map<Long, String> testRuns=new HashMap<Long, String>();
	public static List<Long> failedTests= new ArrayList<Long>();
	public static Map<Long, String> failedCASE=new HashMap<Long, String>();
	
	
	
	
	public static void main(String[] args) {
	
		APIClient client=new APIClient(baseUrl);
	 	client.setUser(uName);
	 	client.setPassword(pwD);	
		
		//Get Project ID - Access TestRail API get_projects with Parameter is_completed=0 for Active projects
		try {
			System.out.println("========GETTING PROJECT ID=====");
			projectID=getID("get_projects&is_completed=0",projectName);
			System.out.println("Project ID of "+projectName+": "+projectID);
		} catch (JSONException|IOException | APIException e) {
			e.printStackTrace();
		} 
		
		//Get Milestone ID- get_milestones/:project_id and get_milestone/:milestone_id
		try {
			System.out.println("========GETTING MILESTONE ID=====");
			milestoneID=getID("get_milestones/"+projectID,milestoneName);
			System.out.println("Milestone ID of "+milestoneName+": "+milestoneID);
		} catch (JSONException|IOException | APIException e) {
			e.printStackTrace();
		}
		
		//Get Sub-Milestones IDs
		try {
			System.out.println("========GETTING SUB-MILESTONE ID=====");
			jsonObj=(JSONObject)client.sendGet("get_milestone/"+milestoneID);
			
			jsonArr=(JSONArray)jsonObj.get("milestones");
			
			for(int i=0;i<jsonArr.size();i++){
				jsonObj=(JSONObject)jsonArr.get(i);
				if(subMilestoneName.equalsIgnoreCase(jsonObj.get("name").toString())){
					submilestoneID=(Long) jsonObj.get("id");
					}
			}
			System.out.println("Sub Milestone ID of "+subMilestoneName+": "+submilestoneID);
		} catch (IOException | APIException e) {
			e.printStackTrace();
		}
		
		
		
		//Get TESTPLAN ID for project "get_plans/"+projectID
		try {
			System.out.println("========GETTING TESTPLAN ID=====");
			planID=getID("get_plans/"+projectID,testPlanName);
			System.out.println("TestPlan ID of "+testPlanName+": "+planID);
		} catch (JSONException|IOException | APIException e) {
			e.printStackTrace();
		} 
		
		//GET TESTRUNS(RUN IDs - NAMES) within a TESTPLAN : get_plan/:plan_id
		try {
			System.out.println("========GET TESTRUNS(RUN IDs - NAMES) within a TESTPLAN=====");
			Long runID = 0L;
			String runName;
			
			//Get TESTPLAN object
			jsonObj= (JSONObject)client.sendGet("get_plan/"+planID);
//			System.out.println("TESTPLAN Array:"+jsonObj);
			
			//Get ENTRIES array
			jsonArr=(JSONArray)jsonObj.get("entries");
//			System.out.println("Entries Array size: "+jsonArr.size());
			//Iterate ENTRIES array to get RUNS array
				for(int i=0;i<jsonArr.size();i++){
//					System.out.println("Inside entries for loop");
					jsonObj=(JSONObject)jsonArr.get(i);
					runName=jsonObj.get("name").toString();//Get TESTRUN Name
					testRunNames.add(runName);
					//Get RUNS array
					JSONArray jsonArrRuns=(JSONArray)jsonObj.get("runs");
//					System.out.println("RUNS Array size "+jsonArrRuns.size());
					for(int j=0;j<jsonArrRuns.size();j++){
						JSONObject jsonObjRuns=(JSONObject) jsonArrRuns.get(j);
						runID=(Long)jsonObjRuns.get("id");
						testRunID.add(runID);//Get RUN ID for the Test Run
					}
					testRuns.put(runID, runName);
				}
			System.out.println("Test Runs in the TestPlan "+testPlanName+" are:");
			for (Map.Entry<Long, String> entry : testRuns.entrySet()) {
				System.out.println(entry.getKey()+" : "+entry.getValue());
	 		}
		
		} catch (IOException | APIException e2) {
			e2.printStackTrace();
		}
	
		
		
		/*//Get TESTRUNS for Project : get_runs/:project_id
		try {
			testRunID=getID("get_runs/"+projectID, testRunName);
			System.out.println("TestRUN ID of "+testRunName+": "+testRunID);
		} catch (IOException | APIException | JSONException e1) {
			e1.printStackTrace();
		}*/
		
		//Get CONFIGURATION INFO - Config ID and Browser ID - on TESTPLAN -- get_configs/:project_id
		try {
			System.out.println("========Get CONFIGURATION INFO - Config ID and Browser ID - on TESTPLAN =====");
		 	jsonArr=(JSONArray)client.sendGet("get_configs/"+projectID);

			//Config ID for Web Browsers
			for(int i=0;i<jsonArr.size();i++){
				
//				System.out.println("Array: "+jsonArr.get(i).toString());
				
				jsonObj=(JSONObject)jsonArr.get(i);
				if(jsonObj.get("name").toString().equalsIgnoreCase("Web Browsers")){
					configID=(Long) jsonObj.get("id");
				}
				
				JSONArray configsArr=(JSONArray)jsonObj.get("configs");
//				System.out.println("Configs"+configsArr);
				//Browser ID
				for(int j=0;j<configsArr.size();j++){
					
					jsonObj=(JSONObject)configsArr.get(j);
					
					if((jsonObj.get("name").toString()).equalsIgnoreCase(browserName)){//Need parameterization for browser
						browserID=(Long)jsonObj.get("id");
					}
				}
//						
			}
			
			System.out.println("configID of Web Browsers: "+configID);
			System.out.println("browserID of "+browserName+" is "+browserID);
			
		} catch (IOException | APIException e) {
			e.printStackTrace();
		}
		
		
		
		/*FAILED TESTS INFO : get_results_for_run/:run_id and return Test IDs of Failed tests
				Status Codes:
					==============================
					RETEST=4
					BLOCKED=2
					PASSED=1
					FAILED=5*/
	
	 	try {
	 		System.out.println("========Get FAILED TESTS INFO OF A TESTRUN=====");
	 		for(Long runId:testRunID){
	 		jsonArr= (JSONArray)client.sendGet("get_results_for_run/"+runId);
//			System.out.println(jsonArr.toString());
			
			for(int i=0;i<jsonArr.size();i++){
				jsonObj=(JSONObject)jsonArr.get(i);
				Long stId=(Long)jsonObj.get("status_id");
				if(stId==5){//Looking for Failed tests with status_id==5
					Long tId=(Long)jsonObj.get("test_id");
					//Add to HashMap
					failedTests.add(tId);
					}
				}
	 		}
	 		System.out.println("Failed Tests' TestIDs are :"+failedTests.toString());	
		} catch (IOException | APIException e) {
			e.printStackTrace();
		}
		
		//GET TEST-CASES INFO - get_test/:test_id getting failed cases -CASEID AND TITLE -get_test returns JSONObject
	 	try {
	 		System.out.println("========Get CASEID AND TITLE OF THE FAILED TESTS INFO OF A TESTRUN=====");
	 		for(int i=0;i<failedTests.size();i++){
	 			jsonObj= (JSONObject) client.sendGet("get_test/"+failedTests.get(i));
//	 			System.out.println("TEST info arrays: "+jsonObj.toString());
				
					Long caseId=(Long)jsonObj.get("case_id"); //For re-adding test cases to Re-run testruns
					String title=jsonObj.get("title").toString();
					
					failedCASE.put(caseId, title);
					
				}
	 		System.out.println("No. of Failed Tests: "+failedCASE.size());
	 		
	 		//Printing Hashmap
	 		System.out.println("CaseID  :  Title");
	 		for (Map.Entry<Long, String> entry : failedCASE.entrySet()) {
	 			
	 			System.out.println(entry.getKey()+" : "+entry.getValue());
	 		}
	 		} catch (IOException | APIException e) {
			e.printStackTrace();
		}
	 	
		
	}

	public static Long getID(String attribute, String attName) throws MalformedURLException, IOException, APIException, JSONException {
		Long id = null;
		
		 	APIClient client=new APIClient(baseUrl);
		 	client.setUser(uName);
		 	client.setPassword(pwD);
 
		 jsonArr= (JSONArray)client.sendGet(attribute);
		 
		 System.out.println("No. of items: "+(jsonArr.size()));
		 
		 for(int i=0;i<jsonArr.size();i++){
//			 System.out.println("inside for loop");
			 jsonObj=(JSONObject)jsonArr.get(i);
			 if((jsonObj.get("name").toString().equalsIgnoreCase(attName))){
				 id=(Long)jsonObj.get("id");
				 break;
			 }
		 }
		 
		return id;
		
	}

}
