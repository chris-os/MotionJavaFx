package moleculesampleapp;

import com.leapmotion.leap.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class MoleculeSampleApp extends Application {

    final Group root = new Group();
    final Xform axisGroup = new Xform();
    final Xform world = new Xform();
    final Xform moleculeGroup = new Xform();
    final PerspectiveCamera camera = new PerspectiveCamera(true);
    final Xform cameraXform = new Xform();
    final Xform cameraXform2 = new Xform();
    final Xform cameraXform3 = new Xform();
    private static final double CAMERA_INITIAL_DISTANCE = -500;
    private static final double CAMERA_INITIAL_X_ANGLE = 70.0;
    private static final double CAMERA_INITIAL_Y_ANGLE = 320.0;
    private static final double CAMERA_NEAR_CLIP = 0.1;
    private static final double CAMERA_FAR_CLIP = 10000.0;
    private static final double AXIS_LENGTH = 250.0;
    private static final double HYDROGEN_ANGLE = 104.5;
    private static final double CONTROL_MULTIPLIER = 0.1;
    private static final double SHIFT_MULTIPLIER = 10.0;
    private static final double MOUSE_SPEED = 0.1;
    private static final double ROTATION_SPEED = 2.0;
    private static final double TRACK_SPEED = 0.3;

    double mousePosX;
    double mousePosY;
    double mouseOldX;
    double mouseOldY;
    double mouseDeltaX;
    double mouseDeltaY;
    private Controller controller;
    private SampleListener listener;
    private Sphere sp;

    private void buildCamera() {
        root.getChildren().add(cameraXform);
        cameraXform.getChildren().add(cameraXform2);
        cameraXform2.getChildren().add(cameraXform3);
        cameraXform3.getChildren().add(camera);
        cameraXform3.setRotateZ(180.0);

        camera.setNearClip(CAMERA_NEAR_CLIP);
        camera.setFarClip(CAMERA_FAR_CLIP);
        camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
        cameraXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
        cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
    }

    private void buildAxes() {
        final PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(Color.DARKRED);
        redMaterial.setSpecularColor(Color.RED);

        final PhongMaterial greenMaterial = new PhongMaterial();
        greenMaterial.setDiffuseColor(Color.DARKGREEN);
        greenMaterial.setSpecularColor(Color.GREEN);

        final PhongMaterial blueMaterial = new PhongMaterial();
        blueMaterial.setDiffuseColor(Color.DARKBLUE);
        blueMaterial.setSpecularColor(Color.BLUE);

        final Box xAxis = new Box(AXIS_LENGTH, 1, 1);
        final Box yAxis = new Box(1, AXIS_LENGTH, 1);
        final Box zAxis = new Box(1, 1, AXIS_LENGTH);

        xAxis.setMaterial(redMaterial);
        yAxis.setMaterial(greenMaterial);
        zAxis.setMaterial(blueMaterial);

        axisGroup.getChildren().addAll(xAxis, yAxis, zAxis);
        axisGroup.setVisible(false);
        world.getChildren().addAll(axisGroup);
    }

    private void buildMolecule() {

        final PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(Color.DARKRED);
        redMaterial.setSpecularColor(Color.RED);

        final PhongMaterial whiteMaterial = new PhongMaterial();
        whiteMaterial.setDiffuseColor(Color.WHITE);
        whiteMaterial.setSpecularColor(Color.LIGHTBLUE);

        final PhongMaterial greyMaterial = new PhongMaterial();
        greyMaterial.setDiffuseColor(Color.DARKGREY);
        greyMaterial.setSpecularColor(Color.GREY);

        // Molecule Hierarchy
        // [*] moleculeXform
        //     [*] oxygenXform
        //         [*] oxygenSphere
        //     [*] hydrogen1SideXform
        //         [*] hydrogen1Xform
        //             [*] hydrogen1Sphere
        //         [*] bond1Cylinder
        //     [*] hydrogen2SideXform
        //         [*] hydrogen2Xform
        //             [*] hydrogen2Sphere
        //         [*] bond2Cylinder

        Xform moleculeXform = new Xform();
        Xform oxygenXform = new Xform();
        Xform hydrogen1SideXform = new Xform();
        Xform hydrogen1Xform = new Xform();
        Xform hydrogen2SideXform = new Xform();
        Xform hydrogen2Xform = new Xform();

        Sphere oxygenSphere = new Sphere(40.0);
        oxygenSphere.setMaterial(redMaterial);

        Sphere hydrogen1Sphere = new Sphere(30.0);
        hydrogen1Sphere.setMaterial(whiteMaterial);
        hydrogen1Sphere.setTranslateX(0.0);

        Sphere hydrogen2Sphere = new Sphere(30.0);
        hydrogen2Sphere.setMaterial(whiteMaterial);
        hydrogen2Sphere.setTranslateZ(0.0);

        Cylinder bond1Cylinder = new Cylinder(5, 100);
        bond1Cylinder.setMaterial(greyMaterial);
        bond1Cylinder.setTranslateX(50.0);
        bond1Cylinder.setRotationAxis(Rotate.Z_AXIS);
        bond1Cylinder.setRotate(90.0);

        Cylinder bond2Cylinder = new Cylinder(5, 100);
        bond2Cylinder.setMaterial(greyMaterial);
        bond2Cylinder.setTranslateX(50.0);
        bond2Cylinder.setRotationAxis(Rotate.Z_AXIS);
        bond2Cylinder.setRotate(90.0);

        moleculeXform.getChildren().add(oxygenXform);
        moleculeXform.getChildren().add(hydrogen1SideXform);
        moleculeXform.getChildren().add(hydrogen2SideXform);
        oxygenXform.getChildren().add(oxygenSphere);
        hydrogen1SideXform.getChildren().add(hydrogen1Xform);
        hydrogen2SideXform.getChildren().add(hydrogen2Xform);
        hydrogen1Xform.getChildren().add(hydrogen1Sphere);
        hydrogen2Xform.getChildren().add(hydrogen2Sphere);
        hydrogen1SideXform.getChildren().add(bond1Cylinder);
        hydrogen2SideXform.getChildren().add(bond2Cylinder);

        hydrogen1Xform.setTx(100.0);
        hydrogen2Xform.setTx(100.0);
        hydrogen2SideXform.setRotateY(HYDROGEN_ANGLE);

        moleculeGroup.getChildren().add(moleculeXform);

        world.getChildren().addAll(moleculeGroup);
    }

    /*
 * Copyright (c) 2013, 2014 Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

//
// The handleMouse() method is used in the MoleculeSampleApp application to
// handle the different 3D camera views.
// This method is used in the Getting Started with JavaFX 3D Graphics tutorial.
//

    private void handleMouse(Scene scene) {

        scene.setOnMousePressed(me -> {
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            mouseOldX = me.getSceneX();
            mouseOldY = me.getSceneY();
        });
        scene.setOnMouseDragged(me -> {
            mouseOldX = mousePosX;
            mouseOldY = mousePosY;
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            mouseDeltaX = (mousePosX - mouseOldX);
            mouseDeltaY = (mousePosY - mouseOldY);
            double modifierFactor = 1.0;

            double modifier = 1.0;

            if (me.isControlDown()) {
                modifier = CONTROL_MULTIPLIER;
            }
            if (me.isShiftDown()) {
                modifier = SHIFT_MULTIPLIER;
            }
            if (me.isPrimaryButtonDown()) {
                cameraXform.ry.setAngle(cameraXform.ry.getAngle() -
                        mouseDeltaX * modifierFactor * modifier * ROTATION_SPEED);  //
                cameraXform.rx.setAngle(cameraXform.rx.getAngle() +
                        mouseDeltaY * modifierFactor * modifier * ROTATION_SPEED);  // -
            } else if (me.isMiddleButtonDown()) {
                cameraXform2.t.setX(cameraXform2.t.getX() +
                        mouseDeltaX * MOUSE_SPEED * modifier * TRACK_SPEED);  // -
                cameraXform2.t.setY(cameraXform2.t.getY() +
                        mouseDeltaY * MOUSE_SPEED * modifier * TRACK_SPEED);  // -
            }
        }); // setOnMouseDragged
        scene.setOnScroll(se -> {
            double modifier = 1.0;
            if (se.isControlDown()) {
                modifier = CONTROL_MULTIPLIER;
            }
            if (se.isShiftDown()) {
                modifier = SHIFT_MULTIPLIER;
            }
            double z = camera.getTranslateZ();
            double newZ = z + MOUSE_SPEED * se.getDeltaY() * modifier;
            camera.setTranslateZ(newZ);
        });
    } //handleMouse

    //
// The handleKeyboard() method is used in the MoleculeSampleApp application to
// handle the different 3D camera views.
// This method is used in the Getting Started with JavaFX 3D Graphics tutorial.
//

    private void handleKeyboard(Scene scene, final Node root) {

        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case Z:
                    cameraXform2.t.setX(0.0);
                    cameraXform2.t.setY(0.0);
                    cameraXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
                    cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
                    break;
                case X:
                    axisGroup.setVisible(!axisGroup.isVisible());
                    break;
                case V:
                    moleculeGroup.setVisible(!moleculeGroup.isVisible());
                    break;
            } // switch
        });  // setOnKeyPressed
    }  //  handleKeyboard()

    private Map<Finger.Type, Sphere> leftHandsFingerTips = new HashMap<>();
    private Map<Finger.Type, Sphere> rightHandsFingerTips = new HashMap<>();

    private void buildHands() {
        final PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(Color.DARKRED);
        redMaterial.setSpecularColor(Color.RED);

        final PhongMaterial whiteMaterial = new PhongMaterial();
        whiteMaterial.setDiffuseColor(Color.WHITE);
        whiteMaterial.setSpecularColor(Color.LIGHTBLUE);

        final PhongMaterial greyMaterial = new PhongMaterial();
        greyMaterial.setDiffuseColor(Color.DARKGREY);
        greyMaterial.setSpecularColor(Color.GREY);

        final PhongMaterial greenMaterial = new PhongMaterial();
        greenMaterial.setDiffuseColor(Color.DARKGREEN);
        greenMaterial.setSpecularColor(Color.GREEN);

        final PhongMaterial blueMaterial = new PhongMaterial();
        blueMaterial.setDiffuseColor(Color.DARKBLUE);
        blueMaterial.setSpecularColor(Color.BLUE);


        Sphere finger = new Sphere(10);
        finger.setTranslateX(0);
        finger.setTranslateZ(5);
        finger.setMaterial(redMaterial);
        leftHandsFingerTips.put(Finger.Type.TYPE_THUMB, finger);
        finger = new Sphere(10);
        finger.setTranslateX(5);
        finger.setTranslateZ(0);
        finger.setMaterial(blueMaterial);
        leftHandsFingerTips.put(Finger.Type.TYPE_INDEX, finger);
        finger = new Sphere(10);
        finger.setTranslateX(0);
        finger.setTranslateZ(25);
        finger.setMaterial(greenMaterial);
        leftHandsFingerTips.put(Finger.Type.TYPE_MIDDLE, finger);
        finger = new Sphere(10);
        finger.setTranslateX(0);
        finger.setTranslateZ(20);
        finger.setMaterial(greyMaterial);
        leftHandsFingerTips.put(Finger.Type.TYPE_RING, finger);
        finger = new Sphere(10);
        finger.setTranslateX(0);
        finger.setTranslateZ(15);
        finger.setMaterial(whiteMaterial);
        leftHandsFingerTips.put(Finger.Type.TYPE_PINKY, finger);

        leftHandsFingerTips.forEach((type, sphere) -> {
            world.getChildren().add(sphere);
            sphere.setRadius(20);
        });

        finger = new Sphere(10);
        finger.setTranslateX(0);
        finger.setTranslateZ(5);
        finger.setMaterial(redMaterial);
        rightHandsFingerTips.put(Finger.Type.TYPE_THUMB, finger);
        finger = new Sphere(10);
        finger.setTranslateX(0);
        finger.setTranslateZ(20);
        finger.setMaterial(blueMaterial);
        rightHandsFingerTips.put(Finger.Type.TYPE_INDEX, finger);
        finger = new Sphere(10);
        finger.setTranslateX(0);
        finger.setTranslateZ(25);
        finger.setMaterial(greenMaterial);
        rightHandsFingerTips.put(Finger.Type.TYPE_MIDDLE, finger);
        finger = new Sphere(10);
        finger.setTranslateX(0);
        finger.setTranslateZ(20);
        finger.setMaterial(greyMaterial);
        rightHandsFingerTips.put(Finger.Type.TYPE_RING, finger);
        finger = new Sphere(10);
        finger.setTranslateX(0);
        finger.setTranslateZ(15);
        finger.setMaterial(whiteMaterial);
        rightHandsFingerTips.put(Finger.Type.TYPE_PINKY, finger);
        rightHandsFingerTips.forEach((type, xform1) -> world.getChildren().add(xform1));
    }

    @Override
    public void start(Stage primaryStage) {
        buildCamera();
        buildAxes();
        //buildMolecule();
        buildHands();
        // Create a Box
        root.getChildren().add(world);
        Scene scene = new Scene(root, 1024, 768, true);
        scene.setFill(Color.GREY);
        handleKeyboard(scene, world);
        handleMouse(scene);
        primaryStage.setTitle("Molecule Sample Application");
        primaryStage.setScene(scene);
        primaryStage.show();
        scene.setCamera(camera);

        Thread t = new Thread() {
            @Override
            public void run() {
                listener = new SampleListener();
                controller = new Controller();

                // Have the sample listener receive events from the controller
                controller.addListener(listener);
            }
        };

        t.start();

        // Remove the sample listener when done
        //controller.removeListener(listener);
        //primaryStage.close();
    }

    class SampleListener extends Listener {
        public void onInit(Controller controller) {
            System.out.println("Initialized");
        }

        public void onConnect(Controller controller) {
            System.out.println("Connected");
        }

        public void onDisconnect(Controller controller) {
            //Note: not dispatched when running in a debugger.
            System.out.println("Disconnected");
        }

        public void onExit(Controller controller) {
            System.out.println("Exited");
        }

        public void onFrame(Controller controller) {
            // Get the most recent frame and report some basic information
            Frame frame = controller.frame();
            /*System.out.println("Frame id: " + frame.id()
                    + ", timestamp: " + frame.timestamp()
                    + ", hands: " + frame.hands().count()
                    + ", fingers: " + frame.fingers().count()); */

            //Get hands
            for (Hand hand : frame.hands()) {
                String handType = hand.isLeft() ? "Left hand" : "Right hand";
                /*System.out.println("  " + handType + ", id: " + hand.id()
                        + ", palm position: " + hand.palmPosition()); */

                // Get the hand's normal vector and direction
                Vector normal = hand.palmNormal();
                Vector direction = hand.direction();
                Vector origin = hand.palmPosition();


                //System.out.println(origin.getX());
                // Calculate the hand's pitch, roll, and yaw angles
                /*System.out.println("  pitch: " + Math.toDegrees(direction.pitch()) + " degrees, "
                        + "roll: " + Math.toDegrees(normal.roll()) + " degrees, "
                        + "yaw: " + Math.toDegrees(direction.yaw()) + " degrees"); */

                // Get arm bone
                Arm arm = hand.arm();
               /* System.out.println("  Arm direction: " + arm.direction()
                        + ", wrist position: " + arm.wristPosition()
                        + ", elbow position: " + arm.elbowPosition()); */

                // Get fingers
                for (Finger finger : hand.fingers()) {

                    /*System.out.println("    " + finger.type() + ", id: " + finger.id()
                            + ", length: " + finger.length()
                            + "mm, width: " + finger.width() + "mm"); */
                    /*sp = new Sphere(3.0);
                    Vector tipPosition = finger.tipPosition();
                    sp.setTranslateX(tipPosition.getX());
                    sp.setTranslateY(tipPosition.getY());
                    sp.setTranslateZ(tipPosition.getZ());
                    world.getChildren().add(sp); */
                    final Sphere fingerSphere;
                    if (hand.isLeft()) {
                        fingerSphere = leftHandsFingerTips.get(finger.type());
                    } else {
                        fingerSphere = rightHandsFingerTips.get(finger.type());
                    }

                    final double deltaX, deltaY, deltaZ;
                    Vector tipPosition = finger.tipPosition();
                    //System.out.println("x: "+tipPosition.getX() + " y: "+tipPosition.getY());
                    deltaX = tipPosition.getX() / 10 - fingerSphere.getTranslateX();
                    deltaY = tipPosition.getY() / 10 - fingerSphere.getTranslateY();
                    deltaZ = tipPosition.getZ() / 10 - fingerSphere.getTranslateZ();
                    Platform.runLater(() -> fingerSphere.getTransforms().add(new Translate(deltaX, deltaY,deltaZ)));
                    fingerSphere.setTranslateX(fingerSphere.getTranslateX() + deltaX);
                    fingerSphere.setTranslateY(fingerSphere.getTranslateY() + deltaY);
                    fingerSphere.setTranslateZ(fingerSphere.getTranslateZ() + deltaZ);
                    //Get Bones
                    for (Bone.Type boneType : Bone.Type.values()) {
                        Bone bone = finger.bone(boneType);
                        /*System.out.println("      " + bone.type()
                                + " bone, start: " + bone.prevJoint()
                                + ", end: " + bone.nextJoint()
                                + ", direction: " + bone.direction()); */
                    }
                }
            }

            if (!frame.hands().isEmpty()) {
                System.out.println();
            }
        }

    }

    /**
     * The main() method is ignored in correctly deployed JavaFX
     * application. main() serves only as fallback in case the
     * application can not be launched through deployment artifacts,
     * e.g., in IDEs with limited FX support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}