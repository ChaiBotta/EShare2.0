package androigati.eshare.access.dao;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import androigati.eshare.model.Rank;


/**
 * Created by valerio on 27/08/16.
 */
public class RankDAO {
    final String BASE = "http://androigatiserver.westeurope.cloudapp.azure.com/api/ranks";
    final String GET_BY_ID = "http://androigatiserver.westeurope.cloudapp.azure.com/api/ranks/%s";
    private RestTemplate restTemplate;

    public RankDAO() {
        restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
    }

    public Rank[] findAll() {
        ResponseEntity<Rank[]> responseEntity = restTemplate.getForEntity(BASE, Rank[].class);
        return responseEntity.getBody();

    }

    public Rank findById(String id) {
        ResponseEntity<Rank> responseEntity = restTemplate.getForEntity(String.format(GET_BY_ID, id), Rank.class);
        return responseEntity.getBody();
    }

    public boolean insert(Rank rank) {
        ResponseEntity<Rank> responseEntity = restTemplate.postForEntity(BASE, rank, Rank.class);
        return responseEntity.getHeaders().containsValue("200");
    }

    public boolean update(Rank rank) {
        ResponseEntity<String> responseEntity = restTemplate.exchange(String.format(GET_BY_ID, rank.getId()), HttpMethod.PATCH, new HttpEntity<Rank>(rank), String.class);

        return responseEntity.getHeaders().containsValue("200");
    }

    public boolean delete(Rank rank) {
        restTemplate.delete(String.format(GET_BY_ID, rank.getId()));

        return true;
    }
}
