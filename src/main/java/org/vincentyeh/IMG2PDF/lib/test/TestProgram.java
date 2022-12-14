package org.vincentyeh.img2pdf.lib.test;


import org.apache.pdfbox.io.MemoryUsageSetting;
import org.vincentyeh.img2pdf.lib.image.ImageReaderFacade;
import org.vincentyeh.img2pdf.lib.image.reader.framework.ColorType;
import org.vincentyeh.img2pdf.lib.image.reader.framework.ImageReader;
import org.vincentyeh.img2pdf.lib.pdf.concrete.builder.PDFBoxBuilder;
import org.vincentyeh.img2pdf.lib.pdf.concrete.factory.ImagePDFFactory;
import org.vincentyeh.img2pdf.lib.pdf.concrete.factory.StandardImagePageCalculationStrategy;
import org.vincentyeh.img2pdf.lib.pdf.parameter.DocumentArgument;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PageArgument;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PageSize;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class TestProgram {

    public static void main(String[] args) throws IOException {
        File tempFolder = Files.createTempDirectory("org.vincentyeh.img2pdf-lib.tmp.").toFile();
        tempFolder.deleteOnExit();
        ImagePDFFactory factory = createImagePDFFactory(
                new PageArgument(PageSize.A4),
                new DocumentArgument("1234", "5678")
                , 3 * 1024 * 1024, tempFolder, true
        );


        File[] files = new File("test").listFiles();
        factory.start(files, new File("output2.pdf"), listener);
    }

    private static final ImagePDFFactory.Listener listener = new ImagePDFFactory.Listener() {
        @Override
        public void initializing(long procedure_id) {
            System.out.println("initializing:" + procedure_id);
        }

        @Override
        public void onSaved(long procedure_id, File destination) {
            System.out.println("onSaved:" + procedure_id + "\tDest:" + destination);
        }

        @Override
        public void onConversionComplete(long procedure_id) {
            System.out.println("onConversionComplete:" + procedure_id);
        }

        @Override
        public void onAppend(long procedure_id, int index, int total) {
            System.out.println("onAppend:" + procedure_id + "\t Page:" + index);
        }
    };

    public static ImagePDFFactory createImagePDFFactory(PageArgument pageArgument, DocumentArgument documentArgument,
                                                        long bytes_count, File tempFolder, boolean overwrite_output
    ) {
        MemoryUsageSetting setting = MemoryUsageSetting.setupMixed(bytes_count).setTempDir(tempFolder);
        ImageReader reader = ImageReaderFacade.getImageReader(ColorType.sRGB);

        return new ImagePDFFactory(pageArgument, documentArgument, new PDFBoxBuilder(setting),
                reader, new StandardImagePageCalculationStrategy(),
                overwrite_output);
    }

}
