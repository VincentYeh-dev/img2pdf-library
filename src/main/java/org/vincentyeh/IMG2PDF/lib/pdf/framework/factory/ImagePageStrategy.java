package org.vincentyeh.img2pdf.lib.pdf.framework.factory;

import org.vincentyeh.img2pdf.lib.pdf.framework.objects.PointF;
import org.vincentyeh.img2pdf.lib.pdf.framework.objects.SizeF;
import org.vincentyeh.img2pdf.lib.pdf.parameter.PageArgument;

public interface ImagePageStrategy {

    PointF getImagePosition();

    SizeF getPageSize();

    SizeF getImageSize();

    void execute(PageArgument pageArgument, SizeF imageSize);
}
