package de.rwth.setups;

import android.app.Activity;
import android.location.Location;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import geo.GeoObj;
import gl.GL1Renderer;
import gl.GLFactory;
import system.DefaultARSetup;
import util.Vec;
import worldData.Entity;
import worldData.Obj;
import worldData.UpdateTimer;
import worldData.Updateable;
import worldData.Visitor;
import worldData.World;

/**
 * Created by Chai on 27/08/2016.
 */
public class MyArSetup extends DefaultARSetup {

    @Override
    public void addObjectsTo(GL1Renderer renderer, final World world,
                             GLFactory objectFactory) {

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

    public void _e1_addElementsToOverlay(FrameLayout overlayView,
                                         Activity activity) {
        LinearLayout sourceView = new LinearLayout(activity);
        sourceView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT ));
        Button btn1 = new Button(activity);
        btn1.setText("btn1");
        btn1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT ));

        Button btn2 = new Button(activity);
        btn2.setText("btn2");
        btn2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT ));

        sourceView.setOrientation(LinearLayout.HORIZONTAL);
        sourceView.addView(btn1);
        sourceView.addView(btn2);
        overlayView.addView(sourceView);
    }
}
