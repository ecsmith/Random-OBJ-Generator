// HitRecord class
// defines the structure that store 'hit' information

import javax.vecmath.*;

public class HitRecord
{
	public float t;	// hit point in terms of ray parameter t
	public Vector3f pos = new Vector3f(0,0,0); // hit point
	public Vector3f normal = new Vector3f(0,0,0);	// normal at the hit point
	public Material material;	// material at the hit point
	public boolean ray_intersects_something;
	public Shape shape;
	
	public void set(HitRecord r)
	{
		this.ray_intersects_something = false;
		this.t = r.t;
		this.pos.set(r.pos);
		this.normal.set(r.normal);
		this.material = r.material;
		shape = null;
	}
	public void setShape(Shape s){
		shape = s;
	}
}
