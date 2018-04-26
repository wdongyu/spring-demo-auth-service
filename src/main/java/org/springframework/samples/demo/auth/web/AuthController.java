package org.springframework.samples.demo.auth.web;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.HashMap;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.util.Random;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Properties;
import org.springframework.web.bind.annotation.CrossOrigin;

/**
 * @author wdongyu
 */

@RestController
public class AuthController {

    private final Logger logger = Logger.getLogger(getClass());

    private HashMap<String, String> map = new HashMap<String, String>();

    @Autowired
    private DiscoveryClient client;

    @RequestMapping(value = "/auth/{param}/{username}/{password}" ,method = RequestMethod.GET)
    public String auth(@PathVariable String param, @PathVariable String username, @PathVariable String password) {
        //ServiceInstance instance = client.getLocalServiceInstance();
        //logger.info("/auth, host:" + instance.getHost() + ", service_id:" + instance.getServiceId());
        //return "Token from Authenticate";
        
        if (param.equals("portal")) {
            //logger.info(username + ":" + password);
            String authId = getCommitId(serviceInfo());
            Random random = new Random();
            String retUsername = null;
            do {
                retUsername = String.valueOf(random.nextInt(9999));
            } while (map.containsKey(retUsername));
            String retPassword = String.valueOf(random.nextInt(9999));
            map.put(retUsername, retPassword);
            return (authId + ":" + retUsername + ":" + retPassword);
        }
        else if (param.equals("proc")) {
            if (password.equals(map.get(username))) {
                map.remove(username);
                return "Pass";
            }
            else 
                return "Fail";
        }
        else {
            return "Fail";
        }
    }

    @RequestMapping(value = "/servicePath" ,method = RequestMethod.GET)
    @CrossOrigin(origins = "*")
    public JSONObject servicePath() {
        Properties properties = System.getProperties();
        String path = properties.getProperty("user.dir");
        String res = "{\"path\" : \"" + path + "\"}";

        //execCommand("/usr/bin/open -a /Applications/Utilities/Terminal.app");
        execCommand("git pull");
        execCommand("mvn package");
        execCommand("nohup java -jar ./target/spring-demo-auth-service-1.5.6.jar &");
        //Process pro = null;
        try {
            //pro = rt.exec("mvn spring-boot:run", null, new File(path));
            //ProcessBuilder pb = new ProcessBuilder(cmdString);
            //pb.start();
            return (JSONObject)(new JSONParser().parse(res));
        } catch (Exception e) {
            //TODO: handle exception
            logger.info(e);
        }
        
        return null;
    }


    private void execCommand(String command) {
        try {
            Process pro = Runtime.getRuntime().exec(command);
            if (pro != null) {
                BufferedReader in = new BufferedReader(new InputStreamReader(pro.getInputStream()));
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(pro.getOutputStream())), true);
                try {
                   String line;
                   while ((line = in.readLine()) != null) {
                      logger.info(line);
                   }
                   pro.waitFor();
                   in.close();
                   out.close();
                   pro.destroy();
                }
                catch (Exception e) {
                   logger.info("aaa");
                }
             }
        } catch (Exception e) {
            logger.info("fail to exec " + command);
        }
    }


    public String serviceInfo() {
        //List<ServiceInstance> list = this.client.getInstances(serviceName);
        try {
            URL url = new URL(client.getLocalServiceInstance().getUri().toString() + "/info");
            URLConnection urlConnection = url.openConnection();
            urlConnection.connect();
            InputStream is = urlConnection.getInputStream();
            BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
            StringBuffer bs = new StringBuffer();
            String str = null;
            while((str=buffer.readLine())!=null){
                bs.append(str);
            }
            buffer.close();
            return bs.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getCommitId(String str) {
        try {
            if (str == null) return null;
            JSONObject json = (JSONObject)(new JSONParser().parse(str));
            json = (JSONObject)(json.get("git"));
            json = (JSONObject)(json.get("commit"));
            return json.get("id").toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
