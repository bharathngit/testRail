/*
To accommodate the current release plans, I would like to create creation of mile stones,sub-mile stones , test plans manual.
We have the code below to add test suites to an existing test plan.(with automation complete test cases) & create individual test runs.
*/
 
 
package com.automation.testrail;
 
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
 
import javax.swing.text.html.HTMLDocument.Iterator;
 
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
 
public class DTestRail_Org {
 
       public static void main(String[] args) throws MalformedURLException,
                     IOException, APIException {
 
              int totalTestCasesCount = 1000;
// Inputs to be provided from Jenkins for executions ..
              
              String projectName = "TravelTracker";//Sandbox";
              String mileStoneName = "TravelTracker_7.3.0";
              String subMileStoneName = "Sprint_10";
              String testPlanName = "TT_7.2.2.0";//TT_7.2.1.10";
              String[] testSuitesName = {"TT_SmokeTest", "TT_Features"};
              String[] testCasesName = new String[totalTestCasesCount];
              String[] testRunName = {
                           "TT_Pen_7.2.1.20_Automation_" + "TT_SmokeTest",
                           "TT_Pen_7.2.1.20_Automation_" + "TT_Features" };
 
              int indexOfAutomationCompleteStatus = 5;
              
              
              Long projectId = 0L;
              Long mileStoneId = 0L;
              Long subMileStoneId = 0L;
              Long testPlanId = 0L;
              Long[] testSuiteId = new Long[2];
              Long[] testCaseId = new Long[totalTestCasesCount];
              Long[] testRunId = new Long[2];
 
              JSONArray jsonArray;
              JSONObject jsonObject;
 
              // TODO Auto-generated method stub
 
              APIClient client = new APIClient(
                           "https://internationalsos.testrail.net");
              client.setUser("deepti.samantra@gallop.net");
              client.setPassword("omOMom#123");
 
              jsonArray = (JSONArray) client.sendGet("get_projects");
              for (int i = 0; i < (jsonArray.size()); i++) {
                     jsonObject = (JSONObject) jsonArray.get(i);
                     if (jsonObject.get("name").equals(projectName)) {
                           projectId = (Long) jsonObject.get("id");
                           System.out.println(projectId
                                         + jsonObject.get("name").toString());
                     }
              }
              jsonArray = (JSONArray) client.sendGet("get_milestones/" + projectId);
              for (int i = 0; i < (jsonArray.size()); i++) {
                     jsonObject = (JSONObject) jsonArray.get(i);
                     if (jsonObject.get("name").equals(mileStoneName)) {
                           mileStoneId = (Long) jsonObject.get("id");
                           System.out.println(mileStoneId
                                         + jsonObject.get("name").toString());
                     }
              }
              jsonObject = (JSONObject) client
                           .sendGet("get_milestone/" + mileStoneId);
              {
                     // System.out.println(jsonObject);
                     jsonArray = (JSONArray) jsonObject.get("milestones");
                     for (int i = 0; i < (jsonArray.size()); i++) {
                           jsonObject = (JSONObject) jsonArray.get(i);
                           if (jsonObject.get("name").equals(subMileStoneName)) {
                                  subMileStoneId = (Long) jsonObject.get("id");
                                  System.out.println(subMileStoneId + "SUB"
                                                + jsonObject.get("name").toString());
                           }
                     }
              }
 
              jsonArray = (JSONArray) client.sendGet("get_suites/" + projectId);
              for (int i = 0; i < (jsonArray.size()); i++) {
                     jsonObject = (JSONObject) jsonArray.get(i);
                     for (int j = 0; j < testSuitesName.length; j++) {
                           if (jsonObject.get("name").equals(testSuitesName[j])) {
                                  testSuiteId[j] = (Long) jsonObject.get("id");
                                  System.out.println(testSuiteId[j]
                                                + jsonObject.get("name").toString());
                           }
                     }
              }
              JSONArray jsonArrayGetRun = (JSONArray) client.sendGet("get_runs/"
                           + projectId);
              
              List<String> availableTestRuns = new ArrayList<String>();
              // String[] availableTestRuns= new String[10];
              for (int j = 0; j < (testRunName.length); j++) {
                     for (int i = 0; i < (jsonArrayGetRun.size()); i++) {
                           JSONObject jsonObjectGetRun = (JSONObject) jsonArrayGetRun
                                         .get(i);
                           if (jsonObjectGetRun.get("name").toString()
                                         .equals(testRunName[j])) {
                                  testRunId[j] = (Long) jsonObjectGetRun.get("id");
                                  availableTestRuns
                                                .add((String) jsonObjectGetRun.get("name"));
                                  System.out.println(testRunId[j] + "**"
                                                + availableTestRuns.get(j)
                                                + jsonObjectGetRun.get("name").toString());
                           }
                     }
              }
              System.out.println(availableTestRuns.size() + "&&&&&&&&&&");
              
              boolean testPlanExists = false;
              boolean testPlanEntryExists = false;
              List testPlanEntryId= new ArrayList();
              jsonArray = (JSONArray) client.sendGet("get_plans/" + projectId);
              for (int getPlansCount = 0; getPlansCount < (jsonArray.size()); getPlansCount++) {
                     jsonObject = (JSONObject) jsonArray.get(getPlansCount);
                     System.out.println((jsonArray.size())+"TTTTTTTTTTTT"+jsonObject.get("name"));
                     System.out.println(jsonObject);
 
// Add test suites with  automation completed status to existing test plan
 
                     if (jsonObject.get("name").equals(testPlanName)) {
                           testPlanId = (Long) jsonObject.get("id");
                           System.out.println(testPlanId
                                         + jsonObject.get("name").toString());
                                  testPlanExists = true;
                                                              
                                         for (int tr=0;tr<testRunName.length;tr++) {
                                                boolean testRunExists=false;
                                                jsonObject = (JSONObject) client.sendGet("get_plan/" + testPlanId);
                                                System.out.println(jsonObject);
                                                testPlanEntryId = (ArrayList)jsonObject.get("entries");
                                                System.out.println("**"+testPlanEntryId);
                                                for(int entries=0;entries<testPlanEntryId.size();entries++)
                                                {
                                                       JSONObject entry= (JSONObject)testPlanEntryId.get(entries);
                                                       System.out.println("***********************"+entry);
                                                       if (testRunName[tr].equals(entry.get("name"))) 
                                                       {
                                                              System.out.println("HEREEEEEEEEEEEEEE");
                                                              Map data = new HashMap();
                                                              data.put("name",testRunName[tr]+"Prior");
                                                              System.out.println("UPDATE" + testRunName[tr]);
                                                              jsonObject = (JSONObject) client.sendPost("update_plan_entry/"
                                                                           + testPlanId+"/"+entry.get("id"), data);
                                                                     testRunExists=true;
                                                       }
                                                       }      
                                                }
                                                //if(testRunExists==false){
                                                //System.out.println("ELSEEEEEEEEEEEEEee");
                                         for (int tr=0;tr<testRunName.length;tr++) {
                                                for(int tSuite=0;tSuite<testSuitesName.length;tSuite++){
                                                       if(testRunName[tr].contains(testSuitesName[tSuite])){
                                                              System.out.println("MMMMMMMMMMMM");
                                                              
                                                              jsonArray = (JSONArray) client.sendGet("get_cases/" + projectId
                                                                           + "&suite_id=" + testSuiteId[tSuite]);
                                                              for (int j = 0; j < jsonArray.size(); j++) {
                                                                     jsonObject = (JSONObject) jsonArray.get(j);
                                                                     if ((Long) jsonObject.get("custom_automationstatus") == indexOfAutomationCompleteStatus) {
                                                                           testCaseId[j] = (Long) jsonObject.get("id");
                                                                           testCasesName[j] = (String) jsonObject.get("title");
                                                                           totalTestCasesCount++;
                                                                     }
                                                              }
                                                              System.out.println(totalTestCasesCount);
                                                              Map data = new HashMap();
                                                              data.put("name",testRunName[tr]);
                                                              data.put("suite_id", testSuiteId[tSuite]);
                                                              data.put("include_all", false);
                                                              List cases = new ArrayList(Arrays.asList(testCaseId));
                                                              data.put("case_ids", cases);
                                                              System.out.println("UPDATE" + testRunName[tr]);
                                                              jsonObject = (JSONObject) client.sendPost("add_plan_entry/"
                                                                           + testPlanId, data);
                                                                                                
                                                }}}                  
                           }
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
}
 

