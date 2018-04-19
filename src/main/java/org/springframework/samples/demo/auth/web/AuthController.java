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
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.util.Random;

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
