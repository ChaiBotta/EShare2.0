package androigati.eshare.access;

import android.location.Location;

import java.util.Arrays;
import java.util.List;

import androigati.eshare.access.dao.ContentDAO;
import androigati.eshare.model.Content;

/**
 * Created by Antonello Fodde on 27/08/16.
 * fodde.antonello@gmail.com
 */
public class AccessManager {

    public static List<Content> getNearbyContent(Location location) {
        ContentDAO contentDAO = new ContentDAO();
        return Arrays.asList(contentDAO.findAll());
    }
}
