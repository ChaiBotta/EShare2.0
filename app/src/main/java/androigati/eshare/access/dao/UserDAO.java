package androigati.eshare.access.dao;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import androigati.eshare.model.User;


/**
 * Created by valerio on 27/08/16.
 */
public class UserDAO {
    final String BASE = "http://androigatiserver.westeurope.cloudapp.azure.com/api/utenti";
    final String GET_BY_ID = "http://androigatiserver.westeurope.cloudapp.azure.com/api/utenti/%s";
    private RestTemplate restTemplate;

    public UserDAO() {
        restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
    }

    public User[] findAll() {
        ResponseEntity<User[]> responseEntity = restTemplate.getForEntity(BASE, User[].class);
        return responseEntity.getBody();

    }

    public User findById(String id) {
        ResponseEntity<User> responseEntity = restTemplate.getForEntity(String.format(GET_BY_ID, id), User.class);
        return responseEntity.getBody();
    }

    public boolean insert(User user) {
        ResponseEntity<User> responseEntity = restTemplate.postForEntity(BASE, user, User.class);
        return responseEntity.getHeaders().containsValue("200");
    }

    public boolean update(User user) {
        ResponseEntity<String> responseEntity = restTemplate.exchange(String.format(GET_BY_ID, user.getId()), HttpMethod.PATCH, new HttpEntity<User>(user), String.class);

        return responseEntity.getHeaders().containsValue("200");
    }

    public boolean delete(User user) {
        restTemplate.delete(String.format(GET_BY_ID, user.getId()));

        return true;
    }
}
