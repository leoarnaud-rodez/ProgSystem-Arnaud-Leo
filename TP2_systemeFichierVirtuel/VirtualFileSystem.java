/*
 * VirtualFileSystem.java                       5 oct. 2026
 * IUT de Rodez, pas de copyright (ni de "copyleft")
 */

import java.util.*;

/**
 * Système de fichiers virtuel gérant l'allocation des inodes
 * et la création de fichiers
 *
 * @author Léo Arnaud
 */
public class VirtualFileSystem {

    private MemoryManager memoryManager;

    public VirtualFileSystem() {
        this.memoryManager =
                new MemoryManager();
    }

    private int allocateInode() {

        byte[] memory =
                memoryManager.getFilesystemMemory();

        for (int indexInode = 0; indexInode < MemoryManager.MAX_INODES; indexInode++) {
            Inode inode = new Inode(memoryManager, indexInode);

            // Identifier le premier inode libre (type 0)
            if (inode.getFileType() == 0) {
                return indexInode;
            }
        }

        return -1;
    }

    public boolean createFile(
            String directory,
            String filename) {

        int inodeNum = allocateInode();

        if (inodeNum == -1) {
            return false;
        }

        Inode inode = new Inode(memoryManager, inodeNum);

        // L'initialiser comme fichier vide
        int[] directPointers = new int[Inode.DIRECT_POINTERS];
        inode.writeToMemory(
                1, // Type 1 : Fichier
                0, // Taille : 0
                0L, // Date création
                0L, // Date modification
                directPointers, // Pointeurs directs vides
                0, // Pointeur indirect
                (short) 0, // Permissions
                1 // Nombre de liens
        );

        return true;
    }

    /**
     * Écrit les données dans le fichier représenté par l'inode spécifié
     * 
     * @param inodeNum
     * @param data
     * @return true si l'écriture a réussi false sinon
     */
    public boolean writeFile(
            int inodeNum,
            byte[] data) {

        int blocksNeeded =
                (data.length
                + MemoryManager.BLOCK_SIZE - 1)
                / MemoryManager.BLOCK_SIZE;

        if (blocksNeeded > Inode.DIRECT_POINTERS) {
            return false;
        }

        int[] blockPointers =
                new int[Inode.DIRECT_POINTERS];

        // Allouer blocksNeeded blocs.
        for (int i = 0; i < blocksNeeded; i++) {
            int allocatedBlock = memoryManager.allocateBlock();
            if (allocatedBlock == -1) {
                return false;
            }
            blockPointers[i] = allocatedBlock;
        }

        byte[] memory =
                memoryManager.getFilesystemMemory();

        int bytesRemaining =
                data.length;

        int dataSrcOffset = 0;

        // Pour chaque bloc :
        // - calculer la quantité à copier ;
        // - récupérer le numéro du bloc ;
        // - calculer son offset physique ;
        // - copier les données.
        for (int i = 0; i < blocksNeeded; i++) {
            int copyLength = Math.min(MemoryManager.BLOCK_SIZE, bytesRemaining);
            int blockNumber = blockPointers[i];
            int physicalOffset = blockNumber * MemoryManager.BLOCK_SIZE;

            System.arraycopy(data, dataSrcOffset, memory, physicalOffset, copyLength);

            dataSrcOffset += copyLength;
            bytesRemaining -= copyLength;
        }

        // Mettre à jour l'inode.
        Inode inode = new Inode(memoryManager, inodeNum);
        inode.writeToMemory(
                1,
                data.length,
                inode.getCreationTime(),
                0L,
                blockPointers,
                inode.getIndirectPointer(),
                inode.getPermissions(),
                inode.getLinkCount()
        );

        return true;
    }

    public byte[] readFile(int inodeNum) {

        Inode inode =
                new Inode(memoryManager, inodeNum);

        int fileSize =
                inode.getFileSize();

        if (fileSize == 0) {
            return new byte[0];
        }

        byte[] fileData =
                new byte[fileSize];

        byte[] memory =
                memoryManager.getFilesystemMemory();

        int[] blockPointers =
                inode.getDirectPointers();

        // Parcourir les blocs utilisés.
        // Copier chaque fragment vers fileData.
        int bytesRead = 0;
        int blockIndex = 0;

        while (bytesRead < fileSize && blockIndex < Inode.DIRECT_POINTERS) {
            int bytesToRead = Math.min(MemoryManager.BLOCK_SIZE, fileSize - bytesRead);
            int blockNumber = blockPointers[blockIndex];

            if (blockNumber == 0) {
                break;
            }

            int physicalOffset = blockNumber * MemoryManager.BLOCK_SIZE;
            System.arraycopy(memory, physicalOffset, fileData, bytesRead, bytesToRead);

            bytesRead += bytesToRead;
            blockIndex++;
        }

        return fileData;
    }

    public MemoryManager getMemoryManager() {
        return memoryManager;
    }

    /**
     * Supprime un fichier en libérant ses blocs de données dans le bitmap
     * et en réinitialisant son inode
     *
     * @param inodeNum le numéro de l'inode du fichier à supprimer
     * @return true si la suppression a réussi false si l'inode est pas bonne
     */
    public boolean deleteFile(int inodeNum) {
        if (inodeNum < 0 || inodeNum >= MemoryManager.MAX_INODES) {
            return false;
        }

        Inode inode = new Inode(memoryManager, inodeNum);

        // Si l'inode est déjà libre, rien à supprimer
        if (inode.getFileType() == 0) {
            return false;
        }

        int[] pointers = inode.getDirectPointers();

        // 1. Libération des blocs physiques dans le bitmap
        for (int blockNumber : pointers) {
            if (blockNumber != 0) {
                memoryManager.setBlockUsed(blockNumber, false);
            }
        }

        // 2. Réinitialisation des métadonnées de l'inode (type 0 = libre)
        int[] emptyPointers = new int[Inode.DIRECT_POINTERS];
        inode.writeToMemory(
                0,
                0,
                0L,
                0L,
                emptyPointers,
                0,
                (short) 0,
                0
        );

        return true;
    }
}