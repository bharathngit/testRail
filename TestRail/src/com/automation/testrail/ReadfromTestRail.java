package com.automation.testrail;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
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
	public static String milestoneName="Sandbox_1.2";
	public static String subMilestoneName="SubSandbox_1.2";
	public static String testPlanName="Sandbox_1.2_TestPlan";
	public static String testRunName="Sandbox_1.0_TestSuite_1";//Actually is the TestSuite Name
	public static String[] testRunsNames={testRunName+"_Node_1",testRunName+"_Node_2",
										testRunName+"_Node_3",testRunName+"_Node_4"};//List<String> runslist = Arrays.asList(testRunsNames);
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
	public static List<Long> testRunIDs=new ArrayList<Long>();
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
	public static List<Long> browserList=new ArrayList<Long>();
	public static Map<String, List> testCasesMap= new HashMap();
	
	public static Long testSuiteId;
	public static Long indexOfAutomationCompleteStatus=5L;
	public static List<Long> testCaseIds=new ArrayList<Long>();
	public static Long testRunID;
	
	/*public ReadfromTestRail() {
		APIClient client=new APIClient(baseUrl);
	 	client.setUser(uName);
	 	client.setPassword(pwD);
	}*/
	
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
		
		//Get or Create  Milestone ID- get_milestones/:project_id and get_milestone/:milestone_id
		try {
			System.out.println("========GETTING MILESTONE ID=====");
			milestoneID=getID("get_milestones/"+projectID,milestoneName);
			
			if(milestoneID==null){
				//Create the Milestone
								
				System.out.println(milestoneName+" doesn't exist, now creating a new one..");
				Map msData=new HashMap();
				msData.put("name", milestoneName);
				
				client.sendPost("add_milestone/"+projectID, msData);
				
				milestoneID=getID("get_milestones/"+projectID,milestoneName);
				System.out.println("Milestone ID of "+milestoneName+": "+milestoneID);
				}else{
				System.out.println("Milestone ID of "+milestoneName+": "+milestoneID);
			}
		} catch (JSONException|IOException | APIException e) {
			e.printStackTrace();
		}
		
		//Get or Create  Sub-Milestones IDs
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
			
			if(submilestoneID==null){
				//Create the sub Milestone
			
				System.out.println(subMilestoneName+" doesn't exist, now creating a new one..");

				Map msData=new HashMap();
				msData.put("name", subMilestoneName);
				msData.put("parent_id", milestoneID);
				
				client.sendPost("add_milestone/"+projectID, msData);
				
				//Get the new sub-milestone ID
				jsonObj=(JSONObject)client.sendGet("get_milestone/"+milestoneID);
				
				jsonArr=(JSONArray)jsonObj.get("milestones");
				
				for(int i=0;i<jsonArr.size();i++){
					jsonObj=(JSONObject)jsonArr.get(i);
					if(subMilestoneName.equalsIgnoreCase(jsonObj.get("name").toString())){
						submilestoneID=(Long) jsonObj.get("id");
						}
					}
				System.out.println("Sub Milestone ID of "+subMilestoneName+": "+submilestoneID);
				}else{
					System.out.println("Sub Milestone ID of "+subMilestoneName+": "+submilestoneID);
					}
		} catch (IOException | APIException e) {
			e.printStackTrace();
		}
		
		
		
		//Get or Create TESTPLAN ID for project "get_plans/"+projectID
		try {
			System.out.println("========GETTING TESTPLAN ID=====");
			planID=getID("get_plans/"+projectID,testPlanName);
			
			if(planID==null){
				//Create the Testplan
				
				System.out.println(testPlanName+" doesn't exist, now creating a new one..");

				//Creates a Testplan under a milestone/sub-milestone and get the new Plan ID
				Map planData=new HashMap();
				planData.put("name", testPlanName);
				planData.put("milestone_id", submilestoneID);
				client.sendPost("add_plan/"+projectID, planData);
				planID=getID("get_plans/"+projectID,testPlanName);
				System.out.println("TestPlan ID of "+testPlanName+": "+planID);
			}else{
				System.out.println("TestPlan ID of "+testPlanName+": "+planID);
			}
		} catch (JSONException|IOException | APIException e) {
			e.printStackTrace();
		} 
		
		//GET TESTSUITE INFO - suite_id
		 try {
				System.out.println("========GET TESTSUITE INFO - suite_id=====");

			jsonArr = (JSONArray) client.sendGet("get_suites/" + projectID);
			 for (int i = 0; i < (jsonArr.size()); i++) {
			        jsonObj = (JSONObject) jsonArr.get(i);
			        if (jsonObj.get("name").equals(testRunName)) {//testrunName=testSuiteName
			        	testSuiteId = (Long) jsonObj.get("id");
			        }
			 	}
			 System.out.println("Suite_id of "+testRunName+" is "+testSuiteId);
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (APIException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		/*//Creating NEW TESTRUNS based on TESTSUITE if they dont exist in the testplan
		 *
		 * Add the testcases with custom_automationstatus==5, of the aforementioned TestSuite to the new test run
		 * suite_id	int	The ID of the test suite for the test run (optional if the project is operating in single suite mode, required otherwise)
		 * name	string	The name of the test run
		 * milestone_id	int	The ID of the milestone to link to the test run
		 * include_all	bool	(TO be sent FALSE)True for including all test cases of the test suite and false for a custom case selection (default: true)
		 * case_ids	array	An array of case IDs for the custom case selection
		 */		
			 
		 
		 
		/* try {
				System.out.println("========Creating NEW TESTRUNS based on TESTSUITE if they dont exist in the testplan=====");
				
				//Check whether the test run exists already
				
				//Get TESTPLAN object
				jsonObj= (JSONObject)client.sendGet("get_plan/"+planID);
				
				//Get ENTRIES array
				jsonArr=(JSONArray)jsonObj.get("entries");

				//Iterate ENTRIES array to get RUNS array
					for(int i=0;i<jsonArr.size();i++){
						jsonObj=(JSONObject)jsonArr.get(i);
						
						if((jsonObj.get("name").toString()).equalsIgnoreCase(testRunName)){
						
						//Get RUNS array
						JSONArray jsonArrRuns=(JSONArray)jsonObj.get("runs");

						for(int j=0;j<jsonArrRuns.size();j++){
							JSONObject jsonObjRuns=(JSONObject) jsonArrRuns.get(j);
							testRunIDs.add((Long)jsonObjRuns.get("id"));//Get RUN ID for the Test Run
							}
						}
					}
				//If there is no TestRUN the Create New Ones
				if(testRunIDs.isEmpty()){
				
					System.out.println("TestRUN doesn't exist, creating new one..");
					//Creating a new test run and adding cases from testsuite with autoncomplete status
					jsonArr = (JSONArray) client.sendGet("get_cases/" + projectID
					         + "&suite_id=" + testSuiteId);
					System.out.println("Getting cases with Automation status=5 from the TestSuite");
	
					for (int j = 0; j < jsonArr.size(); j++) {
					   jsonObj= (JSONObject) jsonArr.get(j);
					   if ((Long) jsonObj.get("custom_automationstatus") == indexOfAutomationCompleteStatus) {
						   //Get caseids of all cases with automation complete status 
						   testCaseIds.add((Long) jsonObj.get("id"));
					   }
					}	
				   System.out.println("Total cases in "+testRunName+" those in Automation Complete status are: "+testCaseIds.size());
				  
				   //Initializing configurations to IE,Chrome and FF
				   browserList.add(3l);
				   browserList.add(5l);
				   browserList.add(6l);
				  
				   Map chromeMap = new HashMap();//1 map for each browser
				   Map firefoxMap = new HashMap();
				   Map iEMap = new HashMap();
				   
				   List<Long> chrome=new ArrayList<Long>();
				   List<Long> ff=new ArrayList<Long>();
				   List<Long> ie=new ArrayList<Long>();
				   
				   chrome.add(6l);
				   ff.add(5l);
				   ie.add(3l);
				   
				   chromeMap.put("config_ids",chrome);//should be [1,3][1,5][1,6]
				   firefoxMap.put("config_ids",ff);
				   iEMap.put("config_ids",ie);
				   
				   List runsList=new ArrayList();
				   runsList.add(chromeMap);
				   runsList.add(firefoxMap);
				   runsList.add(iEMap);
				   for(Object run:runsList){
					   System.out.println("Runs List"+run);   
				   }
				   
	
				   Map data = new HashMap();
				   data.put("name",testRunName);
				   data.put("suite_id", testSuiteId);
				   data.put("milestone_id", submilestoneID);
				   data.put("include_all", false);
				   data.put("case_ids", testCaseIds);
				   data.put("config_ids", browserList);  //To set Configurations to the Test Runs - to IE
				   data.put("runs", runsList);//Include values for each Test Run in the runsList
				   
				   client.sendPost("add_plan_entry/"+ planID, data);
				
				   System.out.println("Created a Test Run "+testRunName+" under TestPlan "+testPlanName+" and added no. of tests:"+testCaseIds.size());
					
				   //Obtain RunID of the newly created TestRUN
				   String runName;
					Long runID = 0L;
					//Get TESTPLAN object
					jsonObj= (JSONObject)client.sendGet("get_plan/"+planID);
					
					//Get ENTRIES array
					jsonArr=(JSONArray)jsonObj.get("entries");
	
					//Iterate ENTRIES array to get RUNS array
						for(int i=0;i<jsonArr.size();i++){
							jsonObj=(JSONObject)jsonArr.get(i);
							runName=jsonObj.get("name").toString();//Get TESTRUN Name
							if(runName.equalsIgnoreCase(testRunName)){
							
							//Get RUNS array
							JSONArray jsonArrRuns=(JSONArray)jsonObj.get("runs");
	
							for(int j=0;j<jsonArrRuns.size();j++){
								JSONObject jsonObjRuns=(JSONObject) jsonArrRuns.get(j);
								runID=(Long)jsonObjRuns.get("id");
								System.out.println("RunID: "+runID);
							}
							}
						}
				}else{
					System.out.println("Test RUN ID(s):");
					for(Long id: testRunIDs){
						System.out.println(id);
					}
				}

		 }catch (IOException | APIException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}*/
					
	/*//CREATE TESTRUNS(4) AND GET THE RUN IDS
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 	
*/		 try {
				System.out.println("========Creating NEW TESTRUNS, if they dont ALREADY exist in the testplan=====");
				
			//Check whether the test run exists already
				
				//Get TESTPLAN object
				jsonObj= (JSONObject)client.sendGet("get_plan/"+planID);
				
				//Get ENTRIES array
				jsonArr=(JSONArray)jsonObj.get("entries");
				
				//FOR EACH TESTRUN NAME - SEARCH JSONARRAY FOR ITS PRESENCE
				List<String> runslist = Arrays.asList(testRunsNames);
				for(String runName:runslist){
				//Iterate ENTRIES array to get RUNS array
					for(int i=0;i<jsonArr.size();i++){
						jsonObj=(JSONObject)jsonArr.get(i);
						
						if((jsonObj.get("name").toString()).equalsIgnoreCase(runName)){
						
						//Get RUNS array
						JSONArray jsonArrRuns=(JSONArray)jsonObj.get("runs");

						for(int j=0;j<jsonArrRuns.size();j++){
							JSONObject jsonObjRuns=(JSONObject) jsonArrRuns.get(j);
							testRunIDs.add((Long)jsonObjRuns.get("id"));//Get RUN ID for the Test Run and ADD to List
							}
						}
					}
				}
			//If there is no TestRUN the Create New Ones
				if(testRunIDs.isEmpty()){
				
					System.out.println("TestRUN doesn't exist, creating new one..");
					//Get Cases from test suite with AutomationStatus==5
					jsonArr = (JSONArray) client.sendGet("get_cases/" + projectID
					         + "&suite_id=" + testSuiteId);
					System.out.println("Getting cases with Automation status=5 from the TestSuite");
	
					for (int j = 0; j < jsonArr.size(); j++) {
					   jsonObj= (JSONObject) jsonArr.get(j);
					   if ((Long) jsonObj.get("custom_automationstatus") == indexOfAutomationCompleteStatus) {
						   //Get caseids of all cases with automation complete status 
						   testCaseIds.add((Long) jsonObj.get("id"));
					   }
					}	
				   System.out.println("Total cases in "+testRunName+" those in Automation Complete status are: "+testCaseIds.size());
				   
				   //Divide the testCaseIds into 4 different sublists and add it to HashMap<RunNAme,CaseList>
				   List<Long> s1=new ArrayList<Long>();
				   List<Long> s2=new ArrayList<Long>();
				   List<Long> s3=new ArrayList<Long>();
				   List<Long> s4=new ArrayList<Long>();
				   
				   /*for(int i = 0 ; i < testCaseIds.size(); i+=4){
					   if(testCaseIds.size()-i >= 4){
						  s1.add(testCaseIds.get(i));
						  s2.add(testCaseIds.get(i+1));
						  s3.add(testCaseIds.get(i+2));
						  s4.add(testCaseIds.get(i+3));
						  }else if(testCaseIds.size()-i == 3){
							   s1.add(testCaseIds.get(i));
							   s2.add(testCaseIds.get(i+1));
							   s3.add(testCaseIds.get(i+2));
						  }else if(testCaseIds.size()-i == 2){
							   s1.add(testCaseIds.get(i));
							   s2.add(testCaseIds.get(i+1));
						  }else if(testCaseIds.size()-i == 1){
						  s1.add(testCaseIds.get(i));
						  }
					   
					  }*/
				   //OR
				   int chunk = testCaseIds.size()/4;
				   
				   if(testCaseIds.size() % 4 == 0 ){
				    
				    int temp = chunk;
				    
				    s1.addAll(testCaseIds.subList(0, temp)); // 0 - 10
				    s2.addAll(testCaseIds.subList(temp, temp = temp+chunk)); // 10, 20
				    s3.addAll(testCaseIds.subList(temp, temp = temp+chunk)); // 20, 30
				    s4.addAll(testCaseIds.subList(temp, temp = temp+chunk)); // 30, 40
				    
				   }else {
				    
				    int remainder = testCaseIds.size() % 4;
				    int temp = chunk;
				    s1.addAll(testCaseIds.subList(0, temp)); // 0 - 10
				    s2.addAll(testCaseIds.subList(temp, temp = temp+chunk)); // 10, 20
				    s3.addAll(testCaseIds.subList(temp, temp = temp+chunk)); // 20, 30
				    s4.addAll(testCaseIds.subList(temp, temp = temp+chunk)); // 30, 40
				    
				    if(remainder == 1){
				     s4.add(testCaseIds.get(temp+remainder));
				    }else if(remainder == 2){
				     s4.add(testCaseIds.get(temp+remainder-2));
				     s4.add(testCaseIds.get(temp+remainder-1));
				    }else{
				     s4.add(testCaseIds.get(temp+remainder-3));
				     s4.add(testCaseIds.get(temp+remainder-2));
				     s4.add(testCaseIds.get(temp+remainder-1));
				    }
				   }
				   
				   //Adding cases lists to a list
				   List caseList=new ArrayList();
				   caseList.add(s1);
				   caseList.add(s2);
				   caseList.add(s3);
				   caseList.add(s4);
				   System.out.println("Cases Lists are: "+caseList.toString());
				   
				   //Add to runName and casesList to a Hashmap
				   
				   for(int i=0;i<4;i++){
					   testCasesMap.put(runslist.get(i),(List) caseList.get(i));
				   }
				   System.out.println("Test cases MAP"+testCasesMap.toString());
				   
				   //Iterate Each TestRUn and add Cases
				   
				   //Initializing configurations to FIREFOX only
				   browserList.add(3l);
				   browserList.add(5l);
				   browserList.add(6l);
				  
//				   Map chromeMap = new HashMap();//1 map for each browser
				   Map firefoxMap = new HashMap();
//				   Map iEMap = new HashMap();
				   
//				   List<Long> chrome=new ArrayList<Long>();
				   List<Long> ff=new ArrayList<Long>();
//				   List<Long> ie=new ArrayList<Long>();
				   
//				   chrome.add(6l);
				   ff.add(5l);
//				   ie.add(3l);
				   
//				   chromeMap.put("config_ids",chrome);//should be [1,3][1,5][1,6]
				   firefoxMap.put("config_ids",ff);
//				   iEMap.put("config_ids",ie);
				   
				   List runsList=new ArrayList();
//				   runsList.add(chromeMap);
				   runsList.add(firefoxMap);
//				   runsList.add(iEMap);
				   for(Object run:runsList){
					   System.out.println("Runs List"+run);   
				   }
				   
				   //Creating the Test Run and Add TestCases to it
				   Map data = new HashMap();
				   
				   for (Map.Entry entry : testCasesMap.entrySet()) {
				   data.put("name",entry.getKey());//TestRun Name
				   data.put("suite_id", testSuiteId);
				   data.put("milestone_id", submilestoneID);
				   data.put("include_all", false);
				   data.put("case_ids",entry.getValue()); //array list of CaseIDs to be added to the test run
				   data.put("config_ids", browserList);  //To set Configurations to the Test Runs - to IE
				   data.put("runs", runsList);//Include values for each Test Run in the runsList
				   
				   client.sendPost("add_plan_entry/"+ planID, data);
				   
				   System.out.println("Created a Test Run "+entry.getKey()+" under TestPlan "+testPlanName);
				   }
				   //Obtain RunID of the newly created TestRUN
				   String runName;
					Long runID = 0L;
					//Get TESTPLAN object
					jsonObj= (JSONObject)client.sendGet("get_plan/"+planID);
					
					//Get ENTRIES array
					jsonArr=(JSONArray)jsonObj.get("entries");
	
					//Iterate ENTRIES array to get RUNS array
						for(int i=0;i<jsonArr.size();i++){
							jsonObj=(JSONObject)jsonArr.get(i);
							runName=jsonObj.get("name").toString();//Get TESTRUN Name
							if(runName.equalsIgnoreCase(testRunName)){
							
							//Get RUNS array
							JSONArray jsonArrRuns=(JSONArray)jsonObj.get("runs");
	
							for(int j=0;j<jsonArrRuns.size();j++){
								JSONObject jsonObjRuns=(JSONObject) jsonArrRuns.get(j);
								runID=(Long)jsonObjRuns.get("id");
								System.out.println("RunID: "+runID);
							}
							}
						}
				}
				else{
					System.out.println("Test RUN ID(s):");
					for(Long id: testRunIDs){
						System.out.println(id);
					}
				}

		 }catch (IOException | APIException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		
	/*	//GET TESTRUNS(RUN IDs - NAMES) within a TESTPLAN : get_plan/:plan_id
		try {
			System.out.println("========GET TESTRUNS(RUN IDs - NAMES) within a TESTPLAN=====");
			
			String runName;
			Long suiteID=0L;
			Long runID = 0L;
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
						testRunIDs.add(runID);//Get RUN ID for the Test Run
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
		}*/
	
		
	/*	//Get CONFIGURATION INFO - Config ID and Browser ID - on TESTPLAN -- get_configs/:project_id
		try {
			System.out.println("========Get CONFIGURATION INFO - Config ID and Browser ID - on TESTPLAN =====");
		 	jsonArr=(JSONArray)client.sendGet("get_configs/"+projectID);
		 	System.out.println(jsonArr.toJSONString());
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
		}*/
		
		
		
	/*	FAILED TESTS INFO : get_results_for_run/:run_id and return Test IDs of Failed tests
				Status Codes:
					==============================
					RETEST=4
					BLOCKED=2
					PASSED=1
					FAILED=5
					
	
	 	try {
	 		System.out.println("========Get FAILED TESTS INFO (status_id and test_id OF A TESTRUN=====");
	 		for(Long runId:testRunIDs){
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
		}*/
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
