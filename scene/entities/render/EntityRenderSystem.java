package scene.entities.render;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

import renderEngine.MasterRenderSystem;
import renderEngine.models.RawModel;
import renderEngine.models.TexturedModel;
import renderEngine.textures.ModelTexture;
import scene.entities.Camera;
import scene.entities.Entity;
import scene.entities.Light;
import utils.SFMath;

public class EntityRenderSystem {
	
	private EntityShader shader;
	
	public EntityShader getShaderPointer() {
		return shader;
	}
	
	public EntityRenderSystem(Matrix4f projectionMatrix) {
		this.shader = new EntityShader();
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}
	
	public void render(Map<TexturedModel, List<Entity>> entities, 
			float skyR, float skyG, float skyB, List<Light> lights, Camera camera, Vector4f clipPlane) {
		prepare(skyR, skyG, skyB, lights, camera, clipPlane);
		for (TexturedModel model : entities.keySet()) {
			prepareTexturedModel(model);
			List<Entity> batch = entities.get(model);
			for (Entity entity : batch) {
				prepareInstance(entity);
				GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(),
						GL11.GL_UNSIGNED_INT, 0);
			}
			unbindTexturedModel();
		}
		shader.stop();
	}
	
	private void prepare(float skyR, float skyG, float skyB, List<Light> lights, Camera camera, Vector4f clipPlane) {
		shader.start();
		shader.loadClipPlane(clipPlane);
		shader.loadSkyColour(skyR, skyG, skyB);
		shader.loadLights(lights);
		shader.loadViewMatrix(camera);
		shader.connectTextureUnits();
	}
	
	private void prepareTexturedModel(TexturedModel model) {
		
		RawModel rawModel = model.getRawModel();
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		ModelTexture texture = model.getTexture();
		shader.loadNumberOfRows(texture.getNumRows());
		
		if (texture.isTransparent()) 
			MasterRenderSystem.disableFaceCulling();
		
		shader.loadFakeLight(texture.isUseFakeLighting());
		shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());
		shader.loadUseSpecularMap(texture.hasSpecularMap());
		shader.loadBrightDamper(texture.getBrightDamper());
		if (texture.hasSpecularMap()) {
			GL13.glActiveTexture(GL13.GL_TEXTURE1);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getSpecularMap());
		}
	}

	private void unbindTexturedModel() {
		MasterRenderSystem.enableFaceCulling();
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}

	private void prepareInstance(Entity entity) {
		Matrix4f transformationMatrix = SFMath.createTransformationMatrix(entity.getPosition(),
				entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
		shader.loadTransformationMatrix(transformationMatrix);
		shader.loadOffset(entity.getTextureXOffset(), entity.getTextureYOffset());
	}
	
	public void setProjectionMatrix(Matrix4f matrix) {
		shader.start();
		shader.loadProjectionMatrix(matrix);
		shader.stop();
	}

}
