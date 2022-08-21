package lol.j0.modulus;

public class ModulusMath {



	public static int average(int[] a) {
		int res=0;
		for (int n : a) res += n;
		return res/a.length;
	}

	public static Float average(Float[] a) {
		Float res=0f;
		for (Float n : a) res += n;
		return res/a.length;
	}
}
