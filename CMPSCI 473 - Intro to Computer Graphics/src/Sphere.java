// Sphere class
// defines a Sphere shape

import javax.vecmath.*;

public class Sphere extends Shape {
	private Vector3f center;	// center of sphere
	private float radius;		// radius of sphere

	public Sphere() {
	}
	public Sphere(Vector3f pos, float r, Material mat) {
		center = new Vector3f(pos);
		radius = r;
		material = mat;
	}

	public float dot_product(float[] a, float[] b) {
		float ret = 0.0f;
		for (int i = 0; i < a.length; i++) {
			ret += a[i] * b[i];
		}
		return ret;
	}
	public Vector3f calc_normal(Vector3f p, Vector3f c){
		Vector3f base = new Vector3f(p);
		base.sub(c);
		float denom = (float)1/base.length();
		if (denom != 0.0f) {
			base.scale(denom);
			return base;
		}
		return null;
	}
	public HitRecord hit(Ray ray, float tmin, float tmax) {
		/* compute ray-plane intersection */
		float t = 0.0f;
		Vector3f temp = new Vector3f(ray.getOrigin()); temp.sub(center);
		float A = ray.getDirection().lengthSquared();
		float B = 2.0f * temp.dot(ray.getDirection());
		float C = temp.lengthSquared()-(float)Math.pow(radius, 2);
		float discrim = (float)(Math.pow(B,2)-(4*A*C));
		
		if(discrim < 0) return null;
		if(discrim == 0) return null; //tangential collisions ignored
		if(discrim > 0){
			float t1 = (-B - discrim)/(2*A);
			if (t1 < tmin || t1 > tmax){
				t1 = (-B + discrim)/(2*A);
			}
			float t2 = C/(A*t1);
			if(t2 < t1 && t2 > 0){
				t = t2;
			}
			if(t1 < t2 && t1 > 0){
				t = t1;
			}
		}
		/* if t out of range, return null */
		if (t <= tmin || t >= tmax)	return null;
		/* construct hit record */
		HitRecord rec = new HitRecord();
		rec.setShape(this);
		rec.ray_intersects_something = true;
		rec.pos = ray.pointAt(t);		// position of hit point
		rec.t = t;						// parameter t (distance along the ray)
		rec.material = material;		// material
		rec.normal =calc_normal(rec.pos,center);					// normal at the hit point
		rec.normal.normalize();			// normal should be normalized
		
		return rec;
	}
}
