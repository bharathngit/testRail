

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.automation.testrail.APIClient;
import com.automation.testrail.APIException;



public class DTestRail1 {

       public static void main(String[] args) throws MalformedURLException, IOException, APIException {
              
              
              APIClient client = new APIClient("https://internationalsos.testrail.net");
        
              client.setUser("bharath.nadukatla@gallop.net"); // your log in
              client.setPassword("pqUkNXLigTp63CBxmGfx-WUvg8MVPQZ7UcwO2cFuh"); //your password
              
              
              System.out.println("Setting up URL and Login credentials is successful");
              
              
              JSONObject c;
			try {
				System.out.println("Getting test case title from Testrail");
				  c = (JSONObject) client.sendGet("get_case/3373");
				  System.out.println(c.get("title")); //Printing the title of case #3373 
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
              
            
              JSONArray j;
			try {
				System.out.println("Getting projects from Test Rail");
				  j = (JSONArray) client.sendGet("get_projects");
				  for(int i=0;i<(j.size());i++)
				         System.out.println(j.get(i));
			} catch (Exception e) {
				
				e.printStackTrace();
			}
           
              /* Map data = new HashMap();
              data.put("name", "Testing Add Mile Stone on Sandbox");
              data.put("description", "This test worked fine!");
              //JSONObject r = (JSONObject) client.sendPost("add_result_for_case/1/1", data);
              //c = (JSONObject) client.sendPost("add_milestone/1",data);
               */            
              
			/*try {
				System.out.println("Getting Milestones 253 from Test Rail");
				  j = (JSONArray) client.sendGet("get_milestones/253");
				  for(int i=0;i<(j.size());i++)
				         System.out.println(j.get(i));
			} catch (Exception e) {
				e.printStackTrace();
			}*/
             
              /*j = (JSONArray) client.sendGet("get_milestones/262");
              for(int i=0;i<(j.size());i++)
                     System.out.println(j.get(i));*/
              
              try {
  				System.out.println("Getting Milestone 272 from Test Rail");

				c = (JSONObject) client.sendGet("get_milestone/272");
				  System.out.println(c.get("milestones")); 
				  System.out.println(c.get("id"));
			} catch (Exception e) {
				e.printStackTrace();
			}
             
              /*Map data1 = new HashMap();
              data1.put("name", "Testing Add SUB Mile Stone on Sandbox");
              data1.put("description", "This test worked fine!");
              data1.put("parent_id","262");
              //c = (JSONObject) client.sendPost("add_milestone/1",data1);*/
              /*Map data3 = new HashMap();
              data3.put("name", "Test PLAN");
              data3.put("description", "This test worked fine!");
              data3.put("milestone_id","264");
              c = (JSONObject) client.sendPost("add_plan/1",data3);*/
              /*j = (JSONArray) client.sendGet("get_plans/1");
              for(int i=0;i<(j.size());i++)
                     System.out.println(j.get(i));*/
              
               
              
              
				try {
					System.out.println("Getting Suites 3072 from Test Rail");
					  c = (JSONObject) client.sendGet("get_suites/3072");
					  System.out.println(c.get("id"));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				//get all cases in a test suite
				try {
					System.out.println("Getting testcases from Test suite 3072");

					j = (JSONArray) client.sendGet("get_cases/2&suite_id=3072");
					for(int i=0;i<(j.size());i++)
					     System.out.println(j.get(i));
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				/*try {
					Map<String, String> data4 = new HashMap<String, String>();
					  data4.put("name", "Testing Add TEST RUN on TravelTracker_7.3.0");
					  data4.put("description", "This test worked fine!");
					  data4.put("milestone_id","272");
					  data4.put("suite_id","3072");
					  c = (JSONObject) client.sendPost("add_runs/1",data4);
					  for(Map.Entry<String, String> x:data4.entrySet()){
						  System.out.println(x.getKey());
						  System.out.println(x.getValue());
					  }
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
			
            	  

       }

}

