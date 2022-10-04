package waterpunch.atamamozi_d.plugin.tool;

public class Quaternion {
	public double w, x, y, z;

	public Quaternion(double w, double x, double y, double z) {
		this.w = w;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public static Quaternion Multiply(Quaternion u, Quaternion v) {
		double w, x, y, z;
		w = u.w * v.w - u.x * v.x - u.y * v.y - u.z * v.z;
		x = u.w * v.x + u.x * v.w + u.y * v.z - u.z * v.y;
		y = u.w * v.y - u.x * v.z + u.y * v.w + u.z * v.x;
		z = u.w * v.z + u.x * v.y - u.y * v.x + u.z * v.w;
		return new Quaternion(w, x, y, z);
	}

}
