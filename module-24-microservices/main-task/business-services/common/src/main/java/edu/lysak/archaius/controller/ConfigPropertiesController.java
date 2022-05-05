package edu.lysak.archaius.controller;

import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfigPropertiesController {

    /*
    After starting the app we can send a request to this endpoint,
    and the service will retrieve the values stored in config.properties as expected.

    Then change the values of the property in the classpath file (target/classes/config.properties).
    Without restarting the service, after a minute or so, a call to the endpoint should retrieve the new values.
     */

    @GetMapping("/properties/{property}")
    public String getPropertyValue(@PathVariable("property") String propertyName) {
        DynamicPropertyFactory propertyFactory = DynamicPropertyFactory.getInstance();
        DynamicStringProperty property = propertyFactory.getStringProperty(propertyName, "Not found");
        return property.get();
    }
}
