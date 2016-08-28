package androigati.eshare.access.dao;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import androigati.eshare.model.Content;

/**
 * Created by valerio on 27/08/16.
 */
public class ContentDAO {

    final String BASE = "http://androigatiserver.westeurope.cloudapp.azure.com/api/contents";
    final String GET_BY_ID = "http://androigatiserver.westeurope.cloudapp.azure.com/api/contents/%s";
    private RestTemplate restTemplate;

    public ContentDAO(){
        restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
    }

    public Content[] findAll(){
        ResponseEntity<Content[]> responseEntity = restTemplate.getForEntity(BASE, Content[].class);
        return responseEntity.getBody();

    }
    public Content findById(String id){
        ResponseEntity<Content> responseEntity = restTemplate.getForEntity(String.format(GET_BY_ID, id), Content.class);
        return responseEntity.getBody();
    }

    public boolean insert(Content content){
        ResponseEntity<Content> responseEntity = restTemplate.postForEntity(BASE, content, Content.class);
        return responseEntity.getHeaders().containsValue("200");
    }
    public boolean update(Content content){
        ResponseEntity<String> responseEntity = restTemplate.exchange(String.format(GET_BY_ID, content.getId()), HttpMethod.PATCH, new HttpEntity<Content>(content), String.class);

        return responseEntity.getHeaders().containsValue("200");
    }
    public boolean delete(Content content){
        restTemplate.delete(String.format(GET_BY_ID, content.getId()));

        return true;
    }

}
