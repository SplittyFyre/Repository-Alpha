package scene.entities;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class Camera {
	
	private static final float FOV = 70;
	private static final float NEAR_PLANE = 1f;
	private static final float FAR_PLANE = 5000;
	private static final float FARTHER_PLANE = 10000;
	
	private float distanceFrom = 50;
	private float angleAround = 0;
	private float pitch = 20;
	private float yaw;
	private float roll;
	
	private Vector3f position = new Vector3f(100, 35, 50);
	private Entity player;
	
	public Camera(Entity player) {
		this.player = player;
	}
	
	public void invertPitch() {
		this.pitch = -pitch;
	}
	
	public void move() {
		calculateAngleAround();
		calculatePitch();
		calculateZoom();
		float horizDistance = calculateHorizDistance();
		float verticDistance = calculateVerticDistance();
		calculateCameraPos(horizDistance, verticDistance);
		this.yaw = 180 - (player.getRotY() + angleAround);
		
	}

	public Vector3f getPosition() {
		return position;
	}

	public float getDistanceFrom() {
		return distanceFrom;
	}

	public void setDistanceFrom(float param) {
		this.distanceFrom = param;
	}
	
	public float getPitch() {
		return pitch;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public float getYaw() {
		return yaw;
	}

	public float getRoll() {
		return roll;
	}
	
	private void calculateCameraPos(float horiz, float vertic) {
		
		float theta = player.getRotY() + angleAround;
		float offsetX = (float) (horiz * Math.sin(Math.toRadians(theta)));
		float offsetZ = (float) (horiz * Math.cos(Math.toRadians(theta)));
		position.x = player.getPosition().x - offsetX;
		position.z = player.getPosition().z - offsetZ;
		position.y = player.getPosition().y + vertic + 3.0f;
		
		
	}
	
	private float calculateHorizDistance() {
		return (float) (distanceFrom * Math.cos(Math.toRadians(pitch)));
	}
	
	private float calculateVerticDistance() {
		return (float) (distanceFrom * Math.sin(Math.toRadians(pitch)));
	}
	
	private void calculateZoom() {
		float zoomLevel = Mouse.getDWheel() * 0.1f;
		distanceFrom -= zoomLevel;
	}
	
	private void calculatePitch() {
		if (Mouse.isButtonDown(1)) {
			float pitchChange = Mouse.getDY() * 0.1f;
			pitch -= pitchChange;
		}
	}
	
	private void calculateAngleAround() {
		if (Mouse.isButtonDown(0)) {
			float angleChange = Mouse.getDX() * 0.3f;
			angleAround -= angleChange;
		}
	}
	
	public void setPitch(float pitch) {
		this.pitch = pitch;
	}
	
	public void setYaw(float yaw) {
		this.yaw = yaw;
	}
	
	public void setRoll(float roll) {
		this.roll = roll;
	}
	
	public static Matrix4f createProjectionMatrix() {
		
		float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2))) * aspectRatio);
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;

		Matrix4f projecMat = new Matrix4f();
		projecMat.m00 = x_scale;
		projecMat.m11 = y_scale;
		projecMat.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
		projecMat.m23 = -1;
		projecMat.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
		projecMat.m33 = 0;
		
		return projecMat;
	}
	
	public static Matrix4f createLargerProjectionMatrix() {
		
		float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2))) * aspectRatio);
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FARTHER_PLANE - NEAR_PLANE;

		Matrix4f projecMat = new Matrix4f();
		projecMat.m00 = x_scale;
		projecMat.m11 = y_scale;
		projecMat.m22 = -((FARTHER_PLANE + NEAR_PLANE) / frustum_length);
		projecMat.m23 = -1;
		projecMat.m32 = -((2 * NEAR_PLANE * FARTHER_PLANE) / frustum_length);
		projecMat.m33 = 0;
		
		return projecMat;
	}

}
