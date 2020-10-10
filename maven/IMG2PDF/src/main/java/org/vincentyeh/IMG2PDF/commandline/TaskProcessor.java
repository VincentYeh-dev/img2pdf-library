package org.vincentyeh.IMG2PDF.commandline;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.vincentyeh.IMG2PDF.file.PDFFile;
import org.vincentyeh.IMG2PDF.task.formatted.FormattedTask;
import org.vincentyeh.IMG2PDF.task.formatted.FormattedTaskList;
import org.xml.sax.SAXException;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 * The Main Program
 * 
 * @author VincentYeh
 *
 */
public class TaskProcessor {
	private ArrayList<String> lists;

	public TaskProcessor(String args) throws IOException, ParserConfigurationException, SAXException {
		this(args.trim().split("\\s"));
	}

	public TaskProcessor(String[] args) throws IOException, ParserConfigurationException, SAXException {
		ArgumentParser parser = createArgParser();
		Arg2Values(parser, args);

		for (String filepath : lists) {
			start(new FormattedTaskList(filepath));
		}
	}

	public TaskProcessor(FormattedTaskList tasks) {
		start(tasks);
	}

	private void start(FormattedTaskList tasks) {
		for (FormattedTask task : tasks) {
			PDFFile pdf = new PDFFile(task);
			try {
//				pdf.setMaxDiff(0.15f);
				pdf.process();

			} catch (IOException e) {
				if(e.getMessage().equals("Unsupported Image Type")) {
					System.err.println("\nUnsupported Image Type\n");
				}
			}
		}
	}

	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
		new TaskProcessor(args);
	}
	
	private ArgumentParser createArgParser() {
		ArgumentParser parser = ArgumentParsers.newFor("IMG2PDF").build().defaultHelp(true)
				.description("Convert or merge image file to PDF");
		parser.addArgument("source").nargs("*").help("File to convert");
		return parser;
	}

	private void Arg2Values(ArgumentParser parser, String[] args) {
		Namespace ns = null;
		try {
			ns = parser.parseArgs(args);
		} catch (ArgumentParserException e) {
			parser.handleError(e);
			System.exit(1);
		}
		lists = new ArrayList<String>(ns.<String>getList("source"));
	}
}
