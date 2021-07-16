package org.vincentyeh.IMG2PDF.task.factory;

import org.vincentyeh.IMG2PDF.task.factory.exception.DirListException;
import org.vincentyeh.IMG2PDF.task.factory.exception.EmptyImagesException;
import org.vincentyeh.IMG2PDF.task.factory.exception.SourceFileException;
import org.vincentyeh.IMG2PDF.util.file.FileUtils;
import org.vincentyeh.IMG2PDF.util.file.exception.*;
import org.vincentyeh.IMG2PDF.task.DocumentArgument;
import org.vincentyeh.IMG2PDF.task.PageArgument;
import org.vincentyeh.IMG2PDF.task.Task;
import org.vincentyeh.IMG2PDF.util.interfaces.NameFormatter;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;

public class DirlistTaskFactory {

    private static FileFilter imageFilter;
    private static Comparator<? super File> fileSorter;
    private static DocumentArgument documentArgument;
    private static PageArgument pageArgument;
    private static NameFormatter<File> formatter;

    public static void setArgument(DocumentArgument documentArgument, PageArgument pageArgument, NameFormatter<File> formatter) {
        DirlistTaskFactory.documentArgument = documentArgument;
        DirlistTaskFactory.pageArgument = pageArgument;
        DirlistTaskFactory.formatter = formatter;
    }

    public static void setImageFilesRule(FileFilter imageFilter, Comparator<? super File> fileSorter) {
        DirlistTaskFactory.imageFilter = imageFilter;
        DirlistTaskFactory.fileSorter = fileSorter;
    }

    public static List<Task> createFromDirlist(File dirlist, Charset charset) throws SourceFileException, DirListException {
        if (dirlist == null)
            throw new IllegalArgumentException("dirlist==null");
        if (charset == null)
            throw new IllegalArgumentException("charset==null");

        List<String> lines = readAllLinesFromDirlist(dirlist, charset);

        List<Task> tasks = new ArrayList<>();

        for (int i = 0; i < lines.size(); i++) {
            tasks.add(createTaskFromSource(getCheckedFileFromLine(lines.get(i), dirlist, i + 1), i + 1));
        }

        return tasks;
    }

    private static File getCheckedFileFromLine(String lineString, File directoryList, int line) throws SourceFileException {
        File dir = new File(lineString);
        try {
            File result;
            if (!dir.isAbsolute()) {
                result = new File(FileUtils.getExistedParentFile(directoryList), lineString).getAbsoluteFile();
            } else {
                result = dir;
            }

            FileUtils.checkExists(result);
            FileUtils.checkType(result, WrongFileTypeException.Type.FOLDER);
            return result;

        } catch (Exception e) {
            throw new SourceFileException(e, dir, line);
        }
    }

    private static Task createTaskFromSource(File directory, int line) throws SourceFileException {
        try {
            return createTask(documentArgument,
                    pageArgument,
                    importSortedImagesFiles(directory),
                    new File(formatter.format(directory)).getAbsoluteFile());
        } catch (Exception e) {
            throw new SourceFileException(e, directory, line);
        }
    }

    private static List<String> readAllLinesFromDirlist(File dirlist, Charset charset) throws DirListException {
        try {
            FileUtils.checkExists(dirlist);
            FileUtils.checkType(dirlist, WrongFileTypeException.Type.FILE);
        } catch (Exception e) {
            throw new DirListException(e, dirlist);
        }

        try (BufferedReader reader = Files.newBufferedReader(dirlist.toPath(), charset)) {
            List<String> result = new ArrayList<>();
            for (; ; ) {
                String line = reader.readLine();
                if (line == null || getFixedLine(line) == null)
                    break;
                result.add(getFixedLine(line));
            }
            return result;
        } catch (Exception e) {
            throw new DirListException(e, dirlist);
        }
    }

    private static String getFixedLine(String raw) {
        if (raw == null || raw.isEmpty() || raw.trim().isEmpty())
            return null;
        else {
//            remove BOM header in UTF8 line
            String result = raw.replace("\uFEFF", "");
            return result.trim();
        }
    }


    private static File[] importSortedImagesFiles(File source_directory) throws EmptyImagesException {
        File[] files = source_directory.listFiles(imageFilter);

        if (files == null || files.length == 0)
            throw new EmptyImagesException("No image was found in: " + source_directory);

        Arrays.sort(files, fileSorter);

        return files;
    }

    private static Task createTask(DocumentArgument documentArgument, PageArgument pageArgument, File[] images, File pdf_destination) {
        return new Task(documentArgument, pageArgument, images, pdf_destination);
    }

}