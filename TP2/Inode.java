/*
 * Inode.java                                    03 oct. 2026
 * IUT de Rodez, pas de copyright (ni de "copyleft")
 */

/**
 * Représente la structure d'indexation (inode) de 128 octets
 * contenant les métadonnées et les pointeurs de blocs d'un fichier.
 *
 * @author Léo Arnaud
 */
public class Inode {

    public static final int INODE_SIZE = 128;
    public static final int DIRECT_POINTERS = 10;

    private MemoryManager memoryManager;
    private int inodeNumber;

    /**
     * Instancie un objet Inode lié au gestionnaire de mémoire.
     *
     * @param memoryManager gestionnaire de la mémoire virtuelle
     * @param inodeNumber   numéro de l'inode cible
     */
    public Inode(MemoryManager memoryManager, int inodeNumber) {
        this.memoryManager = memoryManager;
        this.inodeNumber = inodeNumber;
    }

    /**
     * Calcule l'offset absolu de cet inode dans le tableau d'octets.
     *
     * @return offset en octets
     */
    public int getInodeOffset() {
        // La table des inodes commence au bloc 2.
        return MemoryManager.INODE_TABLE_OFFSET + (this.inodeNumber * INODE_SIZE);
    }

    /**
     * Lit le type du fichier.
     *
     * @return 0 pour libre, 1 pour fichier, 2 pour dossier
     */
    public int getFileType() {
        byte[] memory = memoryManager.getFilesystemMemory();
        int offset = getInodeOffset();

        // Offset relatif 4.
        return Utils.readInt(memory, offset + 4);
    }

    /**
     * Lit la taille totale du fichier en octets.
     *
     * @return taille en octets
     */
    public int getFileSize() {
        byte[] memory = memoryManager.getFilesystemMemory();
        int offset = getInodeOffset();

        // Offset relatif 8.
        return Utils.readInt(memory, offset + 8);
    }

    /**
     * Lit la date de création du fichier.
     *
     * @return timestamp UNIX en millisecondes
     */
    public long getCreationTime() {
        byte[] memory = memoryManager.getFilesystemMemory();
        int offset = getInodeOffset();

        // Offset relatif 12.
        return Utils.readLong(memory, offset + 12);
    }

    /**
     * Lit la date de dernière modification.
     *
     * @return timestamp UNIX en millisecondes
     */
    public long getModificationTime() {
        byte[] memory = memoryManager.getFilesystemMemory();
        int offset = getInodeOffset();

        // Offset relatif 20.
        return Utils.readLong(memory, offset + 20);
    }

    /**
     * Lit les 10 pointeurs directs vers les blocs de données.
     *
     * @return tableau contenant les numéros des blocs
     */
    public int[] getDirectPointers() {
        byte[] memory = memoryManager.getFilesystemMemory();
        int offset = getInodeOffset();
        int[] pointers = new int[DIRECT_POINTERS];

        // Les pointeurs directs commencent à l'offset relatif 28.
        int cursor = offset + 28;
        for (int index = 0; index < DIRECT_POINTERS; index++) {
            pointers[index] = Utils.readInt(memory, cursor);
            cursor += 4;
        }

        return pointers;
    }

    /**
     * Lit le pointeur indirect simple.
     *
     * @return numéro du bloc d'indirection
     */
    public int getIndirectPointer() {
        byte[] memory = memoryManager.getFilesystemMemory();
        int offset = getInodeOffset();

        // Offset relatif 68.
        return Utils.readInt(memory, offset + 68);
    }

    /**
     * Lit les permissions du fichier.
     *
     * @return masque de permissions
     */
    public short getPermissions() {
        byte[] memory = memoryManager.getFilesystemMemory();
        int offset = getInodeOffset();

        // Offset relatif 72.
        return Utils.readShort(memory, offset + 72);
    }

    /**
     * Lit le nombre de liens matériels référençant cet inode.
     *
     * @return nombre de liens
     */
    public int getLinkCount() {
        byte[] memory = memoryManager.getFilesystemMemory();
        int offset = getInodeOffset();

        // Offset relatif 74.
        return Utils.readInt(memory, offset + 74);
    }
}