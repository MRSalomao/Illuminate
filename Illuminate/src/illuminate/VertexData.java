package illuminate;

public class VertexData 
{
	// Vertex data
	float[] pos    = new float[] {0f, 0f, 0f, 1f};
	float[] normal = new float[] {1f, 1f, 1f};
//	float[] color  = new float[] {1f, 1f, 1f, 1f};
	float[] uv1    = new float[] {0f, 0f};
	float[] uv2    = new float[] {0f, 0f};
	
	// The amount of bytes an element has
	public static final int floatSize = 4;
	
	// Elements per parameter
	public static final int positionElementCount = 4;
	public static final int normalElementCount   = 3;
//	public static final int colorElementCount    = 4;
	public static final int texture1ElementCount = 2;
	public static final int texture2ElementCount = 2;
	
	// Bytes per parameter
	public static final int positionBytesCount = positionElementCount * floatSize;
	public static final int normalBytesCount   = normalElementCount   * floatSize;
//	public static final int colorByteCount     = colorElementCount    * elementBytes;
	public static final int texture1ByteCount  = texture1ElementCount * floatSize;
	public static final int texture2ByteCount  = texture2ElementCount * floatSize;
	
	// Byte offsets per parameter
	public static final int positionByteOffset = 0;
	public static final int normalByteOffset   = positionByteOffset + positionBytesCount;
//	public static final int colorByteOffset    = normalByteOffset   + normalBytesCount;
	public static final int texture1ByteOffset = normalByteOffset   + normalBytesCount;
	public static final int texture2ByteOffset = texture1ByteOffset + texture1ByteCount;
	
	// The amount of elements that a vertex has
	public static final int elementCount = positionElementCount + normalElementCount /*+ colorElementCount*/ + texture1ElementCount + texture2ElementCount;	
	
	// The size of a vertex in bytes, like in C/C++: sizeof(Vertex)
	public static final int stride = positionBytesCount + normalBytesCount /*+ colorByteCount*/ + texture1ByteCount + texture2ByteCount;

	// Getters	
	public float[] getElements() {
		float[] out = new float[VertexData.elementCount];
		int i = 0;
		
		// Insert XYZW elements
		out[i++] = this.pos[0];
		out[i++] = this.pos[1];
		out[i++] = this.pos[2];
		out[i++] = this.pos[3];
		// Insert RGBA elements
		out[i++] = this.normal[0];
		out[i++] = this.normal[1];
		out[i++] = this.normal[2];
		// Insert UV1 elements
		out[i++] = this.uv1[0];
		out[i++] = this.uv1[1];
		// Insert UV2 elements
		out[i++] = this.uv2[0];
		out[i++] = this.uv2[1];
		
		return out;
	}
}