/*
 * Copyright (c) 2006-2007 Creative Sphere Limited.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *
 *   Creative Sphere - initial API and implementation
 *
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

/**
 * Simple test that checks behavior of files
 *
 * @author Daniel Sendula
 */
public class FileTestClass {


    public static void main(String[] args) throws Exception {

        File f1 = new File("/Users/daniel/tempdir/file");
        File f2 = new File("/Users/daniel/tempdir/File");
        System.out.println("f1.createNewFile() = " + f1.createNewFile());
        System.out.println("f1.exists() = " + f1.exists());
        System.out.println("f2.exists() = " + f2.exists());

        System.out.println("f1.equals(f2)=" + f1.equals(f2));
        System.out.println("f1.equals(f2)=" + f1.getCanonicalFile().equals(f2.getCanonicalFile()));
        System.out.println("f1.equals(f2)=" + f1.getCanonicalPath().equals(f2.getCanonicalPath()));

        File file1 = File.createTempFile("TempFile-", ".tmp");

        System.out.println("New file has been created " + file1.getAbsolutePath());

        FileOutputStream out = new FileOutputStream(file1);
        out.write("Some text in file \r\n second line\r\n".getBytes());
        out.close();

        RandomAccessFile raf = new RandomAccessFile(file1, "rw");
        try {
            FileChannel channel = raf.getChannel();
            FileLock lock = channel.tryLock();
            if (lock == null) {
                System.err.println("Failed to lock the file");
            } else {
                System.out.println("Locked the file; valid=" + lock.isValid() + ", shared=" + lock.isShared());
            }
    
            File file2 = new File(file1.getAbsolutePath());
    
            if (file2.delete()) {
                System.err.println("Can delete the file " + file2.getAbsolutePath() + "!?");
            } else {
                System.out.println("Cannot delete the file " + file2.getAbsolutePath());
            }
        } finally {
            raf.close();
        }

        Thread.sleep(300000);


    }

}
