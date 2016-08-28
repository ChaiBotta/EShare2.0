package de.rwth.setups;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.List;

import actions.ActionCalcRelativePos;
import actions.ActionMoveCameraBuffered;
import actions.ActionRotateCameraBuffered;
import actions.ActionWASDMovement;
import de.rwth.R;
import geo.GeoObj;
import gl.CustomGLSurfaceView;
import gl.GL1Renderer;
import gl.GLCamera;
import gl.GLFactory;
import gl.animations.AnimationFaceToCamera;
import gl.scenegraph.MeshComponent;
import gui.GuiSetup;
import system.EventManager;
import system.Setup;
import util.Vec;
import worldData.Obj;
import worldData.SystemUpdater;
import worldData.World;

/**
 * Created by valerio on 27/08/16.
 */
public class EShareSetup extends Setup {

    private  World world;
    private GLCamera camera;

    private ActionCalcRelativePos geoupdater;
    private ActionWASDMovement wasdAction;
    private ActionRotateCameraBuffered rotateAction;

    private List<ContentViewModel> contents;

    public final int MAX_NUM_ELEMENTS = 20;


    public EShareSetup(){
        contents = new ArrayList<ContentViewModel>();

    }

    @Override
    public void _a_initFieldsIfNecessary() {

    }

    public static class ContentViewModel{
        private String title;
        private String body;
        private String url;
        private String type;

        public Bitmap getImg() {
            return img;
        }

        public void setImg(Bitmap img) {
            this.img = img;
        }

        private Bitmap img;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }



    @Override
    public void _b_addWorldsToRenderer(GL1Renderer glRenderer, GLFactory objectFactory, GeoObj currentPosition) {

        camera = new GLCamera(new Vec(0.90f, 0, 1));
        world = new World(camera);

        List<MeshComponent> elements = new ArrayList<MeshComponent>();
        int i=0;


        for (ContentViewModel content: contents
             ) {

            switch (content.getType()){
                case "image":
                    elements.add(makeImgMash(content.title, content.getImg()));
                    break;
                case "text":
                    elements.add(makeTextMash(content.body, objectFactory,camera));
                    break;
                case "video":
                    break;
            }

        }


        makeRing(world, elements, currentPosition, 0);



        glRenderer.addRenderElement(world);


    }


    public void AddElement(ContentViewModel content){
        contents.add(content);
    }


    public MeshComponent makeImgMash(String name, Bitmap img) {

        //Bitmap bitmap = BitmapDecoder.decodeSampledBitmapFromResource(getActivity().getResources(), R.drawable.ascoli_piceno, 60, 60);
        MeshComponent mesh = GLFactory.getInstance().newTexturedSquare(name, img);
        mesh.addChild(new AnimationFaceToCamera(camera, 0.5f));

        return mesh;
    }

    public MeshComponent makeTextMash(String msg, GLFactory objectFactory, GLCamera glCamera) {

        float textSize = 1f;

        Button v = new Button(getActivity());

        v.setTypeface(null, Typeface.BOLD);
        v.setMaxWidth(750);
        v.setMaxHeight(600);

        v.setBackgroundColor(Color.TRANSPARENT);

        v.setPadding(2, 0, 2, 0);


        v.setHorizontallyScrolling(false);


        // Set textcolor to black:
        // v.setTextColor(new Color(0, 0, 0, 1).toIntARGB());
        v.setText(msg);

        Obj o = new Obj();
        MeshComponent mesh = objectFactory.newTexturedSquare("textBitmap"
                + msg, util.IO.loadBitmapFromView(v), textSize);
        mesh.setPosition(new Vec(0, 0, 0));
        mesh.addAnimation(new AnimationFaceToCamera(glCamera));
        return mesh;
    }

    public void makeRing(World world, List<MeshComponent> elements, GeoObj currentPosition, int currRotation) {

        int d = 360 / MAX_NUM_ELEMENTS;

        double r = 0.0001;
        for (int i = currRotation, j = 0; j < elements.size(); i += d, j++) {

            MeshComponent currMesh = elements.get(j);
            float x = (float) (r * Math.sin(Math.toRadians(i))), y = (float) (r * Math.cos(Math.toRadians(i)));
            float angle = (360 / MAX_NUM_ELEMENTS) * j + 90;

            //set position
            GeoObj geoObj = new GeoObj(currentPosition.getLatitude() + x, currentPosition.getLongitude() + y);


            //set rotation
            Vec oldRot = currMesh.getRotation();
            currMesh.setRotation(new Vec(oldRot == null ? 0 : oldRot.x, oldRot == null ? 0 : oldRot.y, angle));

            geoObj.setComp(currMesh);


            world.add(geoObj);
        }

    }


    public void makeRing(World world, List<MeshComponent> elements, GeoObj currentPosition) {


        int numberOfElements = elements.size();

        int d = 360 / numberOfElements;

        double r = 0.0001;
        for (int i = 0, j = 0; i < 360; i += d, j++) {

            MeshComponent currMesh = elements.get(j);
            float x = (float) (r * Math.sin(Math.toRadians(i))), y = (float) (r * Math.cos(Math.toRadians(i)));
            float angle = (360 / numberOfElements) * j + 90;

            //set position
            GeoObj geoObj = new GeoObj(currentPosition.getLatitude() + x, currentPosition.getLongitude() + y);


            //set rotation
            Vec oldRot = currMesh.getRotation();
            currMesh.setRotation(new Vec(oldRot == null ? 0 : oldRot.x, oldRot == null ? 0 : oldRot.y, angle));

            geoObj.setComp(currMesh);


            world.add(geoObj);
        }

    }

    @Override
    public void _c_addActionsToEvents(EventManager eventManager, CustomGLSurfaceView arView, SystemUpdater updater) {
        wasdAction = new ActionWASDMovement(camera, 25f,
                50f, 20f);
        rotateAction = new ActionRotateCameraBuffered(
                camera);


        arView.addOnTouchMoveAction(wasdAction);
        eventManager.addOnOrientationChangedAction(rotateAction);

        eventManager.addOnTrackballAction(new ActionMoveCameraBuffered(camera,
                5, 25));

        geoupdater = new ActionCalcRelativePos(world, camera);
        eventManager.addOnLocationChangedAction(geoupdater);
    }

    @Override
    public void _d_addElementsToUpdateThread(SystemUpdater updater) {
        updater.addObjectToUpdateCycle(wasdAction);
        updater.addObjectToUpdateCycle(rotateAction);
    }

    @Override
    public void _e2_addElementsToGuiSetup(GuiSetup guiSetup, Activity activity) {

    }

    @Override
    public void _e1_addElementsToOverlay(FrameLayout overlayView,
                                         final Activity activity) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View switchView = inflater.inflate(R.layout.ar_switch, null);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(16, 16, 16, 16);
        Switch arSwitch = (Switch) switchView.findViewById(R.id.ar_switch);
        arSwitch.setChecked(true);
        arSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            activity.finish();
                        }
                    }, 500);
                }
            }
        });
        overlayView.addView(switchView, layoutParams);
    }
}
