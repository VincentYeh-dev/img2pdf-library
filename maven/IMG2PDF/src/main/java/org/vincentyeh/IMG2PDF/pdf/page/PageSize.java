package org.vincentyeh.IMG2PDF.pdf.page;

import org.apache.pdfbox.pdmodel.common.PDRectangle;

/**
 * Size is the variable that define size of pages of PDFFile.
 * 
 * @author VincetYeh
 */
public enum PageSize {
	A0(PDRectangle.A0), A1(PDRectangle.A1), A2(PDRectangle.A2), A3(PDRectangle.A3), A4(PDRectangle.A4),
	A5(PDRectangle.A5), A6(PDRectangle.A6), LEGAL(PDRectangle.LEGAL), LETTER(PDRectangle.LETTER), DEPEND_ON_IMG(null);

	/**
	 * This is the constant used to create the PDF.
	 */
	private final PDRectangle pdrectangle;

	PageSize(PDRectangle pdrectangle) {
		this.pdrectangle = pdrectangle;
	}

	/**
	 * Create Size by String
	 * 
	 * @param str The String contain definition of Size.
	 * @return Size
	 */
	public static PageSize getByString(String str) {
		PageSize[] sizes = PageSize.values();
		for (PageSize size : sizes) {
			if (size.toString().equals(str))
				return size;
		}
		throw new IllegalArgumentException("Sortby is invalid");
	}

	public PDRectangle getPdrectangle() {
		return pdrectangle;
	}

	/**
	 * List all item of enum in String.
	 * 
	 * @return The array of enum in String.
	 */
	public static String[] valuesStr() {
		PageSize[] size_list = PageSize.values();
		String[] str_list = new String[size_list.length];
		for (int i = 0; i < str_list.length; i++) {
			str_list[i] = size_list[i].toString();
		}
		return str_list;
	}

}