package utils;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import scene.entities.Camera;

public class SFMath {
	
	public static final int SF_DIRECTION_AZIMUTH_RIGHT = -90;
	public static final int SF_DIRECTION_AZIMUTH_LEFT = 90;
	public static final int SF_DIRECTION_AZIMUTH_NEUTRAL = 0;
	
	public static float relativePosShiftX(int direction, float angdeg, float magnitude) {
		float angrad = (float) Math.toRadians(angdeg + direction);
		return (float) (Math.sin(angrad) * magnitude);
	}
	
	public static float relativePosShiftZ(int direction, float angdeg, float magnitude) {
		float angrad = (float) Math.toRadians(angdeg + direction);
		return (float) (Math.cos(angrad) * magnitude);
	}
	
	public static boolean nIsWithin(float n, float min, float max) {
		return n <= max && n >= min;
	}
	
	public static float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {
		float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
		float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
		float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
		float l3 = 1.0f - l1 - l2;
		
		return l1 * p1.y + l2 * p2.y + l3 * p3.y;
	}

	public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale, float rotation) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.scale(new Vector3f(scale.x, scale.y, 1f), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rotation), new Vector3f(0, 0, 1), matrix, matrix);
		return matrix;
	}
	
	public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, Vector3f scale) {
		
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(ry), new Vector3f(0, 1, 0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rz), new Vector3f(0, 0, 1), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rx), new Vector3f(1, 0, 0), matrix, matrix);
		Matrix4f.scale(scale, matrix, matrix);
		
		return matrix;
	}
	
	public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, float scale) {
		
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(ry), new Vector3f(0, 1, 0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rz), new Vector3f(0, 0, 1), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rx), new Vector3f(1, 0, 0), matrix, matrix);
		Matrix4f.scale(new Vector3f(scale, scale, scale), matrix, matrix);
		
		return matrix;
	}
	
	public static Matrix4f createViewMatrix(Camera camera) {
		Matrix4f viewMatrix = new Matrix4f();
		viewMatrix.setIdentity();
		Matrix4f.rotate((float) Math.toRadians(camera.getPitch()), new Vector3f(1, 0, 0), viewMatrix,
				viewMatrix);
		Matrix4f.rotate((float) Math.toRadians(camera.getYaw()), new Vector3f(0, 1, 0), viewMatrix, viewMatrix);
		Vector3f cameraPos = camera.getPosition();
		Vector3f negativeCameraPos = new Vector3f(-cameraPos.x, -cameraPos.y, -cameraPos.z);
		Matrix4f.translate(negativeCameraPos, viewMatrix, viewMatrix);
		
		return viewMatrix;
	}
	
	public static Vector3f rotateToFaceVector(Vector3f rotatingObjectPosition, Vector3f destination) {
		
		Vector3f a = rotatingObjectPosition;
		Vector3f b = destination;
		float dx = b.x - a.x;
		float dy = b.y - a.y;
		float dz = b.z - a.z;
		float requiredRotX = (float) Math.toDegrees(Math.asin(dy / 475));
		float requiredRotY = (float) Math.toDegrees(Math.atan2(dx, dz));
		
		requiredRotY = (requiredRotY < 0) ? requiredRotY + 360 : requiredRotY;
		
		return new Vector3f(requiredRotX, requiredRotY, 0);
	}

}
