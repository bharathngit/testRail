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
	public static String configName="Web Browsers";
	
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
	public static List<Long> failedCases= new ArrayList<Long>();
	public static Map<Long, String> failedCASE=new HashMap<Long, String>();
	public static Map<Long,Long> suiteIdFailedCASE=new HashMap<Long,Long>();
	public static Map<Long, List> suiteIDsfailedCASE=new HashMap<Long, List>();
	public static Map<Long, Long> uniqSuiteIDs=new HashMap<Long, Long>();
	public static List<Long>suiteIds=new ArrayList<Long>(); 
	public static Map<Long, String> suiteRuns=new HashMap<Long, String>();
//	public static List<Long> browserList=new ArrayList<Long>();
	public static Long[] browserList=new Long[1];
	
	
	@SuppressWarnings("unchecked")
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
			Long suiteID=0L;
			//Get TESTPLAN object
			jsonObj= (JSONObject)client.sendGet("get_plan/"+planID);
//			System.out.println("TESTPLAN Array:"+jsonObj);
			
			//Get ENTRIES array
			jsonArr=(JSONArray)jsonObj.get("entries");
//			System.out.println("Entries Array size: "+jsonArr.size());
			//Iterate ENTRIES array to get RUNS array
				for(int i=0;i<jsonArr.size();i++){
					jsonObj=(JSONObject)jsonArr.get(i);
					runName=jsonObj.get("name").toString();//Get TESTRUN Name
					suiteID=(Long)jsonObj.get("suite_id");//Get Suite ID
					testRunNames.add(runName);
					suiteIds.add(suiteID);
//					browserList.add(1);
					suiteRuns.put(suiteID,runName);
					
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
			System.out.println("RunID : Test Run Name");
			System.out.println("===================================");
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
				if(jsonObj.get("name").toString().equalsIgnoreCase(configName)){
					configID=(Long) jsonObj.get("id");
				}
				
				JSONArray configsArr=(JSONArray)jsonObj.get("configs");
//				System.out.println("Configs"+configsArr);
				//Browser ID
				for(int j=0;j<configsArr.size();j++){
					
					jsonObj=(JSONObject)configsArr.get(j);
					
					if((jsonObj.get("name").toString()).equalsIgnoreCase(browserName)){
						browserID=(Long)jsonObj.get("id");
					}
				}
//						
			}
			
			System.out.println("configID of "+configName+": "+configID);
			System.out.println("browserID of "+browserName+" : "+browserID);
			
		} catch (IOException | APIException e) {
			e.printStackTrace();
		}
		
		
		
		/*FAILED TESTS INFO : get_results_for_run/:run_id and return Test IDs of Failed tests
				Status Codes:
					==============================
					RETEST=4
					BLOCKED=2
					PASSED=1
					FAILED=5
					*/
	
	 	try {
	 		System.out.println("========Get FAILED TESTS INFO (status_id and test_id OF A TESTRUN=====");
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
	 		System.out.println("========Get CASEID AND TITLE OF THE FAILED TESTS OF A TESTRUN=====");
	 		for(int i=0;i<failedTests.size();i++){
	 			jsonObj= (JSONObject) client.sendGet("get_test/"+failedTests.get(i));
//	 			System.out.println("TEST info arrays: "+jsonObj.toString());
				
					Long caseId=(Long)jsonObj.get("case_id"); //For re-adding test cases to Re-run testruns
					String title=jsonObj.get("title").toString();
					
					failedCases.add(caseId);
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
	
	 	//GET SUITE ID FOR FAILED CASES and ADD case_ids(ArrayList) and suite_ids(Key) into Hashmap
	 	try{
	 		System.out.println("========GET SUITE ID FOR FAILED CASES and ADD case_ids(ArrayList) and suite_ids(Key) into Hashmap=====");

//	 		List<Long> csID = new ArrayList<Long>();
	 		Long suiteId = null;
	 		
	 		for(int i=0;i<failedCases.size();i++){
 			jsonObj= (JSONObject) client.sendGet("get_case/"+failedCases.get(i));
 			
 			//csID.add(failedCases.get(i));
 			suiteId=(Long)jsonObj.get("suite_id");
 			suiteIdFailedCASE.put(failedCases.get(i),suiteId);
 			}
	 		
	 		
	 		
//	 		TODO Create HashMap of Suite_IDs and corresponding failed Case_Ids Arraylist
	 		List<Long> cIds=new ArrayList<Long>();
	 		for(Long sId:suiteIds){
//	 			??TODO  read values from Hasmap suiteIdFailedCASE
	 			for (Map.Entry<Long,Long> entry : suiteIdFailedCASE.entrySet()) {
		 			if(sId.equals(entry.getValue())){
		 				cIds.add(entry.getKey());
		 			}
		 		}
	 			suiteIDsfailedCASE.put(sId, cIds);
	 		}
	 		System.out.println("Suite IDs and Failed Cases List :");
	 		System.out.println("SuiteID  :  Cases list");
	 		for (Map.Entry<Long,List> entry : suiteIDsfailedCASE.entrySet()) {
	 			System.out.println(entry.getKey()+" : "+entry.getValue());
	 		}
	 	}catch (IOException | APIException e) {
			e.printStackTrace();
		}
	 	
	 	
	 	//Create TEST RUNs in a TEST PLAN for Failed Test Runs
	 	
	 	
		try {
			System.out.println("========CREATING TEST RUN AND ADDING FAILED TESTS==============");
	 	@SuppressWarnings("rawtypes")
		Map postData=new HashMap();
//	 	browserList[0]=(long) 6;
		System.out.println("Creating a New Test Run for Failes tests..");
		//Iterate suiteIdFailedCASE HashMap and through Add Request fields to the Hash Map
		for (Map.Entry<Long, List> entry : suiteIDsfailedCASE.entrySet()){
			System.out.println("Creating Data HashMap for SuiteID:"+entry.getKey());
			postData.put("name", "Re-RunFailedTests_"+entry.getKey());//New testrun name
			postData.put("suite_id", entry.getKey());//TODO sendGet(get_case/:case_id) to get suite_id
//			postData.put("config_ids", browserList);//browser name TODO change to config_ids array
			postData.put("case_ids", entry.getValue());//Failed Cases' Case_IDs
			postData.put("include_all", false);
//			Posting the info to Test Rail
			System.out.println("Creating Test Run Re-RunFailedTests_"+entry.getKey()+"for SuiteID:"+entry.getKey());
			client.sendPost("add_plan_entry/"+planID, postData);
			}

			
			System.out.println("Created Testrun(s) successfully..");
		} catch (IOException | APIException e) {
			// TODO Auto-generated catch block
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
