package de.rwth.setups;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import actions.ActionCalcRelativePos;
import actions.ActionMoveCameraBuffered;
import actions.ActionRotateCameraBuffered;
import actions.ActionWASDMovement;
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

    private List<Bitmap> images;
    private List<String> msgs;

    public final int MAX_NUM_ELEMENTS = 20;


    public EShareSetup(){
        images = new ArrayList<Bitmap>();
        msgs = new ArrayList<String>();


    }

    @Override
    public void _a_initFieldsIfNecessary() {

    }



    @Override
    public void _b_addWorldsToRenderer(GL1Renderer glRenderer, GLFactory objectFactory, GeoObj currentPosition) {

        camera = new GLCamera(new Vec(0.90f, 0, 1));
        world = new World(camera);

        List<MeshComponent> elements = new ArrayList<MeshComponent>();
        int i=0;
        for (Bitmap b: images
             ) {
            elements.add(makeImgMash("Image "+i, b));
            i++;
        }

        for (String msg: msgs
                ) {
            elements.add(makeTextMash(msg, objectFactory, camera));
        }


        makeRing(world, elements, currentPosition, 0);



        glRenderer.addRenderElement(world);


    }


    public void AddImg(Bitmap img){
        images.add(img);
    }

    public void AddText(String text){
        msgs.add(text);
    }


    public MeshComponent makeImgMash(String name, Bitmap img){

        //Bitmap bitmap = BitmapDecoder.decodeSampledBitmapFromResource(getActivity().getResources(), R.drawable.ascoli_piceno, 60, 60);
        MeshComponent mesh = GLFactory.getInstance().newTexturedSquare(name, img);
        mesh.addChild(new AnimationFaceToCamera(camera, 0.5f));

        return mesh;
    }

    public MeshComponent makeTextMash(String msg, GLFactory objectFactory, GLCamera glCamera){

        float textSize = 1f;

        Button v = new Button(getActivity());

        v.setTypeface(null, Typeface.BOLD);
        v.setMaxWidth(750);
        v.setMaxHeight(600);

        v.setBackgroundColor(Color.TRANSPARENT);

        v.setPadding(2, 0, 2,0);


        v.setHorizontallyScrolling(false);


        // Set textcolor to black:
        // v.setTextColor(new Color(0, 0, 0, 1).toIntARGB());
        v.setText(msg);

        Obj o = new Obj();
        MeshComponent mesh = objectFactory.newTexturedSquare("textBitmap"
                + msg, util.IO.loadBitmapFromView(v), textSize);
        mesh.setPosition(new Vec(0,0,0));
        mesh.addAnimation(new AnimationFaceToCamera(glCamera));
        return mesh;
    }

    public void makeRing(World world, List<MeshComponent> elements,GeoObj currentPosition, int currRotation){

        int d=360/MAX_NUM_ELEMENTS;

        double r = 0.0001;
        for (int i=currRotation, j=0; j < elements.size(); i+=d, j++) {

            MeshComponent currMesh = elements.get(j);
            float x = (float)(r * Math.sin(Math.toRadians(i))), y = (float) (r * Math.cos(Math.toRadians(i)));
            float angle = (360 / MAX_NUM_ELEMENTS) * j + 90;

            //set position
            GeoObj geoObj = new GeoObj(currentPosition.getLatitude() + x, currentPosition.getLongitude() + y);


            //set rotation
            Vec oldRot = currMesh.getRotation();
            currMesh.setRotation(new Vec(oldRot==null?0:oldRot.x,  oldRot==null?0:oldRot.y, angle));

            geoObj.setComp(currMesh);


            world.add(geoObj);
        }

    }


    public void makeRing(World world, List<MeshComponent> elements, GeoObj currentPosition){


        int numberOfElements = elements.size();

        int d=360/numberOfElements;

        double r = 0.0001;
        for (int i=0, j=0; i< 360; i+=d, j++) {

            MeshComponent currMesh = elements.get(j);
            float x = (float)(r * Math.sin(Math.toRadians(i))), y = (float) (r * Math.cos(Math.toRadians(i)));
            float angle = (360 / numberOfElements) * j + 90;

            //set position
            GeoObj geoObj = new GeoObj(currentPosition.getLatitude() + x, currentPosition.getLongitude() + y);


            //set rotation
            Vec oldRot = currMesh.getRotation();
            currMesh.setRotation(new Vec(oldRot==null?0:oldRot.x,  oldRot==null?0:oldRot.y, angle));

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
}
