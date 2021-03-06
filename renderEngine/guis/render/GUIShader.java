package renderEngine.guis.render;

import org.lwjgl.util.vector.Matrix4f;

import renderEngine.ShaderProgram;

public class GUIShader extends ShaderProgram{
	
	private static final String VERTEX_FILE = "src/renderEngine/guis/render/guiVertexShader.txt";
	private static final String FRAGMENT_FILE = "src/renderEngine/guis/render/guiFragmentShader.txt";
	
	private int location_transformationMatrix;

	public GUIShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}
	
	public void loadTransformation(Matrix4f matrix){
		super.loadMatrix(location_transformationMatrix, matrix);
	}

	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}
	
}
