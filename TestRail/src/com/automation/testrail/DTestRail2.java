package com.automation.testrail;


import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
 













import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
 


public class DTestRail2 {
	 /*
	To accommodate the current release plans, I would like to create creation of mile stones,sub-mile stones , test plans manual.
	We have the code below to add test suites to an existing test plan.(with automation complete test cases) & create individual test runs.
	*/
	
		// Inputs to be provided from Jenkins for executions ..
		              
		          
	 
		              
		              
		          	APIClient client = new APIClient(
			                 "https://internationalsos.testrail.net");
		 
		             
		              
		              
     public static void main(String[] args) throws MalformedURLException,
	                     IOException, APIException {
         int totalTestCasesCount = 1000;
         int indexOfAutomationCompleteStatus = 5;
    	     String projectName = "TravelTracker";//Sandbox";
               String mileStoneName = "Sandbox";
               String subMileStoneName = "SandboxSubMilestone";
               String testPlanName = "TestPlanSandbox";
               String[] testSuitesName = {"TestSuiteSandbox", "TestSuiteSandbox2"};
               String[] testCasesName = new String[totalTestCasesCount];
               String[] testRunName = {
                         "TestPlanSandbox_Automation_" + "TestSuiteSandbox",
                         "TestPlanSandbox_Automation_" + "TestSuiteSandbox2" };
          
                Long projectId = 0L;
	               Long mileStoneId = 0L;
	               Long subMileStoneId = 0L;
	               Long testPlanId = 0L;
	               Long[] testSuiteId = new Long[2];
	               Long[] testCaseId = new Long[totalTestCasesCount];
	               Long[] testRunId = new Long[2];
	               
	                boolean testPlanExists = false;
	                boolean testPlanEntryExists = false;
	                boolean testRunExists=false;
	             
	                JSONArray jsonArray;
	                JSONObject jsonObject;
	                APIClient client = new APIClient(
			                 "https://internationalsos.testrail.net");
					    client.setUser("bharath.nadukatla@gallop.net");
					    client.setPassword("pqUkNXLigTp63CBxmGfx-WUvg8MVPQZ7UcwO2cFuh");
	                
	             //Setting up login url and creds
	              dologin();
	             try {
					//Getting Project ID of TT
					 projectId=DTestRail2.getID("get_projects", projectName);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	             try {
					//Getting Milestone ID of Sandbox
					 mileStoneId=getID("get_milestones/" + projectId, mileStoneName);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	           try {
					//Getting sub-Milestone ID of Sandbox 
					 subMileStoneId=getID("get_milestones/" + mileStoneId, subMileStoneName);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} 
	              
	           try {
					//Getting Test suites linked to Milestone Sandbox
					  testSuiteId=getIDs("get_suites/" + projectId, testSuitesName);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	         
	            try {
					//Get test runs from project TT
					  
					  testRunId=getIDs("get_runs/"
					          + projectId, testRunName);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	            
	              /* 
	               * 
	               * // Add test suites with  automation completed status to existing test plan
	              * //each test plan has seperate entries id, hence getting those entries id and adding to a list        
	              * 
	              *    
	              */	              
	              List testPlanEntryId= new ArrayList();
	             
	              //reading all testplans from the project
	              jsonArray = (JSONArray) client.sendGet("get_plans/" + projectId);
	             
	              //iterate through json array and get testplan name
	              try {
					for (int getPlansCount = 0; getPlansCount < (jsonArray.size()); getPlansCount++) {
					         jsonObject = (JSONObject) jsonArray.get(getPlansCount);
					         String planName =jsonObject.get("name").toString();
					         System.out.println(("No. of Test plans:"+jsonArray.size())+"Testplan name:"+planName);
					        
 
					         //Compare the test plan names with the required testplan name
					         if (planName.equals(testPlanName)) {
					               //Get the testplan id if it matches with the testplan name we are looking for
					        	 
					               testPlanId = (Long) jsonObject.get("id");

					               System.out.println("TestplanID:"+testPlanId
					                             +"Test plan name:"+ planName);
					               
					               testPlanExists = true;
					                                                  
					                         for (int tr=0;tr<testRunName.length;tr++) {
					                                
					                        	 	//Get testplan object
					                                jsonObject = (JSONObject) client.sendGet("get_plan/" + testPlanId);
					                                System.out.println(jsonObject);
					                           	
					                                //Get info from entries object array of the test plan object and store them into an array list

					                                testPlanEntryId = (ArrayList)jsonObject.get("entries");
					                                
					                                System.out.println("testPlanEntryId="+testPlanEntryId.toString());
					                                
					                                /*Iterate through the entries object list and look for Test run names
					                                 * Each entries array contains a Test run name present in the test plan
					                                 * */
					                                for(int entries=0;entries<testPlanEntryId.size();entries++)
					                                {
					                                    //get entry object   
					                                	JSONObject entry= (JSONObject)testPlanEntryId.get(entries);
					                                       
					                               
					                                    //If required test run name equals the entry name(testrun name itself)
					                                	if (testRunName[tr].equals(entry.get("name"))) 
					                                       {
					                                              System.out.println("Found the required testrun"+entry.get("name"));
					                                              Map data = new HashMap();
					                                              data.put("name",testRunName[tr]+"Prior");
					                                              System.out.println("UPDATE" + testRunName[tr]);

					                                              //append test run name with "prior"
					                                              jsonObject = (JSONObject) client.sendPost("update_plan_entry/"
					                                                           + testPlanId+"/"+entry.get("id"), data);
					                                                     testRunExists=true;
					                                       }
					                                       }      
					                                }
					                                //if(testRunExists==false){
					                                //System.out.println("ELSEEEEEEEEEEEEEee");
					                      
					                         //Get tests from the corresponding suite and add to the Test runs accordingly
					                         for (int tr=0;tr<testRunName.length;tr++) {
					                        	 
					                                for(int tSuite=0;tSuite<testSuitesName.length;tSuite++){
					                                	
					                                		//Compare the testsuite names and testrun names, if they are same then
					                                       if(testRunName[tr].contains(testSuitesName[tSuite])){
					                                              System.out.println("found the required test suites from testruns");
					                                              
					                                              //Get testcases from the testsuite
					                                              jsonArray = (JSONArray) client.sendGet("get_cases/" + projectId
					                                                           + "&suite_id=" + testSuiteId[tSuite]);
					                                              //Get test case ids and names from that testsuite
					                                              for (int j = 0; j < jsonArray.size(); j++) {
					                                                     jsonObject = (JSONObject) jsonArray.get(j);
					                                                     if ((Long) jsonObject.get("custom_automationstatus") == indexOfAutomationCompleteStatus) {
					                                                           testCaseId[j] = (Long) jsonObject.get("id");
					                                                           testCasesName[j] = (String) jsonObject.get("title");
					                                                           totalTestCasesCount++;
					                                                     }
					                                              }
					                                              System.out.println("Total test cases="+testCaseId.length);
					                                             
					                                             //Put name, suite_id, include_all into a HashMap 
					                                              Map data = new HashMap();
					                                              data.put("name",testRunName[tr]);
					                                              data.put("suite_id", testSuiteId[tSuite]);
					                                              data.put("include_all", false);
					                                              
					                                              //Put case_ids into a Arraylist and add to the HashMap 'data'
					                                              List cases = new ArrayList(Arrays.asList(testCaseId));
					                                              data.put("case_ids", cases);
					                                              
					                                              System.out.println("UPDATED test run=" + testRunName[tr]);
					                                             //POST the HashMap "data" to add_plan_entry API method
					                                              jsonObject = (JSONObject) client.sendPost("add_plan_entry/"
					                                                           + testPlanId, data);
					                                                                                
					                                }
					                              }
					                                } 
					                         break;
					               }
					  }
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	 
	//Create individual test runs
	 
	              /*for (int i = 0; i < testSuiteId.length; i++) {
	                     jsonArray = (JSONArray) client.sendGet("get_cases/" + projectId
	                                  + "&suite_id=" + testSuiteId[i]);
	                     for (int j = 0; j < jsonArray.size(); j++) {
	                           jsonObject = (JSONObject) jsonArray.get(j);
	                           if ((Long) jsonObject.get("custom_automationstatus") == indexOfAutomationCompleteStatus) {
	                                  testCaseId[j] = (Long) jsonObject.get("id");
	                                  testCasesName[j] = (String) jsonObject.get("title");
	                                  totalTestCasesCount++;
	                           }
	                     }
	                     System.out.println(totalTestCasesCount);
	                     if (availableTestRuns.toString().contains(testSuitesName[i])) 
	                           {
	                                  Map data = new HashMap();
	                                  data.put("suite_id", testSuiteId[i]);
	                                  data.put("include_all", false);
	                                  List cases = new ArrayList(Arrays.asList(testCaseId));
	                                  data.put("case_ids", cases);
	                                   System.out.println("UPDATE" + testRunName[i]);
	                                  jsonObject = (JSONObject) client.sendPost("update_run/"
	                                                + testRunId[i], data);
	                           } else 
	                           {
	                                  Map data = new HashMap();
	                                  data.put("milestone", subMileStoneId);
	                                  data.put("name", testRunName[i]);
	                                  data.put("description", "TEST" + testPlanName);
	                                  data.put("suite_id", testSuiteId[i]);
	                                  data.put("include_all", false);
	                                  List cases = new ArrayList(Arrays.asList(testCaseId));
	                                  data.put("case_ids", cases);
	                                  System.out.println("ADD" + testRunName[i]);
	                                  jsonObject = (JSONObject) client.sendPost("add_run/"
	                                                + projectId, data);
	                           }
	                           
	              }*/
	       }
     
		     public static void dologin() {
				// TODO Auto-generated method stub
		    	 System.out.println("Inside login");
		    	 	
				    System.out.println("Login complete");
			}
		
			public static Long getID(String attribute, String attrName) throws MalformedURLException, IOException, APIException{
				 JSONArray jsonArray;
	                JSONObject jsonObject;
	                APIClient client = new APIClient(
			                 "https://internationalsos.testrail.net");
					    client.setUser("bharath.nadukatla@gallop.net");
					    client.setPassword("pqUkNXLigTp63CBxmGfx-WUvg8MVPQZ7UcwO2cFuh");
		    	 Long attributeId = 0L;
		    	 jsonArray = (JSONArray) client.sendGet(attribute);
		        for (int i = 0; i < (jsonArray.size()); i++) {
		                jsonObject = (JSONObject) jsonArray.get(i);
		               
		                	 if (jsonObject.get("name").equals(attrName)) {
		                      attributeId = (Long) jsonObject.get("id");
		                      System.out.println(attributeId+"->"+jsonObject.get("name").toString());
		                }
		         }
		        return attributeId;
		     }
			
			public static Long[] getIDs(String attribute, String[] attrName) throws MalformedURLException, IOException, APIException{
				 JSONArray jsonArray;
	                JSONObject jsonObject;
	                APIClient client = new APIClient(
			                 "https://internationalsos.testrail.net");
					    client.setUser("bharath.nadukatla@gallop.net");
					    client.setPassword("pqUkNXLigTp63CBxmGfx-WUvg8MVPQZ7UcwO2cFuh");    	
		    	 jsonArray = (JSONArray) client.sendGet(attribute);
		    	 Long[] attributeId=new Long[jsonArray.size()];
		    	 
		        for (int i = 0; i < (jsonArray.size()); i++) {
		                jsonObject = (JSONObject) jsonArray.get(i);
		               
		                for (int j = 0; j < attrName.length; j++) {
	                         if (jsonObject.get("name").equals(attrName[j])) {
	                        	 attributeId[j] = (Long) jsonObject.get("id");
	                                System.out.println(attributeId[j]+"->"+ jsonObject.get("name").toString());
	                         }
		                }
		         }
		        return attributeId;
		     }
			
	}


