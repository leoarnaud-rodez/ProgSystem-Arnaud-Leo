/*
 * TestRunner.java                                          25 spet. 2026
 * Fichier de tests pour valider les étapes du TP VFS
 */

public class TestRunner {

    public static void main(String[] args) {
        System.out.println("=== DÉBUT DES TESTS DU VFS ===");

        // Étape 2
        testStep2();

        // Étape 3
        testStep3();

        // Étape 4
        testStep4();

        // Étape 5
        testStep5();

        System.out.println("\n[SUCCÈS] Tous les tests activés ont été validés avec succès !");
    }

    public static void testStep2() {
		System.out.println("=== TEST ÉTAPE 2 : Utils Entiers ===");

		byte[] buffer = new byte[32];

		int value = 0xF0A1B2E3;
		int written = Utils.writeInt(buffer, 3, value);

		assert written == 4 : "writeInt doit retourner 4";

		assert (buffer[3]  & 0xFF) == 0xF0 : "Octet 0 incorrect";
		assert (buffer[4]  & 0xFF) == 0xA1 : "Octet 1 incorrect";
		assert (buffer[5]  & 0xFF) == 0xB2 : "Octet 2 incorrect";
		assert (buffer[6]  & 0xFF) == 0xE3 : "Octet 3 incorrect";

		assert Utils.readInt(buffer, 3) == value :
				"Erreur writeInt / readInt";

		short shortValue = (short) 0xF0A1;
		int shortWritten = Utils.writeShort(buffer, 20, shortValue);

		assert shortWritten == 2 : "writeShort doit retourner 2";

		assert (buffer[20] & 0xFF) == 0xF0 :
				"Premier octet du short incorrect";

		assert (buffer[21] & 0xFF) == 0xA1 :
				"Deuxième octet du short incorrect";

		assert Utils.readShort(buffer, 20) == shortValue :
				"Erreur writeShort / readShort";

		System.out.println("[OK] Étape 2 validée !");
	}

    public static void testStep3() {
		System.out.println("=== TEST ÉTAPE 3 : Utils Long & String ===");

		byte[] buffer = new byte[64];

		long value = 0x1122334455667788L;

		int written = Utils.writeLong(buffer, 0, value);

		assert written == 8 : "writeLong doit retourner 8";

		assert (buffer[0] & 0xFF) == 0x11;
		assert (buffer[1] & 0xFF) == 0x22;
		assert (buffer[2] & 0xFF) == 0x33;
		assert (buffer[3] & 0xFF) == 0x44;
		assert (buffer[4] & 0xFF) == 0x55;
		assert (buffer[5] & 0xFF) == 0x66;
		assert (buffer[6] & 0xFF) == 0x77;
		assert (buffer[7] & 0xFF) == 0x88;

		assert Utils.readLong(buffer, 0) == value :
				"Erreur writeLong / readLong";

		for (int i = 16; i < 32; i++) {
			buffer[i] = (byte) 0x7F;
		}

		int stringWritten =
				Utils.writeString(buffer, 16, "MYFS", 16);

		assert stringWritten == 16 :
				"writeString doit retourner maxLength";

		assert (buffer[16] & 0xFF) == 'M';
		assert (buffer[17] & 0xFF) == 'Y';
		assert (buffer[18] & 0xFF) == 'F';
		assert (buffer[19] & 0xFF) == 'S';

		for (int i = 20; i < 32; i++) {
			assert buffer[i] == 0 :
					"La zone inutilisée doit être nettoyée";
		}

		assert Utils.readString(buffer, 16, 16).equals("MYFS") :
				"Erreur writeString / readString";

		System.out.println("[OK] Étape 3 validée !");
	}


    public static void testStep4() {
		System.out.println("=== TEST ÉTAPE 4 : Initialisation Mémoire ===");

		MemoryManager mm = new MemoryManager();

		byte[] mem = mm.getFilesystemMemory();

		assert mem != null :
				"La mémoire ne doit pas être nulle";

		assert mem.length == MemoryManager.TOTAL_MEMORY :
				"Taille mémoire incorrecte";

		assert Utils.readString(
				mem,
				MemoryManager.SUPERBLOCK_OFFSET,
				16).equals("MYFS1.0") :
				"Signature du superbloc incorrecte";

		assert Utils.readInt(
				mem,
				MemoryManager.SUPERBLOCK_OFFSET + 16)
				== MemoryManager.BLOCK_SIZE :
				"Taille de bloc incorrecte";

		assert Utils.readInt(
				mem,
				MemoryManager.SUPERBLOCK_OFFSET + 20)
				== MemoryManager.TOTAL_MEMORY :
				"Taille mémoire incorrecte";

		assert Utils.readInt(
				mem,
				MemoryManager.SUPERBLOCK_OFFSET + 24)
				== MemoryManager.NUM_BLOCKS :
				"Nombre de blocs incorrect";

		assert Utils.readInt(
				mem,
				MemoryManager.SUPERBLOCK_OFFSET + 28)
				== MemoryManager.MAX_INODES :
				"Nombre maximal d'inodes incorrect";

		System.out.println("[OK] Étape 4 validée !");
	}

	public static void testStep5() {
		System.out.println("--- Test Étape 5 : Gestion du Bitmap ---");
		MemoryManager mm = new MemoryManager();

		// 1. Vérification des blocs système réservés (0 à 128)
		boolean systemBlocksOk = true;
		for (int i = 0; i <= 128; i++) {
			if (mm.isBlockUsed(i) != 1) {
				systemBlocksOk = false;
				break;
			}
		}
		System.out.println("Blocs système réservés (0-128) : " + (systemBlocksOk ? "OK" : "ERREUR"));

		// 2. Vérification que le premier bloc de données (129) est libre au départ
		System.out.println("Bloc 129 libre au départ : " + (mm.isBlockUsed(129) == 0 ? "OK" : "ERREUR"));

		// 3. Allocation d'un bloc de données
		int allocated1 = mm.allocateBlock();
		System.out.println("Allocation bloc 1 (attendu 129) : " + allocated1 + " -> " + (allocated1 == 129 ? "OK" : "ERREUR"));
		System.out.println("Bloc 129 marqué comme occupé : " + (mm.isBlockUsed(129) == 1 ? "OK" : "ERREUR"));

		// 4. Deuxième allocation
		int allocated2 = mm.allocateBlock();
		System.out.println("Allocation bloc 2 (attendu 130) : " + allocated2 + " -> " + (allocated2 == 130 ? "OK" : "ERREUR"));

		// 5. Libération du bloc 129
		mm.setBlockUsed(129, false);
		System.out.println("Bloc 129 libéré : " + (mm.isBlockUsed(129) == 0 ? "OK" : "ERREUR"));

		// 6. Réallocation (doit reprendre le premier bloc disponible, soit 129)
		int allocated3 = mm.allocateBlock();
		System.out.println("Réallocation (attendu 129) : " + allocated3 + " -> " + (allocated3 == 129 ? "OK" : "ERREUR"));

		// 7. Vérification de la gestion des hors-bornes
		boolean invalidOk = (mm.isBlockUsed(-1) == -1) && (mm.isBlockUsed(MemoryManager.NUM_BLOCKS) == -1);
		System.out.println("Gestion des indices invalides : " + (invalidOk ? "OK" : "ERREUR"));
		System.out.println();
	}

	/**
     * Test de l'Étape 6 : Lecture de la structure Inode.
     *
     * @author Léo Arnaud
     */
    public static void testStep6() {
        System.out.println("--- Test Étape 6 : Lecture Inode ---");
        MemoryManager memoryManager = new MemoryManager();
        byte[] memory = memoryManager.getFilesystemMemory();

        // L'inode 0 se trouve à l'offset 1024 (2 * 512).
        int inodeOffset = MemoryManager.INODE_TABLE_OFFSET;

        // Écriture manuelle de métadonnées pour le test.
        Utils.writeInt(memory, inodeOffset + 0, 0);       // Numéro inode = 0.
        Utils.writeInt(memory, inodeOffset + 4, 1);       // FileType = 1.
        Utils.writeInt(memory, inodeOffset + 8, 2048);    // FileSize = 2048 octets.
        Utils.writeLong(memory, inodeOffset + 12, 1000L); // Date création.
        Utils.writeLong(memory, inodeOffset + 20, 2000L); // Date modification.

        // Premier pointeur direct = bloc 129.
        Utils.writeInt(memory, inodeOffset + 28, 129);

        // Lecture via l'objet Inode.
        Inode inode = new Inode(memoryManager, 0);

        boolean offsetOk = (inode.getInodeOffset() == 1024);
        boolean typeOk = (inode.getFileType() == 1);
        boolean sizeOk = (inode.getFileSize() == 2048);
        boolean createOk = (inode.getCreationTime() == 1000L);
        boolean modifOk = (inode.getModificationTime() == 2000L);
        boolean pointerOk = (inode.getDirectPointers()[0] == 129);

        System.out.println("Calcul offset (1024) : " + (offsetOk ? "OK" : "ERREUR"));
        System.out.println("Type de fichier (1) : " + (typeOk ? "OK" : "ERREUR"));
        System.out.println("Taille de fichier (2048) : " + (sizeOk ? "OK" : "ERREUR"));
        System.out.println("Dates création/modif : " + (createOk && modifOk ? "OK" : "ERREUR"));
        System.out.println("Pointeur direct 0 (129) : " + (pointerOk ? "OK" : "ERREUR"));
        System.out.println();
    }

}