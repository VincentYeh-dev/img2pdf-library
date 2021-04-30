package org.vincentyeh.IMG2PDF.pdf.page;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.vincentyeh.IMG2PDF.pdf.page.core.Position;
import org.vincentyeh.IMG2PDF.pdf.page.core.PositionCalculator;
import org.vincentyeh.IMG2PDF.pdf.page.core.Size;
import org.vincentyeh.IMG2PDF.pdf.page.core.SizeCalculator;

import static org.vincentyeh.IMG2PDF.pdf.page.PageDirection.Landscape;
import static org.vincentyeh.IMG2PDF.pdf.page.PageDirection.Portrait;

/**
 * Page that contain image in PDF File.
 *
 * @author VincentYeh
 */
public class ImagePageFactory {

    public static PDPage getImagePage(PDDocument document, PageArgument argument, BufferedImage rawImage) throws Exception {
        final PDPage page = new PDPage();

        final Size page_size;
        if (argument.getSize() == PageSize.DEPEND_ON_IMG) {
            page_size = new Size(rawImage.getHeight(), rawImage.getWidth());

        } else {
            PDRectangle rect = argument.getSize().getPdrectangle();
            page_size = new Size(rect.getHeight(), rect.getWidth());
        }

        page.setMediaBox(new PDRectangle(page_size.getWidth(), page_size.getHeight()));

        final Size img_size;
        final Position position;
        final BufferedImage imageOut;

        if (argument.getSize() == PageSize.DEPEND_ON_IMG) {
            img_size = new Size(rawImage.getHeight(), rawImage.getWidth());
            imageOut = rawImage;
            position = new Position(0, 0);
        } else {
            PageDirection page_direction = argument.getDirection();
            if (argument.getAutoRotate()) {
                page_direction = getDirection(rawImage);
            }

            page.setRotation(page_direction == Landscape ? -90 : 0);
            imageOut = rotateImg(rawImage, page_direction == Landscape ? 90 : 0);

            Size rotated_img_size = new Size(imageOut.getHeight(), imageOut.getWidth());
            img_size = SizeCalculator.getInstance().scaleUpToMax(rotated_img_size,page_size);

            PositionCalculator positionCalculator=PositionCalculator.getInstance();
            PositionCalculator.init(page.getRotation() != 0, img_size.getHeight(), img_size.getWidth(), page_size.getHeight(), page_size.getWidth());
            position = positionCalculator.calculate(argument.getAlign());
        }

        PDImageXObject pdImageXObject = LosslessFactory.createFromImage(document, imageOut);
        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        contentStream.drawImage(pdImageXObject, position.getX(), position.getY(), img_size.getWidth(), img_size.getHeight());
        contentStream.close();
        return page;
    }

    public static BufferedImage rotateImg(BufferedImage raw, int rotate_angle) {
        if (rotate_angle == 0) return raw;
        final double rads = Math.toRadians(rotate_angle);
        final double sin = Math.abs(Math.sin(rads));
        final double cos = Math.abs(Math.cos(rads));
        final int w = (int) Math.floor(raw.getWidth() * cos + raw.getHeight() * sin);
        final int h = (int) Math.floor(raw.getHeight() * cos + raw.getWidth() * sin);
        final BufferedImage rotatedImage = new BufferedImage(w, h, raw.getType());
        final AffineTransform at = new AffineTransform();
        at.translate(w / 2., h / 2.);
        at.rotate(rads, 0, 0);
        at.translate(-raw.getWidth() / 2., -raw.getHeight() / 2.);
        AffineTransformOp rotateOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        rotateOp.filter(raw, rotatedImage);
        return rotatedImage;
    }


    private static PageDirection getDirection(BufferedImage image) {
        return getDirection(image.getHeight(), image.getWidth());
    }

    private static PageDirection getDirection(float height, float width) {
        return height / width > 1 ? Portrait : Landscape;
    }
}