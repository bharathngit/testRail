package com.automation.testrail;

import java.io.IOException;
import java.net.MalformedURLException;

import org.json.simple.JSONArray;
import org.json.JSONException;
import org.json.simple.JSONObject;

public class ReadfromTestRail {

	public static String baseUrl="https://internationalsos.testrail.net";
	public static String uName="bharath.nadukatla@gallop.net";
	public static String pwD="pqUkNXLigTp63CBxmGfx-WUvg8MVPQZ7UcwO2cFuh";
	public static JSONArray jsonArr;
	public static JSONObject jsonObj;
	public static Long milestoneID;
	public static Long projectID;
	public static Long submilestoneID;
	public static Long planID;
	private static Long configID;
	private static Object browserID;
	
	
	
	
	
	public static void main(String[] args) {

		
		String projectName="Sandbox";
		String milestoneName="Sandbox_1.0";
		String subMilestoneName="SubSandbox_1.0";
		String testPlanName="Sandbox_1.0_TestPlan";
		
		
		//Get Project ID - Access TestRail API get_projects with Parameter is_completed=0 for Active projects
		try {
			projectID=getID("get_projects&is_completed=0",projectName);
			System.out.println("Project ID of "+projectName+": "+projectID);
		} catch (JSONException|IOException | APIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		//Get Milestone ID- get_milestones/:project_id and get_milestone/:milestone_id
		try {
			milestoneID=getID("get_milestones/"+projectID,milestoneName);
			System.out.println("Milestone ID of "+milestoneName+": "+milestoneID);
		} catch (JSONException|IOException | APIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Get Sub-Milestones IDs
		try {
			 
			APIClient client=new APIClient(baseUrl);
		 	client.setUser(uName);
		 	client.setPassword(pwD);
		 	
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			planID=getID("get_plans/"+projectID,testPlanName);
			System.out.println("TestPlan ID of "+testPlanName+": "+planID);
		} catch (JSONException|IOException | APIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		//Get configuration info - Config ID and Browser ID - on Testplan -- get_configs/:project_id
		try {
			/*configID=getID("get_configs/"+projectID,"chrome");
			System.out.println("configID of chrome: "+configID);
			*/
			APIClient client=new APIClient(baseUrl);
		 	client.setUser(uName);
		 	client.setPassword(pwD);
		 	
		 	jsonArr=(JSONArray)client.sendGet("get_configs/"+projectID);
			
			//jsonArr=(JSONArray)jsonObj.get("milestones");
			System.out.println(jsonArr.size());
			//Config ID for Web Browsers
			for(int i=0;i<jsonArr.size();i++){
				
				System.out.println("Array: "+jsonArr.get(i).toString());
				
				jsonObj=(JSONObject)jsonArr.get(i);
				if(jsonObj.get("name").toString().equalsIgnoreCase("Web Browsers")){
					configID=(Long) jsonObj.get("id");
				}
				
				JSONArray configsArr=(JSONArray)jsonObj.get("configs");
				System.out.println("Configs"+configsArr);
				//Browser ID
				for(int j=0;j<configsArr.size();j++){
					
					jsonObj=(JSONObject)configsArr.get(j);
					
					if((jsonObj.get("name").toString()).equalsIgnoreCase("Chrome")){
						browserID=jsonObj.get("id");
					}
				}
				System.out.println("No. of configs"+configsArr.size());			
			}
			
			System.out.println("configID of Web Browsers: "+configID);
			System.out.println("browserID of Chromers: "+browserID);
			
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
 
		//Fetches list of all the ACTIVE projects
	 
		 jsonArr= (JSONArray)client.sendGet(attribute);
		 
		 System.out.println("No. of items: "+(jsonArr.size()));
		 
		 for(int i=0;i<jsonArr.size();i++){
			 System.out.println("inside for loop");
			 jsonObj=(JSONObject)jsonArr.get(i);
			 if((jsonObj.get("name").toString().equalsIgnoreCase(attName))){
				 id=(Long)jsonObj.get("id");
				 break;
			 }
		 }
		 
		return id;
		
	}

}
