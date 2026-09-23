/*
 * Utils.java                                          16 sept. 2026
 * IUT de Rodez, pas de copyright (ni de "copyleft")
 */


public class Utils {

    public static int writeInt(byte[] memory, int offset, int value) {
        // à partir de 'offset', en big-endian.
		
		// piste approximative : byte b3 = (byte)(value0xFF)
		// faire avec les décalages >> ou <<
		// memory[offset] = b3
		
		memory[offset]     = (byte) (value >> 24);
		memory[offset + 1] = (byte) (value >> 16);
		memory[offset + 2] = (byte) (value >> 8);
		memory[offset + 3] = (byte) (value);

		return 4;
    }

    public static int readInt(byte[] memory, int offset) {
		// Reconstitution de l'int sur 4 octet en big-endian
		int b0 = (memory[offset]     & 0xFF) << 24;
		int b1 = (memory[offset + 1] & 0xFF) << 16;
		int b2 = (memory[offset + 2] & 0xFF) << 8;
		int b3 = (memory[offset + 3] & 0xFF);

		// fusion des 4 morceaux avec le "ou"
		return b0 | b1 | b2 | b3;
	}

    public static int writeShort(byte[] memory, int offset, short value) {
		memory[offset]     = (byte) (value >> 8);
		memory[offset + 1] = (byte) (value);
        return 2;
    }

    public static short readShort(byte[] memory, int offset) {
		int b0 = (memory[offset]     & 0xFF) << 8;
		int b1 = (memory[offset + 1] & 0xFF);

		// fusion des 2 morceaux avec le "ou"
		return (short) (b0 | b1);
    }
	
	// partie 2 sur les long et les string
	
	public static int writeLong(byte[] memory, int offset, long value) {
		memory[offset]     = (byte) (value >> 56);
		memory[offset + 1] = (byte) (value >> 48);
		memory[offset + 2] = (byte) (value >> 40);
		memory[offset + 3] = (byte) (value >> 32);
		memory[offset + 4] = (byte) (value >> 24);
		memory[offset + 5] = (byte) (value >> 16);
		memory[offset + 6] = (byte) (value >> 8);
		memory[offset + 7] = (byte) (value);

		return 8;
    }

    public static long readLong(byte[] memory, int offset) {
		// Reconstitution de l'int sur 4 octet en big-endian
		long b0 = (memory[offset]     & 0xFFL) << 56;
		long b1 = ((long) memory[offset + 1] & 0xFFL) << 48;
		long b2 = ((long) memory[offset + 2] & 0xFFL) << 40;
		long b3 = ((long) memory[offset + 3] & 0xFFL) << 32;
		long b4 = ((long) memory[offset + 4] & 0xFFL) << 24;
		long b5 = ((long) memory[offset + 5] & 0xFFL) << 16;
		long b6 = ((long) memory[offset + 6] & 0xFFL) << 8;
		long b7 = ((long) memory[offset + 7] & 0xFFL);

		// fusion des 8 morceaux avec le "ou"
		return b0 | b1 | b2 | b3 | b4 | b5 | b6 | b7;
	}
	
	public static int writeString(byte[] memory, int offset, String str, int maxLength) {
		// TODO :
		// 1. Convertir la chaîne en octets.
		// 2. Copier les octets sans dépasser maxLength.
		// 3. Nettoyer le reste de la zone avec des zéros.

		return maxLength;
	}

	public static String readString(byte[] memory, int offset, int maxLength) {
		// TODO :
		// Lire jusqu'au premier octet nul
		// ou jusqu'à maxLength.

		return "";
	}
}
