package samefilebatch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println(" first param should be the directory");
            System.out.println(" second the extension");
            return;
        }
        String dir = args[0];
        String extension = args[1];
        System.out.println(dir + extension);
        File sDir = new File(dir);
        if (!sDir.isDirectory()) {
            System.out.println(" first param should be the directory");
            return;
        }

        if (extension.length() < 1) {
            System.out.println(" second the extension");
            return;
        }

        File vf = new File("gdReport.html");
        Vector fla = getFiles(sDir, extension);
        int bMax = 0;
        bMax = fla.size();
        System.out.println("Processing " + bMax + " files");
        for (int a = bMax - 1; a >= 0; a--) {
            System.out.println(a);
            File fa = (File) fla.get(a);
            if (fa.isFile()) {
                for (int b = 0; b < bMax; b++) {
                    File fb = (File) fla.get(b);
                    if ((fb.isFile()) & (fa.compareTo(fb) != 0)) {
                        try {
                            if (filesAreEqual(fa, fb)) {

                                // move fb naar dubbelle directory
                                File dubbeleDir = new File(sDir, "dubbelle");
                                if (!dubbeleDir.exists()) {
                                    dubbeleDir.mkdir();
                                }
                                moveNaarDubbele(dubbeleDir, fb);
                            }
                        } catch (java.io.FileNotFoundException e) {
                            System.out.println("FileNot FoundException " + e);
                        } catch (java.io.IOException e) {
                            System.out.println("io Exception " + e);
                        }
                    }
                }
            }
        }
    }

    static Vector getFiles(File f, String ex) {
        Vector outFiles = new Vector();
        File[] files = f.listFiles();
//		for(File file:files) {
        for (int k = 0; k < files.length; k++) {
            File file = files[k];
            if (file.isFile()) {
//                if (file.getName ().toLowerCase ().endsWith (".jpg")){
                if (file.getName().toLowerCase().endsWith(ex)) {
                    outFiles.add(file);
                }
            }
        }
        return outFiles;
    }

    static boolean filesAreEqual(File fa, File fb) throws java.io.IOException {
        boolean out = false;
        FileInputStream fisa = new FileInputStream(fa);
        FileInputStream fisb = new FileInputStream(fb);
        int c;
        byte[] byteA;
        byte[] byteB;
        byteA = new byte[30000];
        byteB = new byte[30000];
        fisa.read(byteA);
        fisb.read(byteB);
        if (java.util.Arrays.equals(byteA, byteB)) {
            out = true;
        }
        fisa.close();
        fisb.close();
        return out;
    }

    private static String geefUniekeNaam(File dubbeleDir, File ff) {
        String eruit = null;
        int sourceMax = 0;
        int filenr = 0;
        String extension = "";
        String prefix = "";

        if (ff.isFile() && (ff.canRead())) {
//                System.out.println("moving   " + ff.getAbsolutePath());
            File targetFile;
//                do {

            int extensionIndex = ff.getName().lastIndexOf(".");

            try {
                extension = ff.getName().substring(extensionIndex);
                prefix = ff.getName().substring(0, extensionIndex);
                filenr = bepaalHoogsteFileNr(dubbeleDir, prefix, extension);
                filenr++;
            } catch (java.lang.StringIndexOutOfBoundsException e) {
                extension = "";
            }
//            targetFile = new File(dubbeleDir, gormat(filenr, prefix, extension));
            eruit = gormat(filenr, prefix, extension);
        }

        return (eruit);

    }

    public static boolean isNumeric(String str) {
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    private static int bepaalHoogsteFileNr(File targetDir, String voorvoegsel, String erinextension) {
//        throw new UnsupportedOperationException("Not yet implemented");
        int eruit, hulp;
        eruit = 0;
//        File targetDir = new File(workDir);
        String[] files = targetDir.list();
        int aantal = files.length;
        for (int c = 0; c < aantal; c++) {
            if (files[c].toUpperCase().startsWith(voorvoegsel.toUpperCase()) & files[c].toUpperCase().endsWith(erinextension.toUpperCase())) {
//            hulp = Long.parseLong( files[c]);

                String nummerstring = files[c].substring(voorvoegsel.length());
                nummerstring = nummerstring.substring(0, nummerstring.length() - erinextension.length());
                if (nummerstring.isEmpty()) {
                    eruit = 1;
                } else {
                    if (isNumeric((nummerstring))) {
                        hulp = Integer.parseInt(nummerstring);
                        if (eruit < hulp) {
                            eruit = hulp;
                        }
                    }
                }
            }
        }
        return (eruit);
    }

    private static void moveNaarDubbele(File dubbeleDir, File fb) {
        File targetFile = new File(dubbeleDir, fb.getName());
        String uniqueTargetFileNaam;
        if (!targetFile.exists()) {
            try {
                rename(fb, targetFile);
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
//        geef target file een andere 
            uniqueTargetFileNaam = geefUniekeNaam(dubbeleDir, fb);
            targetFile = new File(dubbeleDir, uniqueTargetFileNaam);
            if (!targetFile.exists()) {
                try {
                    rename(fb, targetFile);
                } catch (IOException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }


            }

        }
    }

    static void rename(File ff, File targetFile) throws IOException {
        copy(ff, targetFile);
        if (targetFile.exists()) {
            if (targetFile.length() == ff.length()) {
                boolean delete = ff.delete();
                if (!delete) {
                    System.out.println("De delete van bronfile is fout gegaan:  " + ff.getAbsolutePath());
                }
            } else {
                System.out.println("Target ile verscilt van bronfile, not gedeleted:  " + ff.getAbsolutePath());

            }
        }


    }

    public static String gormat(int number, String erinPrefix, String erinextension) {
        String eruit = erinPrefix + String.format("%09d", number) + erinextension;
        return eruit;
    }

    public static void copy(File src, File dest) throws IOException {
        InputStream nputStream = null;
        OutputStream utputStream = null;
        try {
            nputStream = new FileInputStream(src);
            utputStream = new FileOutputStream(dest);
            // Transfer bytes from in to out
            byte[] buf = new byte[10 * 1024];
            int len;
            while ((len = nputStream.read(buf)) > 0) {
                utputStream.write(buf, 0, len);
            }
        } catch (FileNotFoundException fnfe) {
            //handle it
        } finally {
            nputStream.close();
            utputStream.close();
        }
    }
}
