import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.HashSet;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.abstracthorizon.danube.auth.jaas.memory.PropertiesLoginModule;
import org.abstracthorizon.danube.auth.jaas.memory.PropertiesModuleService;


public class Check {
    
    
    public static void main(String[] args) throws Exception {
        PropertiesModuleService service = new PropertiesModuleService();
        PropertiesLoginModule module = new PropertiesLoginModule();
        
        service.addUser("user", "password");
        System.out.println(service.getProperties());
        
        HashMap<String, Object> options = new HashMap<String, Object>();
        options.put("properties", service.getProperties());
        
        module.initialize(new Subject(false, new HashSet<Principal>(), new HashSet<Object>(), new HashSet<Object>()), new CallbackHandler() {

            public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
                for (Callback cb : callbacks) {
                    if (cb instanceof NameCallback) {
                        NameCallback nc = (NameCallback)cb;
                        nc.setName("user");
                    } else if (cb instanceof PasswordCallback) {
                        PasswordCallback pc = (PasswordCallback)cb;
                        pc.setPassword("password".toCharArray());
                    }
                }
            }
            
        }, new HashMap<String, Object>(), options);
        
        module.login();
        
    }
    
}
