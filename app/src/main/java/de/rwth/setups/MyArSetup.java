package de.rwth.setups;

import android.app.Activity;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.List;

import androigati.eshare.utils.BitmapDecoder;
import de.rwth.R;
import geo.GeoObj;
import gl.GL1Renderer;
import gl.GLCamera;
import gl.GLFactory;
import gl.animations.AnimationFaceToCamera;
import gl.scenegraph.MeshComponent;
import listeners.eventManagerListeners.LocationEventListener;
import system.DefaultARSetup;
import util.IO;
import util.Vec;
import worldData.Obj;
import worldData.World;

/**
 * Created by Chai on 27/08/2016.
 */
public class MyArSetup extends DefaultARSetup {

    private static final float MIN_DIST = 15f;
    private static final float MAX_DIST = 55f;
    List<Location> locations;
    private LocationListener locationListener;
    private LocationEventListener rec;

    public MyArSetup(LocationListener locationListener) {
        this.locationListener = locationListener;
    }

    @Override
    public void addObjectsTo(GL1Renderer renderer, World world, GLFactory objectFactory) {

    }


    public void setLocations(List<Location> locs) {
        locations = locs;
    }


    @Override
    public void _b_addWorldsToRenderer(GL1Renderer renderer,
                                       GLFactory objectFactory, GeoObj currentPosition) {

        camera = new GLCamera(new Vec(0, 0, 1));
        world = new World(camera);

        Obj textContent = objectFactory.newTextObject("HackTheCity 2016 Chiasso", new Vec(10, 1, 1), getActivity(), camera);

        world.add(textContent);
        if (locations != null && locations.size() > 0)
            addMedia(locations, null, objectFactory, world);

        MeshComponent triangleMesh = GLFactory.getInstance()
                .newTexturedSquare(
                        "elefantId",
                        IO.loadBitmapFromId(getActivity(),
                                R.drawable.elephant64));
        triangleMesh.setScale(new Vec(10, 10, 10));
        triangleMesh.addChild(new AnimationFaceToCamera(camera, 0.5f));
        GeoObj treangleGeo = new GeoObj(GeoObj.newRandomGeoObjAroundCamera(
                camera, MIN_DIST, MAX_DIST), triangleMesh);
        world.add(treangleGeo);

        renderer.addRenderElement(world);

    }

    /*
 @Override
 public void addObjectsTo(GL1Renderer renderer, final World world,
                          GLFactory objectFactory) {
     if(locations!=null && locations.size()>0)
     {
         addMedia(locations,null,objectFactory, world);
     }

     Location location = new Location("");
     location.setLatitude(123);
     location.setLongitude(123);
     Obj o = new GeoObj(location);
     o.setComp(objectFactory.newCube());
     o.setComp(new Entity() {

         private Updateable p;
         private float waitTimeInMilliseconds = 1000;
         UpdateTimer timer = new UpdateTimer(waitTimeInMilliseconds, null);

         @Override
         public boolean accept(Visitor visitor) {
             return false;
         }

         @Override
         public boolean update(float timeDelta, Updateable parent) {
             p = parent;
             if (timer.update(timeDelta, parent)) {
                 // true once a second, do the calculation here:
                 float distanceOfGeoObjToUser = Vec.distance(((GeoObj) p)
                         .getVirtualPosition(), world.getMyCamera()
                         .getPosition());
                 // do something with the distance e.g. update a
                 // corresponding Android UI element
             }
             return true;
         }

         @Override
         public void setMyParent(Updateable parent) {
             this.p = parent;
         }

         @Override
         public Updateable getMyParent() {
             return p;
         }
     });

 }
*/
    public void addMedia(List<Location> locations, List<Content> contents, GLFactory objectFactory, final World world) {
       /* Button b = new Button(getActivity());
        b.setText("Click Me");
        MeshComponent button = GLFactory.getInstance().newTexturedSquare(
                "buttonId", IO.loadBitmapFromView(b));
        button.setOnClickCommand(new CommandShowToast(getActivity(),
                "Thanks alot"));

        button.addChild(new AnimationFaceToCamera(camera, 0.5f));
        button.setScale(new Vec(10, 10, 10));
        button.setColor(Color.red());*/
/*
        GeoObj treangleGeo = new GeoObj(GeoObj.newRandomGeoObjAroundCamera(
                camera, MIN_DIST, MAX_DIST), button);

        world.add(treangleGeo);*/

        // transform android ui elements into opengl models:
        ImageView image = new ImageView(getActivity());
        Bitmap bitmap = BitmapDecoder.decodeSampledBitmapFromResource(getActivity().getResources(), R.drawable.ascoli_piceno, 100, 100);
        image.setImageBitmap(bitmap);
        MeshComponent img = GLFactory.getInstance().newTexturedSquare("imageID", bitmap);
        /*img.setOnClickCommand(new CommandShowToast(getActivity(),
                "Ciao BosS"));
        img.addChild(new AnimationFaceToCamera(camera, 0.5f));
        img.setScale(new Vec(10, 10, 10));

        world.add(img);
        */
        GeoObj o = new GeoObj(locations.get(0)); //only the first location
        o.setComp(img);
        world.add(o);

    }

    public void _e1_addElementsToOverlay(FrameLayout overlayView,
                                         Activity activity) {
        LinearLayout sourceView = new LinearLayout(activity);
        sourceView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
        Button btn1 = new Button(activity);
        btn1.setText("btn1");
        btn1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));

        Button btn2 = new Button(activity);
        btn2.setText("btn2");
        btn2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));

        sourceView.setOrientation(LinearLayout.HORIZONTAL);
        sourceView.addView(btn1);
        sourceView.addView(btn2);
        overlayView.addView(sourceView);
    }

    public class Content {


    }
}
