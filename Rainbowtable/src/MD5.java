import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {

	public static char[] Z; // Zeichen Menge
	public static MessageDigest md5;
	// jedes password wird in einem byte array umgewandelt. diese byte array
	// wird benuzt für das updaten von md5 objekt
	public static byte[] data;
	public static TableRow[] rainBowTable;
	private static final int TIMES = 2000;
	private static String baseHash;

	// In main Methode wird den rainbow table erstellt. es wird auch gefragt ein
	// hash wert einzugeben um einen klartext dafür zu suchen
	public static void main(String[] args) {

		Fill_Z();
		try {
			md5 = MessageDigest.getInstance("MD5");
			generateRainbowTable();
			System.out.println();
			System.out.println("Enter hash value :");
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			baseHash = reader.readLine();
			String clearText = findClearText(baseHash);
			if (clearText != null) {
				System.out.println("Clear text is : " + clearText);
			} else {
				System.out.println("No Clear Text found!");
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// Hier wird den rainbow table erstellt und auf dem Konsole ausgegeben
	public static void generateRainbowTable() {
		System.out.println("--------------Rainbow Table------------------");
		rainBowTable = new TableRow[TIMES + 1];
		for (int i = 0; i < TIMES; i++) {
			String w = String.format("%7s", Integer.toString(i, 36)).replace(" ", "0");
			String c = calculateEndValue(w);
			rainBowTable[i] = new TableRow(w, c);
		}

	}

	// Hier wird das endwert von einem gegebenen startwert berechnet
	public static String calculateEndValue(String word) {
		try {
			data = word.getBytes("US-ASCII");
			String hash;
			for (int i = 0; i <= TIMES; i++) {
				hash = Hash(word);
				word = Reduction(hash, String.valueOf(i));
				data = word.getBytes("US-ASCII");
			}
			return word;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	// Die Zeichen Menge wird hier ausgefüllt
	public static void Fill_Z() {
		Z = new char[36];
		for (int i = 0; i < 10; i++) {
			Z[i] = (char) (48 + i);
		}

		for (int i = 0; i < 26; i++) {
			Z[10 + i] = (char) (97 + i);
		}

	}

	// berechnet de Hashwert für ein word
	public static String Hash(String s) {
		md5.update(data);
		byte[] hash = md5.digest();
		StringBuilder stbuild = new StringBuilder("");
		for (int i = 0; i < hash.length; ++i) {

			stbuild.append(String.format("%02x", hash[i]));
		}
		return (stbuild.toString());

	}

	// berechnet das word von einem hashwert in einer gegebenen Stufe
	public static String Reduction(String h, String level) {
		BigInteger val = new BigInteger(h, 16);
		BigInteger lev = new BigInteger(level);
		val = val.add(lev);
		BigInteger mod;
		BigInteger[] divMod = new BigInteger[2];
		StringBuilder stbuild = new StringBuilder("");
		for (int i = 0; i < 7; i++) {
			divMod = val.divideAndRemainder(new BigInteger(String.valueOf(36)));
			mod = divMod[1];
			stbuild.append(Z[mod.intValue()]);
			val = divMod[0];
		}
		stbuild.reverse();
		// System.out.print(stbuild + " ");
		return stbuild.toString();
	}

	// diese ist die Hauptmethode für das überprufen ob für einen gegebenen
	// hashwer ein klartext gibt.
	// Als hilfsmethoden werden die Methoden findEndValue, findStartvalue und
	// findPassword benuzt
	public static String findClearText(String hash) {
		int r = TIMES;
		String endValue = null;
		String startValue = null;
		String clearText = null;

		while (r >= 0 && startValue == null) {
			endValue = findEndValue(hash, r);
			startValue = findStartValue(endValue);
			r--;
		}
		System.out.println("end Value:" + endValue);
		System.out.println("start Value:" + startValue);

		if (startValue != null) {
			clearText = findPassword(startValue, r);
		}
		return clearText;
	}

	// Hier wird die Kette von R un h durchgeführt. ausgehen von einem
	// bestimmten level bis letzten level
	public static String findEndValue(String hash, int level) {
		int lev = level;
		String h = hash;
		String password = null;
		try {
			while (lev <= TIMES) {
				password = Reduction(h, String.valueOf(lev));
				data = password.getBytes("US-ASCII");
				h = Hash(password);
				lev++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return password;
	}

	// hier wird für einen gegebenen endwert den entsprechenden startwert vom
	// Rainbow table gelesen
	public static String findStartValue(String endValue) {
		String startValue = null;
		for (int i = 0; i < TIMES; i++) {
			if (rainBowTable[i].codedWord.equals(endValue)) {
				startValue = rainBowTable[i].word;
			}
		}
		return startValue;
	}

	// Angehend vom start Punkt und bis einen bestimmten level werden die h und
	// R durgefürt.
	// danach wird den Password dort zurückgegeben
	public static String findPassword(String startValue, int level) {
		String password = startValue;
		try {
			data = password.getBytes("US-ASCII");

			String hash = Hash(startValue);
			for (int i = 0; i <= level; i++) {
				hash = Hash(password);
				password = Reduction(hash, String.valueOf(i));
				data = password.getBytes("US-ASCII");

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return password;
	}

	// in jede TableRow gibt es ein startWert und endWert als paar gespeichert.
	private static class TableRow {
		private String word;
		private String codedWord;

		public TableRow(String w, String c) {
			word = w;
			codedWord = c;
		}
	}

};